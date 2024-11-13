package com.app.ai_di.fragment;

import static com.app.ai_di.helper.Constant.SUCCESS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import com.app.ai_di.Adapter.JobPlanAdapter;
import com.app.ai_di.R;
import com.app.ai_di.activities.MainActivity;
import com.app.ai_di.helper.ApiConfig;
import com.app.ai_di.helper.Constant;
import com.app.ai_di.helper.Session;
import com.app.ai_di.model.DemoCodeData;
import com.app.ai_di.model.PlanListModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.zoho.salesiqembed.ZohoSalesIQ;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    Session session;
    Activity activity;

    MaterialButton btCreate, btnSyncNow;
    EditText edSchoolName;
    EditText edStudentName;
    EditText edRollNumber;
    EditText dobEditBox1, dobEditBox2, dobEditBox3, dobEditBox4, dobEditBox5, dobEditBox6, dobEditBox7, dobEditBox8;
    CircularProgressIndicator cbCodes;
    TextView tvCodes, tvEarningWallet, tvTodayCodes, tvTotalCodes, tvWorkingDays, tvPlanName;
    TextView tvSchoolName, tvStudentName, tvRollNumber, tvDOB;
    LinearLayout llWaiting;
    RelativeLayout rlSelectPlan;
    NestedScrollView frame;
    Spinner dropdownSpinner;

    int codeCount = 0;
    final int MAX_CODE_COUNT = 2;
    String isPlanSelected;

    List<DemoCodeData> demoList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        activity = getActivity();
        session = new Session(activity);

        llWaiting = root.findViewById(R.id.llWaiting);
        frame = root.findViewById(R.id.frame);
        btCreate = root.findViewById(R.id.btCreate);
        btnSyncNow = root.findViewById(R.id.btnSyncNow);
        edSchoolName = root.findViewById(R.id.edSchoolName);
        edStudentName = root.findViewById(R.id.edStudentName);
        edRollNumber = root.findViewById(R.id.edRollNumber);
        cbCodes = root.findViewById(R.id.cbCodes);
        tvCodes = root.findViewById(R.id.tvCodes);
        tvEarningWallet = root.findViewById(R.id.tvEarningWallet);
        tvTodayCodes = root.findViewById(R.id.tvTodayCodes);
        tvTotalCodes = root.findViewById(R.id.tvTotalCodes);
        tvWorkingDays = root.findViewById(R.id.tvWorkingDays);
        tvSchoolName = root.findViewById(R.id.tvSchoolName);
        tvStudentName = root.findViewById(R.id.tvStudentName);
        tvRollNumber = root.findViewById(R.id.tvRollNumber);
        tvDOB = root.findViewById(R.id.tvDOB);
//        dropdownSpinner = root.findViewById(R.id.dropdownSpinner);
        rlSelectPlan = root.findViewById(R.id.rlSelectPlan);
        tvPlanName = root.findViewById(R.id.tvPlanName);

        // Initialize DOB EditTexts
        dobEditBox1 = root.findViewById(R.id.dob_edit_box1);
        dobEditBox2 = root.findViewById(R.id.dob_edit_box2);
        dobEditBox3 = root.findViewById(R.id.dob_edit_box3);
        dobEditBox4 = root.findViewById(R.id.dob_edit_box4);
        dobEditBox5 = root.findViewById(R.id.dob_edit_box5);
        dobEditBox6 = root.findViewById(R.id.dob_edit_box6);
        dobEditBox7 = root.findViewById(R.id.dob_edit_box7);
        dobEditBox8 = root.findViewById(R.id.dob_edit_box8);

        llWaiting.setVisibility(View.VISIBLE);
        frame.setVisibility(View.GONE);

        fetchSessionDataAndInitialize();
//        initialCodeCount();
        initializeDemoList();
        setDemoData();
        setDobEdit();
//        setBtCreate();
        setBtSyncNow();

        return root;
    }

    private void planName() {
        String startWorkPlanName = session.getData(Constant.START_WORK_PLAN_NAME);
        Log.d("START_WORK", "START_WORK startWorkPlanName: " + startWorkPlanName);

        if (startWorkPlanName.isEmpty()) {
            rlSelectPlan.setVisibility(View.GONE);
        } else {
            rlSelectPlan.setVisibility(View.VISIBLE);
            tvPlanName.setText(startWorkPlanName);
        }
    }

//    private void dropDown() {
//        try{
//            List<PlanListModel> plans = session.getPlanData();
//            List<String> planNames = new ArrayList<>();
//
//            Log.d("Plan", "Plan Plan: " + planNames);
//
//            if (plans != null && !plans.isEmpty()) {
//                for (PlanListModel plan : plans) {
//                    planNames.add(plan.getName());
//                    Log.d("Plan", "Plan Name: " + plan.getName());
//                    Log.d("Plan", "Plan Description: " + plan.getDescription());
//                }
//                // Create custom adapter to disable certain items
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, planNames) {
//                    @Override
//                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
//                        View view = super.getDropDownView(position, convertView, parent);
//
//                        if (codeCount == 0) {
//                            view.setEnabled(true); // Enable the rest of the items
//                            view.setAlpha(1f); // Normal appearance for enabled items
//                        } else if (codeCount > 0) {
//                            view.setEnabled(false); // Disable it
//                            view.setAlpha(0.5f); // Optionally make it appear dimmed
//                        }
//                        return view;
//                    }
//                };
//
//                // Create ArrayAdapter with plan names
////            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, planNames);
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                dropdownSpinner.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
//
//                dropdownSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                        assert plans != null;
//                        String selectedPlanId = plans.get(position).getId();
//                        Log.d("Plan", "Plan selectedPlanId: " + selectedPlanId);
//                        session.setData("selected_plan_id", selectedPlanId);
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//                        // Optional: Handle no selection
//                    }
//                });
//
//            } else {
//                Log.d("Plan", "Plan empty");
//                rlSelectPlan.setVisibility(View.GONE);
//            }
//        } catch (Exception e) {
//            Log.d("Plan", "Plan Error: " + e.getMessage());
//        }
//    }

    private void initializeDemoList() {
        demoList = new ArrayList<>(Arrays.asList(
                new DemoCodeData("1", "AAKASH PU COLLEGE", "KAJAL CHAVAN", "2034", "2009-01-01"),
                new DemoCodeData("2", "AARALGOUDAR PU COLLEGE", "AKSHATA VIJAY B", "2088", "2009-01-02"),
                new DemoCodeData("3", "AB JATTI COMP PU COLLEGE", "KAVERI PAWAR", "2151", "2009-01-03"),
                new DemoCodeData("4", "ABC GIRLS PU COLLEGE", "RENUKA ANAWAL", "5467", "2009-01-04"),
                new DemoCodeData("5", "ABU INDP PU COLLEGE", "MALAN MULLA", "5542", "2009-01-05"),
                new DemoCodeData("6", "ABULKALAM AZAD PU COLLEGE", "SANGITA BASAVAR", "5553", "2009-01-06"),
                new DemoCodeData("7", "ABULKALAM AZAD PU COLLEGE", "ANAND PATIL", "5583", "2009-01-07"),
                new DemoCodeData("8", "ACHARYA PU COLLEGE", "BALU DURGAPPA W", "6843", "2009-01-08"),
                new DemoCodeData("9", "ADARSH COMP PU COLLEGE", "SHRIMANTH CHALA", "6851", "2009-01-09"),
                new DemoCodeData("10", "ADARSH PU COLLEGE", "LALEMASHAK ISMA", "6882", "2009-01-10"),
                new DemoCodeData("11", "ADARSHA COMP PU COLLEGE", "SHRUTI YALLAPPA", "6898", "2009-01-11"),
                new DemoCodeData("12", "ADARSHA INDP PU COLLEGE", "BIRAPPA NAGAPPA", "6914", "2009-01-12"),
                new DemoCodeData("13", "ADARSHA PU COLLEGE", "KAVERI YALLAPPA", "6923", "2009-01-13"),
                new DemoCodeData("14", "ADARSHA PU COLLEGE", "MARUTI MAHADEV ", "6937", "2009-01-14"),
                new DemoCodeData("15", "ADEPT INDP PU COLLEGE", "POOJA SURESH BA", "6943", "2009-01-15"),
                new DemoCodeData("16", "ADITYA PU COLLEGE", "ROOPA VITHAL JA", "7018", "2009-01-16"),
                new DemoCodeData("17", "ADITYAS PU COLLEGE", "SHRINIVAS BASAV", "7041", "2009-01-17"),
                new DemoCodeData("18", "ADVC INDP PU COLLEGE", "PRADEEP ASHOK U", "7089", "2009-01-18"),
                new DemoCodeData("19", "ADVITYA PU COLLEGE", "NETRAVATI SHIVS", "7104", "2009-01-19"),
                new DemoCodeData("20", "AES NATIONAL PU COLLEGE", "VIKAS SHIVAPPA ", "7118", "2009-01-20"),
                new DemoCodeData("21", "AFTAB INDP PU COLLEGE", "SAGAR SHIRAKANA", "7172", "2009-01-21"),
                new DemoCodeData("22", "AJEET ACADEMY PU COLLEGE", "VINOD NYAMANNAV", "13533", "2009-01-22"),
                new DemoCodeData("23", "AK NANDI COMP PU COLLEGE", "SOMAYYA CHARANT", "13547", "2009-01-23"),
                new DemoCodeData("24", "AK SIRSAGI PU COLLEGE", "MUTTURAJ KUMBAR", "13558", "2009-01-24"),
                new DemoCodeData("25", "AKASH PU COLLEGE", "SHIVANAND KAVAD", "13645", "2009-01-25"),
                new DemoCodeData("26", "AKSHAMALA PU COLLEGE", "PRASANNAKUMAR M", "13661", "2009-01-26"),
                new DemoCodeData("27", "AKSHARA COMP PU COLLEGE", "YALLALING BAIRA", "13807", "2009-01-27"),
                new DemoCodeData("28", "AKSHARA PU COLLEGE", "NAGAPPA AMBALAN", "16386", "2009-01-28"),
                new DemoCodeData("29", "AL AMEEN PU COLLEGE", "BHEEMAPPA AMBAL", "16395", "2009-01-29"),
                new DemoCodeData("30", "AL BADRIA PU COLLEGE", "KAVERI OUDANEPP", "16568", "2009-01-30"),
                new DemoCodeData("31", "AL FALAH PU COLLEGE", "HEMANTH KUMAR G", "20701", "2009-01-31"),
                new DemoCodeData("32", "AL-HILAL GIRLS PU COLLEGE", "SRINIVASA A P", "20786", "2009-02-01"),
                new DemoCodeData("33", "ALMIGHTY PU COLLEGE", "GOWTHAM R", "21002", "2009-02-02"),
                new DemoCodeData("34", "ALVA'S PU COLLEGE", "SWETHA S", "22928", "2009-02-03"),
                new DemoCodeData("35", "AMARESHWAR PU COLLEGE", "SUNIL T", "23904", "2009-02-04"),
                new DemoCodeData("36", "AMB COMP PU COLLEGE", "IKANABAI LAMANI", "26594", "2009-02-05"),
                new DemoCodeData("37", "AMBEDKAR IND PU COLLEGE", "ABHISHEK TIPPAN", "26716", "2009-02-06"),
                new DemoCodeData("38", "AMBEDKAR INDP PU COLLEGE", "VENKATESH RATHO", "26727", "2009-02-07"),
                new DemoCodeData("39", "AMBIKA PU COLLEGE", "MAHEBOOB MASHYA", "26762", "2009-02-08"),
                new DemoCodeData("40", "AMC AMERICAN PU COLLEGE", "AKASH SADASHIVA", "26797", "2009-02-09"),
                new DemoCodeData("41", "AMEENABI GIRLS PU COLLEGE", "POOJA WADED", "26844", "2009-02-10"),
                new DemoCodeData("42", "AMMAJESHWARI PU COLLEGE", "SAVITA SIDDAYYA", "26888", "2009-02-11"),
                new DemoCodeData("43", "AMOGH COMP PU COLLEGE", "LAXMI SHIKALAWA", "26919", "2009-02-12"),
                new DemoCodeData("44", "AMS COMP PU COLLEGE", "SACHINKUMARA G", "37568", "2009-02-13"),
                new DemoCodeData("45", "ANAND ASHRAM PU COLLEGE", "AISHWARYA H", "37609", "2009-02-14"),
                new DemoCodeData("46", "ANAND COMP PU COLLEGE", "PREETHI M", "37622", "2009-02-15"),
                new DemoCodeData("47", "ANANDA COMP PU COLLEGE", "PAVANKUMAR C", "37644", "2009-02-16"),
                new DemoCodeData("48", "ANANDATHIRTHA PU COLLEGE", "SNEHA H S", "37655", "2009-02-17"),
                new DemoCodeData("49", "ANANTHA PU COLLEGE", "SAMIULLA", "37666", "2009-02-18"),
                new DemoCodeData("50", "ANGLO-URDU PU COLLEGE", "DEEKSHITHA G K", "37698", "2009-02-19"),
                new DemoCodeData("51", "ANIKETHANA PU COLLEGE", "LAXMI HIREKURAB", "39417", "2009-02-20"),
                new DemoCodeData("52", "ANITA M NERLI PU COLLEGE", "MOHAMMED YASIN", "42153", "2009-02-21"),
                new DemoCodeData("53", "ANJANADRI PU COLLEGE", "MANOJ SINGH V", "42175", "2009-02-22"),
                new DemoCodeData("54", "ANJANEYASWAMY PU COLLEGE", "SUNEEL", "48579", "2009-02-23"),
                new DemoCodeData("55", "ANJUM COMP PU COLLEGE", "SUREKHA MOKASHI", "49022", "2009-02-24"),
                new DemoCodeData("56", "ANJUMAN BOYS PU COLLEGE", "SHARANBASU SHIR", "49043", "2009-02-25"),
                new DemoCodeData("57", "ANJUMAN COMP PU COLLEGE", "POOJA GUJAGOND", "49085", "2009-02-26"),
                new DemoCodeData("58", "ANJUMAN GIRLS PU COLLEGE", "NEETA KOLKAR", "49142", "2009-02-27"),
                new DemoCodeData("59", "ANJUMAN INDP PU COLLEGE", "ARUN AVARASANG", "49238", "2009-02-28"),
                new DemoCodeData("60", "ANJUMAN ISLAM PU COLLEGE", "ADARSH PAWAR", "49295", "2009-03-01"),
                new DemoCodeData("61", "ANJUMAN P U COLLEGE", "ADITYA PAWAR", "49306", "2009-03-02"),
                new DemoCodeData("62", "ANJUMAN PU COLLEGE", "SACHIN HUGAR", "52685", "2009-03-03"),
                new DemoCodeData("63", "ANJUMAN PU COLLEGE", "PRAVEEN DENGI", "53187", "2009-03-04"),
                new DemoCodeData("64", "ANNADANESHWAR PU COLLEGE", "AKASHA ASANGI", "53241", "2009-03-05"),
                new DemoCodeData("65", "ANUGRAHA PU COLLEGE", "SANGAYYA GANACH", "53313", "2009-03-06"),
                new DemoCodeData("66", "ANUPAMA PU COLLEGE", "ANIL CHANAGOND", "53335", "2009-03-07"),
                new DemoCodeData("67", "APS PU COLLEGE", "LAKSHMI D P", "54319", "2009-03-08"),
                new DemoCodeData("68", "APS PU COLLEGE", "GAGAN K", "57138", "2009-03-09"),
                new DemoCodeData("69", "ARADHANA PU COLLEGE", "ASHWINI T B", "57166", "2009-03-10"),
                new DemoCodeData("70", "ARAVIND P U COLLEGE", "BHAVANA H P", "57178", "2009-03-11"),
                new DemoCodeData("71", "ARIHANT GIRLS PU COLLEGE", "TEJASWINI K", "57227", "2009-03-12"),
                new DemoCodeData("72", "ARIHANT PU COLLEGE", "TEJASHREE M", "57399", "2009-03-13"),
                new DemoCodeData("73", "ARS COMP PU COLLEGE", "VICKY DEVADIGA", "58141", "2009-03-14"),
                new DemoCodeData("74", "ARUNA PU COLLEGE", "BHAVANA P", "58205", "2009-03-15"),
                new DemoCodeData("75", "ARUNDHATI INDP PU COLLEGE", "KALPANA SIDDALI", "64379", "2009-03-16"),
                new DemoCodeData("76", "ARUNODAYA IND PU COLLEGE", "RADHIKA MANTESH", "64395", "2009-03-17"),
                new DemoCodeData("77", "ARUNODAYA INDP PU COLLEGE", "MALLIKARJUN SID", "64403", "2009-03-18"),
                new DemoCodeData("78", "ARUNODAYA PU COLLEGE", "SUJATA SURESH N", "64437", "2009-03-19"),
                new DemoCodeData("79", "ARYABHATA PU COLLEGE", "MALLIKARJUN RUD", "64453", "2009-03-20"),
                new DemoCodeData("80", "ARYAVIDYASHALA PU COLLEGE", "BHAGIRATI NINGA", "64476", "2009-03-21"),
                new DemoCodeData("81", "AS PATIL COMM PU COLLEGE", "KIRANKUMAR G B", "65851", "2009-03-22"),
                new DemoCodeData("82", "ASC PU COLLEGE", "RAVICHANDRA G P", "65916", "2009-03-23"),
                new DemoCodeData("83", "ASCENT PU COLLEGE", "PRASHANTHA G T", "65927", "2009-03-24"),
                new DemoCodeData("84", "ASGNM PU COLLEGE", "SANTHOSH KUMAR ", "65938", "2009-03-25"),
                new DemoCodeData("85", "ASHOK COMP PU COLLEGE", "HONNURSWAMY G S", "65963", "2009-03-26"),
                new DemoCodeData("86", "ASS PU COLLEGE", "MADHURI H M", "70989", "2009-03-27"),
                new DemoCodeData("87", "ATHANI COMP PU COLLEGE", "HONNURASWAMY M", "70995", "2009-03-28"),
                new DemoCodeData("88", "ATHANI COMP PU COLLEGE", "VASANTHKUMAR H ", "71057", "2009-03-29"),
                new DemoCodeData("89", "ATHMASHRI COMP PU COLLEGE", "LAXMI POL", "74964", "2009-03-30"),
                new DemoCodeData("90", "AUROBINDO COMP PU COLLEGE", "SHILPA BALABATT", "75009", "2009-03-31"),
                new DemoCodeData("91", "AVK COLLEGE", "LAXMI YARANTELI", "75018", "2009-04-01"),
                new DemoCodeData("92", "AVK PU COLLEGE", "CHAITRA SHARANA", "75027", "2009-04-02"),
                new DemoCodeData("93", "AVNR INDP PU COLLEGE", "CHANDANA D", "78687", "2009-04-03"),
                new DemoCodeData("94", "AYSHA PU COLLEGE", "MOHAMMED AMAN", "78816", "2009-04-04"),
                new DemoCodeData("95", "AZAD GIRLS PU COLLEGE", "CHANDU K M", "78858", "2009-04-05"),
                new DemoCodeData("96", "B BADARLI COMP PU COLLEGE", "ALFIYATASKEEN M", "79058", "2009-04-06"),
                new DemoCodeData("97", "B LINGAIAH RES PU COLLEGE", "MUBEENA NADAF", "79183", "2009-04-07"),
                new DemoCodeData("98", "B SHANKARANAND PU COLLEGE", "NARESHA P", "79387", "2009-04-08"),
                new DemoCodeData("99", "BABURAO THAKUR PU COLLEGE", "SHASHIKUMARA H", "79864", "2009-04-09"),
                new DemoCodeData("100", "BADRIA PU COLLEGE", "TEJASHREE J N", "79874", "2009-04-10"),
                new DemoCodeData("101", "BAHIMANI GIRLS PU COLLEGE", "ANUSHA H P", "79924", "2009-04-11"),
                new DemoCodeData("102", "BAJSS INDP PU COLLEGE", "OBALESHA GO", "79958", "2009-04-12"),
                new DemoCodeData("103", "BAJSS PU COLLEGE", "MAMATHA N", "80001", "2009-04-13"),
                new DemoCodeData("104", "BALAHANUMAPPA PU COLLEGE", "SHWETHA M", "80027", "2009-04-14"),
                new DemoCodeData("105", "BALDWIN GIRLS PU COLLEGE", "VIJAYA KUMARA M", "80066", "2009-04-15"),
                new DemoCodeData("106", "BALDWIN PU COLLEGE", "DARSHAN KUMARA ", "80092", "2009-04-16"),
                new DemoCodeData("107", "BANAJAWADA RES PU COLLEGE", "KAVERI BIRADAR", "82949", "2009-04-17"),
                new DemoCodeData("108", "BANASIRI LIONS PU COLLEGE", "KEERTI NAYAK", "82984", "2009-04-18"),
                new DemoCodeData("109", "BANGALORE CITY PU COLLEGE", "CHANDRASHEKHAR ", "83047", "2009-04-19"),
                new DemoCodeData("110", "BANGALORE INDP PU COLLEGE", "PRAVEEN RATNAKA", "83308", "2009-04-20"),
                new DemoCodeData("111", "BANJARA IND PU COLLEGE", "SEEMA PAWAR", "85218", "2009-04-21"),
                new DemoCodeData("112", "BAPU COMP PU COLLEGE", "AKSHAY RATHOD", "85221", "2009-04-22"),
                new DemoCodeData("113", "BAPUJI COMP PU COLLEGE", "MITHUN RATHOD", "85265", "2009-04-23"),
                new DemoCodeData("114", "BAPUJI GVS PU COLLEGE", "SHITAL RATHOD", "85309", "2009-04-24"),
                new DemoCodeData("115", "BAPUJI INDP PU COLLEGE", "POOJA PAWAR", "85311", "2009-04-25"),
                new DemoCodeData("116", "BAPUJI PU COLLEGE", "SHILPA PAWAR", "85322", "2009-04-26"),
                new DemoCodeData("117", "BARACK PU COLLEGE", "SACHIN CHAVHAN", "85378", "2009-04-27"),
                new DemoCodeData("118", "BASAPRABHUKORE PU COLLEGE", "JYOTI PAWAR", "85402", "2009-04-28"),
                new DemoCodeData("119", "BASAVA GIRLS PU COLLEGE", "SUREKHA PAWAR", "85427", "2009-04-29"),
                new DemoCodeData("120", "BASAVA INDP PU COLLEGE", "SAVITRI RAMESH ", "87518", "2009-04-30"),
                new DemoCodeData("121", "BASAVA PU COLLEGE", "YOGESHA MUKINDA", "87677", "2009-05-01"),
                new DemoCodeData("122", "BASAVAJYOTHI PU COLLEGE", "MANJUNATH GANAP", "88973", "2009-05-02"),
                new DemoCodeData("123", "BASAVAKALYANA PU COLLEGE", "SHIVANAND SIDDA", "89136", "2009-05-03"),
                new DemoCodeData("124", "BASAVARADDI PU COLLEGE", "BHUVANESHWRI BA", "89259", "2009-05-04"),
                new DemoCodeData("125", "BASAVESHWAR PU COLLEGE", "RAJESHWARI BASA", "89261", "2009-05-05"),
                new DemoCodeData("126", "BASAVESHWARA PU COLLEGE", "NEHA HUCCHAPPA ", "89392", "2009-05-06"),
                new DemoCodeData("127", "BASAVESWARA PU COLLEGE", "SUREKHA RAMACHA", "89425", "2009-05-07"),
                new DemoCodeData("128", "BASE PU COLLEGE", "DEEPA BASAVANTA", "89449", "2009-05-08"),
                new DemoCodeData("129", "BASEL MISSION PU COLLEGE", "PRIYA PANDURANG", "89451", "2009-05-09"),
                new DemoCodeData("130", "BBMP PU COLLEGE", "PARASHURAM SIND", "90782", "2009-05-10"),
                new DemoCodeData("131", "BBMP PU COLLEGE", "AMBIKA R L", "92863", "2009-05-11"),
                new DemoCodeData("132", "BBS AMBEDKAR PU COLLEGE", "CHAMARAJ YAMANA", "94208", "2009-05-12"),
                new DemoCodeData("133", "BCN PU COLLEGE", "ARUN PAWAR", "96071", "2009-05-13"),
                new DemoCodeData("134", "BCT LBK PU COLLEGE", "SHRIKANT CHAVHA", "96082", "2009-05-14"),
                new DemoCodeData("135", "BEENA VAIDYA PU COLLEGE", "MITHUN CHAVHAN", "96104", "2009-05-15"),
                new DemoCodeData("136", "BEL COMP PU COLLEGE", "RAHUL CHAVHAN", "96138", "2009-05-16"),
                new DemoCodeData("137", "BEM COMP PU COLLEGE", "SANGAMMA ODI", "96177", "2009-05-17"),
                new DemoCodeData("138", "BENAKA PU COLLEGE", "RAKSHITA ALUR", "96268", "2009-05-18"),
                new DemoCodeData("139", "BES EVENING COLLEGE", "YASHODA LAMANI", "96281", "2009-05-19"),
                new DemoCodeData("140", "BES MODEL PU COLLEGE", "MITHUN LAMANI", "96314", "2009-05-20"),
                new DemoCodeData("141", "BES PU COLLEGE", "HANAMANT DALAWA", "102557", "2009-05-21"),
                new DemoCodeData("142", "BEST COM & SCI PU COLLEGE", "RAVI ANNAPPA HA", "102608", "2009-05-22"),
                new DemoCodeData("143", "BEST PU COLLEGE", "NARASIMH SALUNK", "102616", "2009-05-23"),
                new DemoCodeData("144", "BET PU COLLEGE", "REVANASIDDA MET", "102635", "2009-05-24"),
                new DemoCodeData("145", "BETHANY PU COLLEGE", "MUJEEF BAGAWAN", "102672", "2009-05-25"),
                new DemoCodeData("146", "BETHEL GIRLS PU COLLEGE", "SHANKAR GUDADIN", "102703", "2009-05-26"),
                new DemoCodeData("147", "BG BYAKOD COMP PU COLLEGE", "BADASHA BAGAWAN", "102724", "2009-05-27"),
                new DemoCodeData("148", "BGS COMP PU COLLEGE", "SUNIL GONAL", "102756", "2009-05-28"),
                new DemoCodeData("149", "BGS PU COLLEGE", "SHABANA MAKANDA", "102762", "2009-05-29"),
                new DemoCodeData("150", "BGS SC PU COLLEGE", "NAVYASHREE D", "106391", "2009-05-30"),
                new DemoCodeData("151", "BGS SCIENCE PU COLLEGE", "DASTHANI KAVYA", "112386", "2009-05-31"),
                new DemoCodeData("152", "BHAGAT PU COLLEGE", "BASAVARAJA DAST", "112492", "2009-06-01"),
                new DemoCodeData("153", "BHAGAWANBUDDHA PU COLLEGE", "M OBAPPA", "112753", "2009-06-02"),
                new DemoCodeData("154", "BHANDARKAR'S PU COLLEGE", "N SUDEEPA", "112874", "2009-06-03"),
                new DemoCodeData("155", "BHARATESH COMP PU COLLEGE", "NILUVANJI SHIVA", "113208", "2009-06-04"),
                new DemoCodeData("156", "BHARATH PU COLLEGE", "CHETHAN B", "113254", "2009-06-05"),
                new DemoCodeData("157", "BHARATHESH PU COLLEGE", "S RANJITHA", "113303", "2009-06-06"),
                new DemoCodeData("158", "BHARATHI PU COLLEGE", "ANANDA H M", "113521", "2009-06-07"),
                new DemoCodeData("159", "BHARATHMATHA PU COLLEGE", "REKHA BASAVARAJ", "114653", "2009-06-08"),
                new DemoCodeData("160", "BHARATHY COMP PU COLLEGE", "DANAMMA CHANDRA", "114695", "2009-06-09"),
                new DemoCodeData("161", "BHOGESWAR COMP PU COLLEGE", "GOPIKA MUTTANAG", "114819", "2009-06-10"),
                new DemoCodeData("162", "BISHOP COTTON PU COLLEGE", "CHANDAN KUMAR S", "114875", "2009-06-11"),
                new DemoCodeData("163", "BISHOP SARGANT PU COLLEGE", "SHIVANANDA N", "114938", "2009-06-12"),
                new DemoCodeData("164", "BK GUDADINNI PU COLLEGE", "SHAHIN KOUSAR R", "114969", "2009-06-13"),
                new DemoCodeData("165", "BK GUPTA COMP PU COLLEGE", "RAVI SHEKAPPA B", "114971", "2009-06-14"),
                new DemoCodeData("166", "BKG PU COLLEGE", "SHREERAKSHA SHR", "114992", "2009-06-15"),
                new DemoCodeData("167", "BLDEA'S NEW PU COLLEGE", "HIMARASHMI S", "115024", "2009-06-16"),
                new DemoCodeData("168", "BLOOMS PU COLLEGE", "MUTTU HANAMANTH", "115086", "2009-06-17"),
                new DemoCodeData("169", "ASS PU COLLEGE", "PRUTHVISHREE S", "115119", "2009-06-18"),
                new DemoCodeData("170", "ASS PU COLLEGE", "KARUNAKARA M", "115173", "2009-06-19"),
                new DemoCodeData("171", "BNM PU COLLEGE", "DADAINAVARA NAG", "115242", "2009-06-20"),
                new DemoCodeData("172", "BNRK PU COLLEGE", "SNEHA R", "115568", "2009-06-21"),
                new DemoCodeData("173", "AVK PU COLLEGE", "MANJUNATH CHAND", "115801", "2009-06-22"),
                new DemoCodeData("174", "BR AMBEDKAR PU COLLEGE", "JEEVAN S", "116109", "2009-06-23"),
                new DemoCodeData("175", "BRIGHT WAY PU COLLEGE", "CHANDAN N R", "116298", "2009-06-24"),
                new DemoCodeData("176", "BRIGHTWAY PU COLLEGE", "ESHWARI C", "116364", "2009-06-25"),
                new DemoCodeData("177", "BRILLIANT PU COLLEGE", "NITHIN R", "116511", "2009-06-26"),
                new DemoCodeData("178", "BRINDAVAN INDP PU COLLEGE", "SOUMYA BHEEMANN", "116878", "2009-06-27"),
                new DemoCodeData("179", "BS COMP PU COLLEGE", "BHAGYASHREE IRA", "116929", "2009-06-28"),
                new DemoCodeData("180", "BS PAWAR IND PU COLLEGE", "SUBRAMANYAM N", "117384", "2009-06-29"),
                new DemoCodeData("181", "AYSHA PU COLLEGE", "THRUPTHI R", "117695", "2009-06-30"),
                new DemoCodeData("182", "AYSHA PU COLLEGE", "KAVITA JADAR", "119087", "2009-07-01"),
                new DemoCodeData("183", "BT PATIL COMP PU COLLEGE", "POOJA GUDIHAL", "119121", "2009-07-02"),
                new DemoCodeData("184", "BTC GOWRAMMA PU COLLEGE", "SADIYA BANU", "121588", "2009-07-03"),
                new DemoCodeData("185", "BBMP PU COLLEGE", "YAMMI BASAPPANA", "122545", "2009-07-04"),
                new DemoCodeData("186", "BV BHOOMARADDY PU COLLEGE", "M SURESHA", "122722", "2009-07-05"),
                new DemoCodeData("187", "BV COMP PU COLLEGE", "KADAKOL SHOHIB", "122776", "2009-07-06"),
                new DemoCodeData("188", "BVVS CSS PU COLLEGE", "M H SANGEETHA", "122895", "2009-07-07"),
                new DemoCodeData("189", "BVVS INDP PU COLLEGE", "P G MANJULA BAI", "122903", "2009-07-08"),
                new DemoCodeData("190", "BVVS SRP INDP PU COLLEGE", "M H DARSHAN", "122952", "2009-07-09"),
                new DemoCodeData("191", "BVVS WOMENS PU COLLEGE", "D V SWATHI", "123052", "2009-07-10"),
                new DemoCodeData("192", "C KARALE PU COLLEGE", "ITAGI SHIVAKUMA", "125647", "2009-07-11"),
                new DemoCodeData("193", "C PAVATE PU COLLEGE", "ITAGI SANGEETHA", "125668", "2009-07-12"),
                new DemoCodeData("194", "CAMBRIDGE COMP PU COLLEGE", "ABDUL NAJEEB", "127657", "2009-07-13"),
                new DemoCodeData("195", "CANARA PU COLLEGE", "GURUPRASAD ADIG", "133584", "2009-07-14"),
                new DemoCodeData("196", "CAPITANIO COMP PU COLLEGE", "MAHESH KALABURG", "133655", "2009-07-15"),
                new DemoCodeData("197", "CARMEL COMP PU COLLEGE", "SHASHIKUMAR POO", "133692", "2009-07-16"),
                new DemoCodeData("198", "CARMEL CONVENT PU COLLEGE", "SEETA HACHYAL", "133718", "2009-07-17"),
                new DemoCodeData("199", "CARMEL GIRLS PU COLLEGE", "RUCHITA NYAMANN", "133756", "2009-07-18"),
                new DemoCodeData("200", "CATHEDRAL COMP PU COLLEGE", "SHANKARAGOUDA N", "133819", "2009-07-19"),
                new DemoCodeData("201", "CAUVERY PU COLLEGE", "VINODA SIDDANAG", "133924", "2009-07-20"),
                new DemoCodeData("202", "CBS PU COLLEGE", "SOMASHEKHARA U ", "138178", "2009-07-21"),
                new DemoCodeData("203", "CES COMP PU COLLEGE", "PRIYANKA U B", "138206", "2009-07-22"),
                new DemoCodeData("204", "CES KH PATIL PU COLLEGE", "GANESHANAIK U", "138229", "2009-07-23"),
                new DemoCodeData("205", "CHAITANYA PU COLLEGE", "REKHA E", "138274", "2009-07-24"),
                new DemoCodeData("206", "CHALLA PU COLLEGE", "ABHISHEKA K", "138282", "2009-07-25"),
                new DemoCodeData("207", "CHAMARAJPET PU COLLEGE", "KOTESHA H", "138312", "2009-07-26"),
                new DemoCodeData("208", "CHANAKYA PU COLLEGE", "MAHANTESHA K", "138325", "2009-07-27"),
                new DemoCodeData("209", "BENAKA PU COLLEGE", "SHIVARAMA K", "138343", "2009-07-28"),
                new DemoCodeData("210", "CHANDRASEKHARA PU COLLEGE", "EDIGARA GURURAJ", "138357", "2009-07-29"),
                new DemoCodeData("211", "CHANDRASHEKHAR PU COLLEGE", "PAVAN K R", "144233", "2009-07-30"),
                new DemoCodeData("212", "CHANGADEVA PU COLLEGE", "VINAYAKA RAJAKU", "144265", "2009-07-31"),
                new DemoCodeData("213", "BES PU COLLEGE", "PREMA KUMARA U", "144272", "2009-08-01"),
                new DemoCodeData("214", "CHENNAMMA PU COLLEGE", "A S POOJA", "144305", "2009-08-02"),
                new DemoCodeData("215", "CHENNASRI PU COLLEGE", "P NEELA BAI", "144374", "2009-08-03"),
                new DemoCodeData("216", "ASS PU COLLEGE", "MAMATHA B", "144419", "2009-08-04"),
                new DemoCodeData("217", "CHETHANA COMP PU COLLEGE", "ALANKARA S", "144455", "2009-08-05"),
                new DemoCodeData("218", "CHINMAYA INDP PU COLLEGE", "POOJA CHANNAPUR", "144495", "2009-08-06"),
                new DemoCodeData("219", "CHITRAKOOTA PU COLLEGE", "TEJU B", "144576", "2009-08-07"),
                new DemoCodeData("220", "CHRIST ACADEMY PU COLLEGE", "SACCHIN M", "144597", "2009-08-08"),
                new DemoCodeData("221", "CHRIST EVENING PU COLLEGE", "M R DARSHANA", "144617", "2009-08-09"),
                new DemoCodeData("222", "CHRIST GLOBAL PU COLLEGE", "TIGARI HEMAREDD", "144654", "2009-08-10"),
                new DemoCodeData("223", "CHRIST PU COLLEGE", "RAKSHITHA N R", "144676", "2009-08-11"),
                new DemoCodeData("224", "CHRIST RESI PU COLLEGE", "MOTEBENNURA MAR", "144735", "2009-08-12"),
                new DemoCodeData("225", "CHRISTA PU COLLEGE", "B YUVARAJA NAIK", "144782", "2009-08-13"),
                new DemoCodeData("226", "CHRISTARAJA PU COLLEGE", "VIJAYAKUMAR RAN", "144804", "2009-08-14"),
                new DemoCodeData("227", "CHRISTEL HOUSE PU COLLEGE", "H PARASHURAMA", "144829", "2009-08-15"),
                new DemoCodeData("228", "CHRISTIAN COMP PU COLLEGE", "B KUBENDRA NAIK", "144967", "2009-08-16"),
                new DemoCodeData("229", "CIC INDP PU COLLEGE", "BASAVARAJA MUDI", "145013", "2009-08-17"),
                new DemoCodeData("230", "CITIZENS PU COLLEGE", "MASALAVADADA NA", "145089", "2009-08-18"),
                new DemoCodeData("231", "AVK COLLEGE", "KOTRESHA TALAVA", "146482", "2009-08-19"),
                new DemoCodeData("232", "CITY RESHMI PU COLLEGE", "PREMA BADIGERA", "153143", "2009-08-20"),
                new DemoCodeData("233", "CJVS COMP SC PU COLLEGE", "ARPITHA N", "153154", "2009-08-21"),
                new DemoCodeData("234", "CLASSIC PU COLLEGE", "RUDRESHA BENDIG", "153163", "2009-08-22"),
                new DemoCodeData("235", "CLES PU COLLEGE", "SALMA AVARAGOLA", "153186", "2009-08-23"),
                new DemoCodeData("236", "CLUNY CONVENT PU COLLEGE", "D G ANUSHA", "153204", "2009-08-24"),
                new DemoCodeData("237", "CMA INDP PU COLLEGE", "DINESHA BARIKA", "153219", "2009-08-25"),
                new DemoCodeData("238", "CMR NATIONAL PU COLLEGE", "POOJA BHOVI", "153221", "2009-08-26"),
                new DemoCodeData("239", "COLUMBIA COMP PU COLLEGE", "RANJITHA HAVANO", "153253", "2009-08-27"),
                new DemoCodeData("240", "CORPORATION PU COLLEGE", "MYLARA KARIBASA", "153262", "2009-08-28"),
                new DemoCodeData("241", "CRESENT COMP PU COLLEGE", "PAVITHRA ALALAG", "153275", "2009-08-29"),
                new DemoCodeData("242", "CROSSLAND PU COLLEGE", "SIDDESHA HONNAM", "153296", "2009-08-30"),
                new DemoCodeData("243", "CRS COMP PU COLLEGE", "KIRAN HAVANURA", "153344", "2009-08-31"),
                new DemoCodeData("244", "CS BEMBALIGI PU COLLEGE", "ARUN BALIGANURU", "153359", "2009-09-01"),
                new DemoCodeData("245", "CSA COMP PU COLLEGE", "PRAVEENA ELIGAR", "153368", "2009-09-02"),
                new DemoCodeData("246", "CV GURUKULA PU COLLEGE", "VIJAYALAKSHMI K", "153392", "2009-09-03"),
                new DemoCodeData("247", "CVG RURAL COMP PU COLLEGE", "PREMA CHIKRAPLA", "153417", "2009-09-04"),
                new DemoCodeData("248", "D BANUMAIAH'S PU COLLEGE", "T M SUSHMA PATI", "153425", "2009-09-05"),
                new DemoCodeData("249", "D CHENNAMMA PU COLLEGE", "SHARADA ANNI", "153473", "2009-09-06"),
                new DemoCodeData("250", "D CHENNAMMA PU COLLEGE", "GIDNALLI NAGARA", "153494", "2009-09-07"),
                new DemoCodeData("251", "D MANJUNATH PU COLLEGE", "KIRANA KURTUKOT", "153501", "2009-09-08"),
                new DemoCodeData("252", "DAKSHA PU COLLEGE", "RAVIGOUDA B", "153536", "2009-09-09"),
                new DemoCodeData("253", "DANDATHIRTHA PU COLLEGE", "REKHA KENCHANAG", "153574", "2009-09-10"),
                new DemoCodeData("254", "DAVAN PU COLLEGE", "POOJA PURANAMAT", "153591", "2009-09-11"),
                new DemoCodeData("255", "DAYANANDA PU COLLEGE", "KORAVARA RUCHIT", "153641", "2009-09-12"),
                new DemoCodeData("256", "DEEPA COMP PU COLLEGE", "MANJYANAIK L", "153663", "2009-09-13"),
                new DemoCodeData("257", "DH COMPOSITE PU COLLEGE", "BANDAMMA GOURIP", "153675", "2009-09-14"),
                new DemoCodeData("258", "DH SHANKARAPPA PU COLLEGE", "MARUTHESHA BADA", "153681", "2009-09-15"),
                new DemoCodeData("259", "DHANWANTARI PU COLLEGE", "B M GANESH", "153699", "2009-09-16"),
                new DemoCodeData("260", "DHARMASAGARA PU COLLEGE", "L SUNITHA", "153704", "2009-09-17"),
                new DemoCodeData("261", "AVK PU COLLEGE", "SHWETHA GORAPLA", "153719", "2009-09-18"),
                new DemoCodeData("262", "DIGVIJAYA COMP PU COLLEGE", "ANNAPPA SOKKI", "153736", "2009-09-19"),
                new DemoCodeData("263", "DL KHOT COMP PU COLLEGE", "ASHOKA CHALAVAD", "153754", "2009-09-20"),
                new DemoCodeData("264", "DMES COMP PU COLLEGE", "SANTHOSHA A", "153783", "2009-09-21"),
                new DemoCodeData("265", "DON BOSCO COMP PU COLLEGE", "MARUTHI KODIHAL", "153808", "2009-09-22"),
                new DemoCodeData("266", "DON BOSCO PU COLLEGE", "DEVARAJA D K", "153847", "2009-09-23"),
                new DemoCodeData("267", "DR AMBEDKAR PU COLLEGE", "MAITHRA MATTIHA", "153894", "2009-09-24"),
                new DemoCodeData("268", "DR BD JATTI PU COLLEGE", "KUDITINIMAGGI D", "153932", "2009-09-25"),
                new DemoCodeData("269", "DR BR AMBEDKAR PU COLLEGE", "H CHAITRA", "153951", "2009-09-26"),
                new DemoCodeData("270", "DR GOPINATHRAO PU COLLEGE", "CHOUDAPPA MALVI", "153993", "2009-09-27"),
                new DemoCodeData("271", "DR JACHANI PU COLLEGE", "KARIBASAMMA POO", "154018", "2009-09-28"),
                new DemoCodeData("272", "DR NSAM PU COLLEGE", "PAVITRA GORAPLA", "154071", "2009-09-29"),
                new DemoCodeData("273", "DR RN KULKARNI PU COLLEGE", "NITHISHKUMAR YA", "154101", "2009-09-30"),
                new DemoCodeData("274", "DR TASED E&SWS PU COLLEGE", "AJJAYYA RAMAVVA", "154113", "2009-10-01"),
                new DemoCodeData("275", "DS NADAGE COMP PU COLLEGE", "ANIL NANDIBEVOO", "154131", "2009-10-02"),
                new DemoCodeData("276", "DSR BANE PU COLLEGE", "DAYANANDA HOSAM", "154153", "2009-10-03"),
                new DemoCodeData("277", "DTJ GIRLS COMP PU COLLEGE", "MANOJ CHALAVADI", "154173", "2009-10-04"),
                new DemoCodeData("278", "DTM NAVODAYA PU COLLEGE", "SUMITHRA KYARAK", "154192", "2009-10-05"),
                new DemoCodeData("279", "DURGADEVI PU COLLEGE", "MARUTHI HIRIYAP", "154232", "2009-10-06"),
                new DemoCodeData("280", "DURGAMBA PU COLLEGE", "BADIGERA MANJUN", "154252", "2009-10-07"),
                new DemoCodeData("281", "DVS INDP PU COLLEGE", "SHWETHA MATTIHA", "154298", "2009-10-08"),
                new DemoCodeData("282", "AYSHA PU COLLEGE", "AJJANAGOUDA BAG", "154377", "2009-10-09"),
                new DemoCodeData("283", "EDU ASIA PU COLLEGE", "SUDEEP BASAPPA ", "158357", "2009-10-10"),
                new DemoCodeData("284", "ELITE PU COLLEGE", "PRAJWAL IRANNA ", "158374", "2009-10-11"),
                new DemoCodeData("285", "EMPRESS PU COLLEGE", "SAVITRI RUDRAPP", "158404", "2009-10-12"),
                new DemoCodeData("286", "EXCELLENT INDP PU COLLEGE", "SAHANA SANGAMES", "158433", "2009-10-13"),
                new DemoCodeData("287", "EXCELLENT PU COLLEGE", "PALLAVI SALAWAD", "158445", "2009-10-14"),
                new DemoCodeData("288", "FALAH GIRLS PU COLLEGE", "BHUVANESHWARI S", "158468", "2009-10-15"),
                new DemoCodeData("289", "BBMP PU COLLEGE", "SACHIN SHIVAPPA", "158483", "2009-10-16"),
                new DemoCodeData("290", "FAROOQIA PU COLLEGE", "YUVARAJ LAXMAN ", "158505", "2009-10-17"),
                new DemoCodeData("291", "FATIMA COMP PU COLLEGE", "BASAVARAJ REVAN", "158516", "2009-10-18"),
                new DemoCodeData("292", "BENAKA PU COLLEGE", "DARSHAN POPATAR", "158592", "2009-10-19"),
                new DemoCodeData("293", "FM DABALI COMP PU COLLEGE", "VEENA MAHADEV B", "158658", "2009-10-20"),
                new DemoCodeData("294", "G CHANNAPPA PU COLLEGE", "BHAGYASHREE MYA", "158723", "2009-10-21"),
                new DemoCodeData("295", "G R P U COLLEGE", "BHAGYASHREE BAS", "158767", "2009-10-22"),
                new DemoCodeData("296", "GANAPATHY PU COLLEGE", "PREETI CHANDU C", "158781", "2009-10-23"),
                new DemoCodeData("297", "GANESH RURAL PU COLLEGE", "RUPSANA AWATI", "158802", "2009-10-24"),
                new DemoCodeData("298", "GANGAVATHI PU COLLEGE", "ANUPAMA CHANDRA", "158891", "2009-10-25"),
                new DemoCodeData("299", "GANGOTHRI PU COLLEGE", "N PARIMALA", "159192", "2009-10-26"),
                new DemoCodeData("300", "GAVISIDDESHWAR PU COLLEGE", "BANGALI HARISHA", "159236", "2009-10-27"),
                new DemoCodeData("301", "GB COMP PU COLLEGE", "H P L SUDEEPA", "159284", "2009-10-28"),
                new DemoCodeData("302", "GB SHANKARRAO PU COLLEGE", "TALEDAHALLI SUN", "159402", "2009-10-29"),
                new DemoCodeData("303", "BES PU COLLEGE", "SAGARA H", "159413", "2009-10-30"),
                new DemoCodeData("304", "GCTM INDP PU COLLEGE", "ANUSHA B S", "160815", "2009-10-31"),
                new DemoCodeData("305", "GE COMP PU COLLEGE", "PRIYANKA N", "160873", "2009-11-01"),
                new DemoCodeData("306", "ASS PU COLLEGE", "CHETHAN R", "161051", "2009-11-02"),
                new DemoCodeData("307", "GENIOUS PU COLLEGE", "N G ROOPA", "163247", "2009-11-03"),
                new DemoCodeData("308", "AVK COLLEGE", "U ABHISHEKA", "163257", "2009-11-04"),
                new DemoCodeData("309", "GHATAPRABHA PU COLLEGE", "A GURUMURTHI", "163319", "2009-11-05"),
                new DemoCodeData("310", "GI BAGEWADI PU COLLEGE", "M M NAGARAJA", "163368", "2009-11-06"),
                new DemoCodeData("311", "GIREESHA GIRLS PU COLLEGE", "M KIRANA NAIK", "163509", "2009-11-07"),
                new DemoCodeData("312", "GLOBAL INDP PU COLLEGE", "SULIPHAL MATHAD", "163541", "2009-11-08"),
                new DemoCodeData("313", "GLOBAL PU COLLEGE", "KADEMANI ABHISH", "163563", "2009-11-09"),
                new DemoCodeData("314", "AVK PU COLLEGE", "P GANESHANAIK", "163606", "2009-11-10"),
                new DemoCodeData("315", "AYSHA PU COLLEGE", "SANTHOSH NAIK L", "163625", "2009-11-11"),
                new DemoCodeData("316", "GM HALAMMA IND PU COLLEGE", "VASANTHI K", "164652", "2009-11-12"),
                new DemoCodeData("317", "GM KHENI PU COLLEGE", "BINDUSHREE N", "167221", "2009-11-13"),
                new DemoCodeData("318", "GNANABHARATHI PU COLLEGE", "HARISHA A B", "169421", "2009-11-14"),
                new DemoCodeData("319", "GNANADHARE PU COLLEGE", "SURYA V", "169663", "2009-11-15"),
                new DemoCodeData("320", "GNANAJYOTHI PU COLLEGE", "ASHWINI SANGANA", "173518", "2009-11-16"),
                new DemoCodeData("321", "GNANAMANDIRA PU COLLEGE", "ASHWINI ANANT U", "173573", "2009-11-17"),
                new DemoCodeData("322", "GNANAVIKAS PU COLLEGE", "EGUBAI PEERU LA", "173641", "2009-11-18"),
                new DemoCodeData("323", "GNS COMP PU COLLEGE", "SANTOSH SHANKAR", "173697", "2009-11-19"),
                new DemoCodeData("324", "GOLDEN PU COLLEGE", "AISHWARYA TIPPA", "173706", "2009-11-20"),
                new DemoCodeData("325", "GOMATESH COMP PU COLLEGE", "BOJARAJ BIRADAR", "174077", "2009-11-21"),
                new DemoCodeData("326", "GOOD NEWS INDP PU COLLEGE", "SANGAMESH BASAY", "174622", "2009-11-22"),
                new DemoCodeData("327", "GOODSHEPHERD PU COLLEGE", "NEHA CHAVAN", "175562", "2009-11-23"),
                new DemoCodeData("328", "BBMP PU COLLEGE", "K P SHOBHA", "180802", "2009-11-24"),
                new DemoCodeData("329", "GOPALAN PU COLLEGE", "MUBARAK ALI H", "180909", "2009-11-25"),
                new DemoCodeData("330", "GOPALASWAMY SV PU COLLEGE", "M D JYOTHI", "180937", "2009-11-26"),
                new DemoCodeData("331", "BENAKA PU COLLEGE", "MEGHA K", "181022", "2009-11-27"),
                new DemoCodeData("332", "GOUTHAMA BUDDA PU COLLEGE", "ANGADI MUSTAFA", "181091", "2009-11-28"),
                new DemoCodeData("333", "GOVERNMENT PU COLLEGE", "T C BHOOMIKA", "181145", "2009-11-29"),
                new DemoCodeData("334", "GOVINDA DASA PU COLLEGE", "D L LAKSHMI", "181266", "2009-11-30"),
                new DemoCodeData("335", "GOVT ALK PU COLLEGE", "K SHRUTHI", "181325", "2009-12-01"),
                new DemoCodeData("336", "GOVT ASB COMP PU COLLEGE", "VISHWA HIREMATH", "181363", "2009-12-02"),
                new DemoCodeData("337", "GOVT BP PU COLLEGE", "MUTTURAJ MADAR", "181476", "2009-12-03"),
                new DemoCodeData("338", "GOVT COMP PU COLLEGE", "M S POOJA", "181488", "2009-12-04"),
                new DemoCodeData("339", "GOVT DB PU COLLEGE", "SHOBHA MADAR", "181508", "2009-12-05"),
                new DemoCodeData("340", "GOVT DPBS PU COLLEGE", "SHILPA TALAWAR", "181529", "2009-12-06"),
                new DemoCodeData("341", "GOVT DRM PU COLLEGE", "U BASAVARAJA", "181548", "2009-12-07"),
                new DemoCodeData("342", "GOVT FC MATHUR PU COLLEGE", "K NETHRAVATHI", "181576", "2009-12-08"),
                new DemoCodeData("343", "GOVT GB PU COLLEGE", "SUNILKUMAR HEBB", "181664", "2009-12-09"),
                new DemoCodeData("344", "GOVT GV GOWDA PU COLLEGE", "MEGHA HEBBAL", "181741", "2009-12-10"),
                new DemoCodeData("345", "GOVT H BENNE PU COLLEGE", "T A ISHWARYA", "181781", "2009-12-11"),
                new DemoCodeData("346", "GOVT HK & GKK PU COLLEGE", "BHAGHYASHREE BI", "181793", "2009-12-12"),
                new DemoCodeData("347", "GOVT HKR GOWDA PU COLLEGE", "SUSHMEETA NADAG", "181836", "2009-12-13"),
                new DemoCodeData("348", "GOVT HMS PU COLLEGE", "MAHANTAGOUDA SH", "181899", "2009-12-14"),
                new DemoCodeData("349", "BES PU COLLEGE", "S ALFAZ", "181903", "2009-12-15"),
                new DemoCodeData("350", "GOVT INDP PU COLLEGE", "SANGAMMA NATIKA", "183376", "2009-12-16"),
                new DemoCodeData("351", "GOVT KLK PU COLLEGE", "VINOD HADAPAD", "183598", "2009-12-17"),
                new DemoCodeData("352", "GOVT LBS PU COLLEGE", "BABAJAN BAVASAB", "188044", "2009-12-18"),
                new DemoCodeData("353", "GOVT LSBSSM PU COLLEGE", "ISMAIL AMEENSAB", "188065", "2009-12-19"),
                new DemoCodeData("354", "GOVT MAHARAJAS PU COLLEGE", "SIDDAROODHA SAJ", "188121", "2009-12-20"),
                new DemoCodeData("355", "GOVT MAHARANI PU COLLEGE", "POOJA SHANKAR R", "188143", "2009-12-21"),
                new DemoCodeData("356", "GOVT MAJID PU COLLEGE", "LAKSHMI SANGAPP", "188224", "2009-12-22"),
                new DemoCodeData("357", "GOVT MARIKAMBA PU COLLEGE", "GURULINGAYYA HI", "188251", "2009-12-23"),
                new DemoCodeData("358", "GOVT MARUTHI PU COLLEGE", "SUSHMITHA NINGA", "188283", "2009-12-24"),
                new DemoCodeData("359", "GOVT MG PU COLLEGE", "BORAMMA HALLUR", "192618", "2009-12-25"),
                new DemoCodeData("360", "GOVT MGSV PU COLLEGE", "PRAKASH KENGUTT", "192736", "2009-12-26"),
                new DemoCodeData("361", "GOVT MHN PU COLLEGE", "UJJINI ROJA", "194181", "2009-12-27"),
                new DemoCodeData("362", "GOVT MM PU COLLEGE", "BASAVANALA NARE", "194208", "2009-12-28"),
                new DemoCodeData("363", "GOVT NMM PU COLLEGE", "M J KIRANAKUMAR", "194219", "2009-12-29"),
                new DemoCodeData("364", "ASS PU COLLEGE", "AYYANAHALLI SID", "194221", "2009-12-30"),
                new DemoCodeData("365", "AVK COLLEGE", "BASAVANALU NIRM", "194242", "2009-12-31"),
                new DemoCodeData("366", "AVK PU COLLEGE", "KODTHI ASHA", "194263", "2009-01-01"),
                new DemoCodeData("367", "AYSHA PU COLLEGE", "ALURU AKSHATHA", "194275", "2009-01-02"),
                new DemoCodeData("368", "GOVT PU COLLEGE", "DODDA BHEMANAGO", "194286", "2009-01-03"),
                new DemoCodeData("369", "BBMP PU COLLEGE", "AYYANAHALLI RAN", "194317", "2009-01-04"),
                new DemoCodeData("370", "BENAKA PU COLLEGE", "BHAVANA BANAKAR", "194364", "2009-01-05"),
                new DemoCodeData("371", "BES PU COLLEGE", "GURUDEVI YALLAP", "195462", "2009-01-06"),
                new DemoCodeData("372", "ASS PU COLLEGE", "RAVIKUMAR KARAL", "195592", "2009-01-07"),
                new DemoCodeData("373", "AVK COLLEGE", "AKASH KHANAPUR", "195654", "2009-01-08"),
                new DemoCodeData("374", "AVK PU COLLEGE", "MUTTAPPA PARAMA", "195663", "2009-01-09"),
                new DemoCodeData("375", "AYSHA PU COLLEGE", "M KALPANA", "198332", "2009-01-10"),
                new DemoCodeData("376", "BBMP PU COLLEGE", "UJJANAGONDRA VE", "198421", "2009-01-11"),
                new DemoCodeData("377", "BENAKA PU COLLEGE", "GANGADHARA NAGA", "198468", "2009-01-12"),
                new DemoCodeData("378", "BES PU COLLEGE", "SHASHIKALA NICH", "198482", "2009-01-13"),
                new DemoCodeData("379", "ASS PU COLLEGE", "HANUMAVVA JAGEN", "198503", "2009-01-14"),
                new DemoCodeData("380", "AVK COLLEGE", "G BASAVARAJA", "198587", "2009-01-15"),
                new DemoCodeData("381", "AVK PU COLLEGE", "DODDABARAMAPPAR", "198633", "2009-01-16"),
                new DemoCodeData("382", "AYSHA PU COLLEGE", "PUJAR NEELA KUM", "198645", "2009-01-17"),
                new DemoCodeData("383", "BBMP PU COLLEGE", "BALLURU HANUMAN", "198662", "2009-01-18"),
                new DemoCodeData("384", "BENAKA PU COLLEGE", "KARNI NITHINKUM", "198689", "2009-01-19"),
                new DemoCodeData("385", "BES PU COLLEGE", "SHARATHKUMARA M", "198711", "2009-01-20"),
                new DemoCodeData("386", "ASS PU COLLEGE", "BENAKALLU SINCH", "198733", "2009-01-21"),
                new DemoCodeData("387", "AVK COLLEGE", "MUDENURU AJJAYY", "198759", "2009-01-22"),
                new DemoCodeData("388", "AVK PU COLLEGE", "OBALAPURA SATHY", "198781", "2009-01-23"),
                new DemoCodeData("389", "AYSHA PU COLLEGE", "O YALLAMMA", "198802", "2009-01-24"),
                new DemoCodeData("390", "BBMP PU COLLEGE", "CHALAVADI KAMAL", "198813", "2009-01-25"),
                new DemoCodeData("391", "BENAKA PU COLLEGE", "M V SUMA", "198834", "2009-01-26"),
                new DemoCodeData("392", "GOVT PU COMP COLLEGE", "NAVEENA NAGAMNA", "198881", "2009-01-27"),
                new DemoCodeData("393", "GOVT SAA PU COLLEGE", "MUGAPPA G", "198902", "2009-01-28"),
                new DemoCodeData("394", "GOVT SARDAR'S PU COLLEGE", "PALESHA BORAMMA", "198923", "2009-01-29"),
                new DemoCodeData("395", "BES PU COLLEGE", "KUMARASWAMY BAN", "198944", "2009-01-30"),
                new DemoCodeData("396", "GOVT SCRB PU COLLEGE", "MARUTHI DIBDALL", "198965", "2009-01-31"),
                new DemoCodeData("397", "GOVT SGGC PU COLLEGE", "BHARATH BHANGI", "198996", "2009-02-01"),
                new DemoCodeData("398", "GOVT SGM PU COLLEGE", "RAMESHA ASAGODU", "199059", "2009-02-02"),
                new DemoCodeData("399", "GOVT SGS COMP PU COLLEGE", "PRASHANTHA KARL", "199092", "2009-02-03"),
                new DemoCodeData("400", "GOVT SGT PU COLLEGE", "VIJAYA KUMARA K", "199114", "2009-02-04"),
                new DemoCodeData("401", "GOVT SH GIRLS PU COLLEGE", "UCHHANGAMMA K", "199145", "2009-02-05"),
                new DemoCodeData("402", "GOVT SHIVAJI PU COLLEGE", "JYOTHI CHANNABA", "199166", "2009-02-06"),
                new DemoCodeData("403", "GOVT SJJM PU COLLEGE", "MAHALAKSHMI NIC", "199187", "2009-02-07"),
                new DemoCodeData("404", "GOVT SJVP PU COLLEGE", "SHOBHA BOMMAYNA", "199251", "2009-02-08"),
                new DemoCodeData("405", "GOVT SK PU COLLEGE", "NAYANA NELKUDRI", "199317", "2009-02-09"),
                new DemoCodeData("406", "GOVT SKR BOYS PU COLLEGE", "G MALLESHA", "199352", "2009-02-10"),
                new DemoCodeData("407", "GOVT SKTM PU COLLEGE", "MUKADAINARA YOG", "199438", "2009-02-11"),
                new DemoCodeData("408", "GOVT SKVD PU COLLEGE", "RANJITHA KANNAK", "199693", "2009-02-12"),
                new DemoCodeData("409", "GOVT SMNN PU COLLEGE", "PALLAVI K M", "199749", "2009-02-13"),
                new DemoCodeData("410", "GOVT SMSD PU COLLEGE", "BESTHARA SAVITH", "199817", "2009-02-14"),
                new DemoCodeData("411", "GOVT SN SANIL PU COLLEGE", "SHILPA DINNI", "200964", "2009-02-15"),
                new DemoCodeData("412", "GOVT SPM PU COLLEGE", "KUSHAVVA RATHOD", "201046", "2009-02-16"),
                new DemoCodeData("413", "GOVT SPMB INDP PU COLLEGE", "SIDDAPPA NAGUR", "201091", "2009-02-17"),
                new DemoCodeData("414", "GOVT SRI KH PU COLLEGE", "RENUKA NAGUR", "201159", "2009-02-18"),
                new DemoCodeData("415", "GOVT SRM PU COLLEGE", "MANOJ MAHESHVAR", "201452", "2009-02-19"),
                new DemoCodeData("416", "GOVT SRS PU COLLEGE", "BHOOMIKA PM", "203142", "2009-02-20"),
                new DemoCodeData("417", "GOVT SRV PATIL PU COLLEGE", "SUSHMITHA M", "203199", "2009-02-21"),
                new DemoCodeData("418", "GOVT SSBM PU COLLEGE", "POORNIMA S", "203236", "2009-02-22"),
                new DemoCodeData("419", "GOVT SSCA PU COLLEGE", "CHETHANA HB", "203254", "2009-02-23"),
                new DemoCodeData("420", "GOVT SSEA PU COLLEGE", "SWATI KUMATAGI", "205589", "2009-02-24"),
                new DemoCodeData("421", "GOVT SSK GIRLS PU COLLEGE", "ARATI SHINDHE", "205733", "2009-02-25"),
                new DemoCodeData("422", "GOVT SSKJ PU COLLEGE", "SHAKEERA DASTAG", "205747", "2009-02-26"),
                new DemoCodeData("423", "GOVT SSLS PU COLLEGE", "CHARANRAJ HARIJ", "208713", "2009-02-27"),
                new DemoCodeData("424", "GOVT STASL PU COLLEGE", "RADHIKA KOLAKAR", "210287", "2009-02-28"),
                new DemoCodeData("425", "GOVT STJ PU COLLEGE", "DEVARAJA HADAGA", "212338", "2009-03-01"),
                new DemoCodeData("426", "GOVT STSK PU COLLEGE", "AKASHA KOUTI", "212341", "2009-03-02"),
                new DemoCodeData("427", "GOVT SVK GIRLS PU COLLEGE", "ROJA KANAKAPNAR", "212368", "2009-03-03"),
                new DemoCodeData("428", "GRAMEENA VVS PU COLLEGE", "SHIVARAJA M", "212383", "2009-03-04"),
                new DemoCodeData("429", "GREEN MOUNTAIN PU COLLEGE", "RANJITHA KAMMAR", "212392", "2009-03-05"),
                new DemoCodeData("430", "ASS PU COLLEGE", "SANTHOSHA MUDDI", "212417", "2009-03-06"),
                new DemoCodeData("431", "AVK COLLEGE", "PREETHI IRANI", "212437", "2009-03-07"),
                new DemoCodeData("432", "GUNASHRI PU COLLEGE", "GAGANASHREE H S", "212442", "2009-03-08"),
                new DemoCodeData("433", "GUPTHA INDP PU COLLEGE", "NANJUNDA BADAGU", "212466", "2009-03-09"),
                new DemoCodeData("434", "GURUKRUPA PU COLLEGE", "AMRUTHA VALEKAR", "212478", "2009-03-10"),
                new DemoCodeData("435", "AVK PU COLLEGE", "NAGAVENI MALAPN", "212481", "2009-03-11"),
                new DemoCodeData("436", "GURUKULA RURAL PU COLLEGE", "NEELAMMA MALAPN", "212493", "2009-03-12"),
                new DemoCodeData("437", "GURUMAHANTESHA PU COLLEGE", "GANGADHARA GRUH", "212502", "2009-03-13"),
                new DemoCodeData("438", "GURUMALLESWARA PU COLLEGE", "NIRANJANA TATAP", "212528", "2009-03-14"),
                new DemoCodeData("439", "GURUNANAK IND PU COLLEGE", "KARIGARA RANJIT", "212544", "2009-03-15"),
                new DemoCodeData("440", "GV NAIK INDP PU COLLEGE", "SHILPA C", "212565", "2009-03-16"),
                new DemoCodeData("441", "AYSHA PU COLLEGE", "CHANDANA MENASI", "212584", "2009-03-17"),
                new DemoCodeData("442", "HAMDARD COMP PU COLLEGE", "ROJA RANGAPURAD", "212593", "2009-03-18"),
                new DemoCodeData("443", "HARABHAT COMP PU COLLEGE", "HULIGEMMA BHOVI", "212635", "2009-03-19"),
                new DemoCodeData("444", "HARALAYYA COMP PU COLLEGE", "CHANNABASAMMA B", "212646", "2009-03-20"),
                new DemoCodeData("445", "HARANI PU COLLEGE", "NAVYA Y N", "214436", "2009-03-21"),
                new DemoCodeData("446", "HARDWICKE INDP PU COLLEGE", "THIPPARAJU V", "214453", "2009-03-22"),
                new DemoCodeData("447", "HASANATH PU COLLEGE", "THRINESHA Y N", "214487", "2009-03-23"),
                new DemoCodeData("448", "BBMP PU COLLEGE", "Y BALAJI", "214501", "2009-03-24"),
                new DemoCodeData("449", "BENAKA PU COLLEGE", "NAGESH Y V", "214512", "2009-03-25"),
                new DemoCodeData("450", "HB GOWDA COMP PU COLLEGE", "ASHOKA Y N", "214521", "2009-03-26"),
                new DemoCodeData("451", "HCES INDP PU COLLEGE", "HANUMANTHA REDD", "214539", "2009-03-27"),
                new DemoCodeData("452", "HCMG COMP PU COLLEGE", "SUGUNA Y N", "214549", "2009-03-28"),
                new DemoCodeData("453", "HD KARADIGOWDA PU COLLEGE", "Y L MUNEENDRA", "214555", "2009-03-29"),
                new DemoCodeData("454", "HEBBALE COMP PU COLLEGE", "Y C GAJENDRA", "214608", "2009-03-30"),
                new DemoCodeData("455", "HELENS PU COLLEGE", "Y N SUKANYA", "214629", "2009-03-31"),
                new DemoCodeData("456", "HG CHANNAPPA PU COLLEGE", "POOJA SANDHIMAN", "218296", "2009-04-01"),
                new DemoCodeData("457", "HG PU COLLEGE", "YALLAVVA MADAR", "218342", "2009-04-02"),
                new DemoCodeData("458", "HIMALAYA PU COLLEGE", "SUSHMITA BADAGI", "218381", "2009-04-03"),
                new DemoCodeData("459", "HINDU PU COLLEGE", "SAVITA WADDAR", "218397", "2009-04-04"),
                new DemoCodeData("460", "HIRA INDP PU COLLEGE", "SHASHIKALA WALI", "218402", "2009-04-05"),
                new DemoCodeData("461", "HK GARIB NAWAZ PU COLLEGE", "DADDEPNARA VIJA", "219531", "2009-04-06"),
                new DemoCodeData("462", "HKES SCIENCE PU COLLEGE", "SEEMA H", "219591", "2009-04-07"),
                new DemoCodeData("463", "HKM BEARY COMP PU COLLEGE", "BURUDI THILAKA", "219737", "2009-04-08"),
                new DemoCodeData("464", "HKS PU COLLEGE", "M PRAJWAL", "219794", "2009-04-09"),
                new DemoCodeData("465", "HM ISHWARAN SC PU COLLEGE", "PRAMODA BHOVI", "219874", "2009-04-10"),
                new DemoCodeData("466", "HOLY CROSS PU COLLEGE", "HUSEN SAB R", "219892", "2009-04-11"),
                new DemoCodeData("467", "HOLY SPIRIT PU COLLEGE", "CHINNAVARA BASA", "219912", "2009-04-12"),
                new DemoCodeData("468", "HOMBEGOWDA IND PU COLLEGE", "HALEMANI RADHIK", "219964", "2009-04-13"),
                new DemoCodeData("469", "HONNAMMADEVI PU COLLEGE", "MEGHARAJA KORAV", "220045", "2009-04-14"),
                new DemoCodeData("470", "HOYSALA COMP PU COLLEGE", "BURUDI DEEPA", "220063", "2009-04-15"),
                new DemoCodeData("471", "HOYSALA INDP PU COLLEGE", "THIRUPATHI C", "220179", "2009-04-16"),
                new DemoCodeData("472", "HOYSALA PU COLLEGE", "BELURU SIDDESHA", "220236", "2009-04-17"),
                new DemoCodeData("473", "HOYSALESWARA PU COLLEGE", "BASAVARAJA M", "220252", "2009-04-18"),
                new DemoCodeData("474", "HPS PU COLLEGE", "LAKSHMI C", "220277", "2009-04-19"),
                new DemoCodeData("475", "HSB PU COLLEGE", "MADIVALARA VEEN", "220352", "2009-04-20"),
                new DemoCodeData("476", "HSM WOMENS PU COLLEGE", "BARIKARA SUMA", "220394", "2009-04-21"),
                new DemoCodeData("477", "HUDA NATIONAL PU COLLEGE", "A SANNANAGARAJA", "224332", "2009-04-22"),
                new DemoCodeData("478", "HUDA PU COLLEGE", "A NAGARAJA", "224348", "2009-04-23"),
                new DemoCodeData("479", "HV COMP PU COLLEGE", "B SUMITRA", "224364", "2009-04-24"),
                new DemoCodeData("480", "HYMAMSHU COMP PU COLLEGE", "YELLAMMA YALLAP", "224416", "2009-04-25"),
                new DemoCodeData("481", "IDEAL PU COLLEGE", "K NEELAMMA", "224423", "2009-04-26"),
                new DemoCodeData("482", "IG HIREGOUDAR PU COLLEGE", "M NAGAMMA", "224449", "2009-04-27"),
                new DemoCodeData("483", "IIBS PU COLLEGE", "T KAVYA", "224479", "2009-04-28"),
                new DemoCodeData("484", "IICA INDP PU COLLEGE", "H KUMARSWAMY", "224487", "2009-04-29"),
                new DemoCodeData("485", "IKRA COMP PU COLLEGE", "D SARASWATHI", "224493", "2009-04-30"),
                new DemoCodeData("486", "IMPERIAL INDP PU COLLEGE", "M C ASHA", "224536", "2009-05-01"),
                new DemoCodeData("487", "IMPERIAL INDP PU COLLEGE", "T RAKESH", "224541", "2009-05-02"),
                new DemoCodeData("488", "IMPERIAL PU COLLEGE", "M SANGEETHA", "224552", "2009-05-03"),
                new DemoCodeData("489", "INDIAN ACADEMY PU COLLEGE", "KAVYA R", "226428", "2009-05-04"),
                new DemoCodeData("490", "INDU INDP PU COLLEGE", "S YASHAVANTHA", "226494", "2009-05-05"),
                new DemoCodeData("491", "INDU INNOVATIVE PU COLLEGE", "SONDURU AMBIKA", "226506", "2009-05-06"),
                new DemoCodeData("492", "INDU PU COLLEGE", "GANACHARI ROOPA", "226518", "2009-05-07"),
                new DemoCodeData("493", "INDU PU COLLEGE", "HEGGANNARA POOJ", "226545", "2009-05-08"),
                new DemoCodeData("494", "ISLAMIA NISWAN PU COLLEGE", "NALABAND SAMEEN", "226624", "2009-05-09"),
                new DemoCodeData("495", "ISLAMIYA COMP PU COLLEGE", "MADIVALARA POOJ", "226644", "2009-05-10"),
                new DemoCodeData("496", "J BETHANI PU COLLEGE", "HADAPADA ANUSHA", "226663", "2009-05-11"),
                new DemoCodeData("497", "J THONTADARYA PU COLLEGE", "JOGINARA SUMA", "226672", "2009-05-12"),
                new DemoCodeData("498", "J TONTADARYA PU COLLEGE", "HAVANOORU SYED", "226687", "2009-05-13"),
                new DemoCodeData("499", "JA COMP PU COLLEGE", "G R SUPRITHA", "228284", "2009-05-14"),
                new DemoCodeData("500", "JA SAVADATTI PU COLLEGE", "VEERESHA BAAGLI", "230047", "2009-05-15"),
                new DemoCodeData("501", "JAIN INDP PU COLLEGE", "SHWETHA GARBHAG", "230053", "2009-05-16"),
                new DemoCodeData("502", "JAIN PU COLLEGE", "SANGEETHA KORAV", "230084", "2009-05-17"),
                new DemoCodeData("503", "BES PU COLLEGE", "AMBIKA HYARDADA", "230091", "2009-05-18"),
                new DemoCodeData("504", "ASS PU COLLEGE", "SINDHU CHIKKABI", "230102", "2009-05-19"),
                new DemoCodeData("505", "AVK COLLEGE", "SUCHITRA HADGAL", "230118", "2009-05-20"),
                new DemoCodeData("506", "JANANI PU COLLEGE", "POOJA GUTTEVAR", "230124", "2009-05-21"),
                new DemoCodeData("507", "JANASEVA RESI PU COLLEGE", "AMBIKA KORAVARA", "230169", "2009-05-22"),
                new DemoCodeData("508", "JANATHA PU COLLEGE", "CHETHANA KANCHI", "230173", "2009-05-23"),
                new DemoCodeData("509", "JAVALIS PU COLLEGE", "G KAVYA", "231883", "2009-05-24"),
                new DemoCodeData("510", "JAWAHAR COMP PU COLLEGE", "DONNEPNARA VENK", "231918", "2009-05-25"),
                new DemoCodeData("511", "AVK PU COLLEGE", "MRUTHYUNJAYA S", "231927", "2009-05-26"),
                new DemoCodeData("512", "JAYAPURA COMP PU COLLEGE", "HAREESH L N", "231933", "2009-05-27"),
                new DemoCodeData("513", "JCBM PU COLLEGE", "SUSHMA TIRUKAPP", "231971", "2009-05-28"),
                new DemoCodeData("514", "AYSHA PU COLLEGE", "CHANDANA K", "231997", "2009-05-29"),
                new DemoCodeData("515", "BBMP PU COLLEGE", "SUDEEPA A", "232002", "2009-05-30"),
                new DemoCodeData("516", "JINDAL GIRLS PU COLLEGE", "VADDARAHALLI DA", "232035", "2009-05-31"),
                new DemoCodeData("517", "JINDAL PU COLLEGE", "DARSHAN D H", "232045", "2009-06-01"),
                new DemoCodeData("518", "JJS COMP PU COLLEGE", "KUSHALA RODDAPP", "232057", "2009-06-02"),
                new DemoCodeData("519", "JK INDP PU COLLEGE", "KOTRAPPANAVARA ", "232104", "2009-06-03"),
                new DemoCodeData("520", "JMJ COMP PU COLLEGE", "HANUMANTHA A", "232138", "2009-06-04"),
                new DemoCodeData("521", "JNANA PRAGATI PU COLLEGE", "KANCHIKERI CHAI", "232165", "2009-06-05"),
                new DemoCodeData("522", "JNANA SAGARA PU COLLEGE", "MADIVALARA NING", "232209", "2009-06-06"),
                new DemoCodeData("523", "JNANABANDHU PU COLLEGE", "R LAKSHMI BAI", "232212", "2009-06-07"),
                new DemoCodeData("524", "JNANABHARATHI PU COLLEGE", "KANCHIKERI PRAV", "232269", "2009-06-08"),
                new DemoCodeData("525", "JNANADEEPA PU COLLEGE", "MEGHARAJA M", "232276", "2009-06-09"),
                new DemoCodeData("526", "JNANAGANGA PU COLLEGE", "DANI LAKSHMI", "232302", "2009-06-10"),
                new DemoCodeData("527", "JNANAJYOTHI PU COLLEGE", "POOJA M H", "232319", "2009-06-11"),
                new DemoCodeData("528", "JNANAPEETA PU COLLEGE", "TIPPANAHALLI HA", "235389", "2009-06-12"),
                new DemoCodeData("529", "JNANASAGARA PU COLLEGE", "MADIVALARA HARS", "235438", "2009-06-13"),
                new DemoCodeData("530", "JNANASAROVARA PU COLLEGE", "S ANJUM", "235442", "2009-06-14"),
                new DemoCodeData("531", "JNANASHREE EXPERT PU COLLEGE", "MALA N", "235486", "2009-06-15"),
                new DemoCodeData("532", "JNANASINCHANA PU COLLEGE", "GHATINA DEEPA", "235606", "2009-06-16"),
                new DemoCodeData("533", "JNANASUDHA PU COLLEGE", "SOMANAGOWDRA PR", "238624", "2009-06-17"),
                new DemoCodeData("534", "JNANASWEEKAR PU COLLEGE", "P PADMA BAI", "241693", "2009-06-18"),
                new DemoCodeData("535", "JNANODAYA INDP PU COLLEGE", "M B DEVARAJA", "241729", "2009-06-19"),
                new DemoCodeData("536", "JOHN WESLEY SC PU COLLEGE", "LOKESHA L", "241745", "2009-06-20"),
                new DemoCodeData("537", "JP MEMORIAL PU COLLEGE", "SATHWADI PAVANA", "241773", "2009-06-21"),
                new DemoCodeData("538", "JP NARAYAN PU COLLEGE", "D ASHOK CHAWAN", "241781", "2009-06-22"),
                new DemoCodeData("539", "JSDK COMP PU COLLEGE", "BRAHMAKUMAR JAI", "241853", "2009-06-23"),
                new DemoCodeData("540", "JSS COMP PU COLLEGE", "SANGAYYA VEERAY", "241867", "2009-06-24"),
                new DemoCodeData("541", "JSS GIRLS INDP PU COLLEGE", "C S RAKSHITHA", "241882", "2009-06-25"),
                new DemoCodeData("542", "JSS INDP PU COLLEGE", "LAMBANI SHILPA", "241896", "2009-06-26"),
                new DemoCodeData("543", "JSS PU COLLEGE", "KAVADI SHIVAKUM", "241912", "2009-06-27"),
                new DemoCodeData("544", "BENAKA PU COLLEGE", "M GOURI", "241929", "2009-06-28"),
                new DemoCodeData("545", "BES PU COLLEGE", "MARUTHI M", "241943", "2009-06-29"),
                new DemoCodeData("546", "ASS PU COLLEGE", "BASAVARAJ MADAR", "244886", "2009-06-30"),
                new DemoCodeData("547", "JSS SM INDP SC PU COLLEGE", "GANESH MADIKESH", "244893", "2009-07-01"),
                new DemoCodeData("548", "JT PU COLLEGE", "SHRISHAIL BADIG", "244988", "2009-07-02"),
                new DemoCodeData("549", "JUBEDA INDP PU COLLEGE", "RUKSANA BEELAGI", "245023", "2009-07-03"),
                new DemoCodeData("550", "JYOTHI COMP PU COLLEGE", "AKASH SUDHAKAR", "245047", "2009-07-04"),
                new DemoCodeData("551", "JYOTHI INDP PU COLLEGE", "SATISH DODAMANI", "245159", "2009-07-05"),
                new DemoCodeData("552", "JYOTHY KV COMP PU COLLEGE", "VISHAL KOLUR", "245169", "2009-07-06"),
                new DemoCodeData("553", "JYOTI NIVAS PU COLLEGE", "RAVI MADAR", "245185", "2009-07-07"),
                new DemoCodeData("554", "JYOTI PU COLLEGE", "CHANDRAKALA V R", "251064", "2009-07-08"),
                new DemoCodeData("555", "K MALLANNA MEM PU COLLEGE", "VARALAKSHMI P E", "251074", "2009-07-09"),
                new DemoCodeData("556", "K VEERABASAPPA PU COLLEGE", "KAVYA S R", "251088", "2009-07-10"),
                new DemoCodeData("557", "AVK COLLEGE", "GEETHA S V", "251121", "2009-07-11"),
                new DemoCodeData("558", "KAIZEN PU COLLEGE", "VISHWANATH S N", "251175", "2009-07-12"),
                new DemoCodeData("559", "KALIDAS COMP PU COLLEGE", "VEENA V K", "251216", "2009-07-13"),
                new DemoCodeData("560", "KALIDASA COMP PU COLLEGE", "VEDASHREE P", "251239", "2009-07-14"),
                new DemoCodeData("561", "KALIDASA PU COLLEGE", "KARTHIKA J K", "251243", "2009-07-15"),
                new DemoCodeData("562", "KALINGA PU COLLEGE", "NAVYASHREE S", "251286", "2009-07-16"),
                new DemoCodeData("563", "KALPATARU PU COLLEGE", "PAVANI G M", "251299", "2009-07-17"),
                new DemoCodeData("564", "KALPAVRUKSHA PU COLLEGE", "GAJENDRA C N", "251314", "2009-07-18"),
                new DemoCodeData("565", "KAMPALARANGA PU COLLEGE", "DIVYASHREE S", "251414", "2009-07-19"),
                new DemoCodeData("566", "KANAKA INDP PU COLLEGE", "NANDINI S R", "251425", "2009-07-20"),
                new DemoCodeData("567", "KANAKADAS COMP PU COLLEGE", "CHAITHRA S R", "251512", "2009-07-21"),
                new DemoCodeData("568", "KANAKADASA PU COLLEGE", "SRIKANTH H G", "251527", "2009-07-22"),
                new DemoCodeData("569", "KANNUR PU COLLEGE", "SOWMYA J V", "251531", "2009-07-23"),
                new DemoCodeData("570", "KAPILAMMA PU COLLEGE", "B ROJA", "256975", "2009-07-24"),
                new DemoCodeData("571", "KARNATAK ARTS COLLEGE", "CHELUVADI BHEEM", "257006", "2009-07-25"),
                new DemoCodeData("572", "KARNATAKA COMP PU COLLEGE", "HULIKATTI NAGAR", "257028", "2009-07-26"),
                new DemoCodeData("573", "KARNATAKA PU COLLEGE", "BHOVI KALAPPA", "257125", "2009-07-27"),
                new DemoCodeData("574", "AVK PU COLLEGE", "ERADETTINAHALLI", "257174", "2009-07-28"),
                new DemoCodeData("575", "KARNATAKKEERTI PU COLLEGE", "TALAVARA MAHEND", "257187", "2009-07-29"),
                new DemoCodeData("576", "KARTHIK PU COLLEGE", "CHELUVADI POOJA", "257229", "2009-07-30"),
                new DemoCodeData("577", "KARUNYA PU COLLEGE", "S G RANJITHA", "261407", "2009-07-31"),
                new DemoCodeData("578", "KAVERI PU COLLEGE", "ANUSHA K L", "261634", "2009-08-01"),
                new DemoCodeData("579", "KBC PU COLLEGE", "NAGARATHNA", "261729", "2009-08-02"),
                new DemoCodeData("580", "KE BOARD PU COLLEGE", "PUJARA SANDHYA", "265816", "2009-08-03"),
                new DemoCodeData("581", "KEB COMP PU COLLEGE", "UJJANI UMAR FAR", "266137", "2009-08-04"),
                new DemoCodeData("582", "KENCHAMBA PU COLLEGE", "RANJITHA D", "266232", "2009-08-05"),
                new DemoCodeData("583", "KENGERI PU COLLEGE", "KARADI ROSHAN", "266291", "2009-08-06"),
                new DemoCodeData("584", "KES COMP PU COLLEGE", "DODDAMANI BHOOM", "266504", "2009-08-07"),
                new DemoCodeData("585", "KET SC INDP PU COLLEGE", "NITTURU SHASHIK", "266543", "2009-08-08"),
                new DemoCodeData("586", "KET SC INDP PU COLLEGE", "JALIMARADA ANUS", "266834", "2009-08-09"),
                new DemoCodeData("587", "KHYKHA COMP PU COLLEGE", "SARKHAJI MADIYA", "266881", "2009-08-10"),
                new DemoCodeData("588", "KITTEL ART&COM PU COLLEGE", "DODDAMANI MANJU", "266953", "2009-08-11"),
                new DemoCodeData("589", "KLE COMMERCE PU COLLEGE", "MADIVALARA AJJA", "266982", "2009-08-12"),
                new DemoCodeData("590", "KLE IND PU COLLEGE", "RAYADURGADA PRA", "267026", "2009-08-13"),
                new DemoCodeData("591", "AYSHA PU COLLEGE", "PRIYANKA K H", "267584", "2009-08-14"),
                new DemoCodeData("592", "BBMP PU COLLEGE", "SANTHOSHA MALLA", "267689", "2009-08-15"),
                new DemoCodeData("593", "KLES IND PU COLLEGE", "MASALAVADADA NA", "267707", "2009-08-16"),
                new DemoCodeData("594", "KLES INDP PU COLLEGE", "TEJASWINI B N", "267856", "2009-08-17"),
                new DemoCodeData("595", "KLES INDP SC PU COLLEGE", "KARTHIKA S", "267966", "2009-08-18"),
                new DemoCodeData("596", "KLES PU COLLEGE", "SHAILAJA B", "268039", "2009-08-19"),
                new DemoCodeData("597", "KMES PU COLLEGE", "CHIKKERI VIJAYA", "268045", "2009-08-20"),
                new DemoCodeData("598", "KMS INDP PU COLLEGE", "PRABHU K", "268155", "2009-08-21"),
                new DemoCodeData("599", "KNVV'S ART&COM PU COLLEGE", "RADHIKA R", "268267", "2009-08-22"),
                new DemoCodeData("600", "KONIKA PU COLLEGE", "SARASWATHI GIDD", "268409", "2009-08-23"),
                new DemoCodeData("601", "KONNUR PU COLLEGE", "SINCHITH KUMAR ", "269798", "2009-08-24"),
                new DemoCodeData("602", "KOTRENANJAPPA PU COLLEGE", "SATHWADI AKASH", "270073", "2009-08-25"),
                new DemoCodeData("603", "KOTTURESHWARA PU COLLEGE", "GOWDRU MAMATHA", "270133", "2009-08-26"),
                new DemoCodeData("604", "KPCL PU COLLEGE", "GOWDRA PRIYANKA", "270206", "2009-08-27"),
                new DemoCodeData("605", "KPES INDP PU COLLEGE", "G M NAYANA", "270288", "2009-08-28"),
                new DemoCodeData("606", "KRES COMP PU COLLEGE", "M LAKSHMI", "270334", "2009-08-29"),
                new DemoCodeData("607", "KRISHNA ED SOC PU COLLEGE", "SATWADI PRASANN", "270395", "2009-08-30"),
                new DemoCodeData("608", "KRISHNA INDP PU COLLEGE", "KARADI LINGARAJ", "270427", "2009-08-31"),
                new DemoCodeData("609", "KRUPANIDHI PU COLLEGE", "PRAJWAL B", "270445", "2009-09-01"),
                new DemoCodeData("610", "KRUPANIDHI RES PU COLLEGE", "A K SWATHI", "270497", "2009-09-02"),
                new DemoCodeData("611", "KSS COMP PU COLLEGE", "K NAGARAJ", "270552", "2009-09-03"),
                new DemoCodeData("612", "KSSS PU COLLEGE", "SATVADI ANNAPPA", "270598", "2009-09-04"),
                new DemoCodeData("613", "KUMAR KANADE PU COLLEGE", "SATWADI LAKSHMI", "270661", "2009-09-05"),
                new DemoCodeData("614", "KUMARAN'S COMP PU COLLEGE", "GOWDRU PREMA", "270954", "2009-09-06"),
                new DemoCodeData("615", "KUMARESHWARA PU COLLEGE", "DURUGESHA B", "272744", "2009-09-07"),
                new DemoCodeData("616", "KUMBHASHRI PU COLLEGE", "BASAVARAJA K", "272807", "2009-09-08"),
                new DemoCodeData("617", "KUVEMPU IND PU COLLEGE", "MARUTHI B", "272869", "2009-09-09"),
                new DemoCodeData("618", "KVS COMP PU COLLEGE", "TUMBAPPA T", "272932", "2009-09-10"),
                new DemoCodeData("619", "KVS SG TODAKAR PU COLLEGE", "DODDAMANI ANJIN", "273141", "2009-09-11"),
                new DemoCodeData("620", "KVSR COMP PU COLLEGE", "SHREEMATHI N", "273213", "2009-09-12"),
                new DemoCodeData("621", "L SIDDAPPA PU COLLEGE", "PAVITHRA N", "273234", "2009-09-13"),
                new DemoCodeData("622", "LALITHA PU COLLEGE", "HALAMMA A", "273307", "2009-09-14"),
                new DemoCodeData("623", "LAVANYA COMP PU COLLEGE", "V K HANUMANTHA", "273375", "2009-09-15"),
                new DemoCodeData("624", "LCR INDIAN PU COLLEGE", "G M PARASHURAMA", "273982", "2009-09-16"),
                new DemoCodeData("625", "LES SC & ARTS PU COLLEGE", "KAVANA B P", "275224", "2009-09-17"),
                new DemoCodeData("626", "LINGARAJ A&C PU COLLEGE", "MAMATHA G C", "275302", "2009-09-18"),
                new DemoCodeData("627", "LIONS COMP PU COLLEGE", "THANUJA K R", "275321", "2009-09-19"),
                new DemoCodeData("628", "LIONS GIRLS PU COLLEGE", "M JYOTHI", "276259", "2009-09-20"),
                new DemoCodeData("629", "LIONS INDP PU COLLEGE", "M KANAKAMURTHY", "276331", "2009-09-21"),
                new DemoCodeData("630", "LITTLE FLOWER PU COLLEGE", "RANJITHA K", "276366", "2009-09-22"),
                new DemoCodeData("631", "LJ HOLI COMP PU COLLEGE", "V APPU NAIK", "276414", "2009-09-23"),
                new DemoCodeData("632", "LOKPAL PU COLLEGE", "MORAGERI KAVYA", "276458", "2009-09-24"),
                new DemoCodeData("633", "BENAKA PU COLLEGE", "K CHOUDAPPA", "276489", "2009-09-25"),
                new DemoCodeData("634", "LORVEN INDP PU COLLEGE", "DEVIKA KUMBARA", "276517", "2009-09-26"),
                new DemoCodeData("635", "LOURDU MATHA PU COLLEGE", "BANGALI RADHIKA", "276682", "2009-09-27"),
                new DemoCodeData("636", "LOWRY MEMORIAL PU COLLEGE", "KOTRESHA K", "276718", "2009-09-28"),
                new DemoCodeData("637", "LOYALA COMP PU COLLEGE", "A K MALLIKARJUN", "277476", "2009-09-29"),
                new DemoCodeData("638", "LOYOLA COMP PU COLLEGE", "A ABHI", "277502", "2009-09-30"),
                new DemoCodeData("639", "LOYOLA PU COLLEGE", "MAHESHA B", "277538", "2009-10-01"),
                new DemoCodeData("640", "LOYOLA YOMIURI PU COLLEGE", "Y T DARSHANA", "277579", "2009-10-02"),
                new DemoCodeData("641", "BES PU COLLEGE", "DARSHAN T", "277601", "2009-10-03"),
                new DemoCodeData("642", "M G C COMP PU COLLEGE", "B ANJINAPPA", "277642", "2009-10-04"),
                new DemoCodeData("643", "M KHARGE COMP PU COLLEGE", "MALLIKARJUNA BA", "277673", "2009-10-05"),
                new DemoCodeData("644", "M KRISHNA PU COLLEGE", "M NAVEENA", "277704", "2009-10-06"),
                new DemoCodeData("645", "M PRATAPSINGH PU COLLEGE", "K ARCHANA", "277837", "2009-10-07"),
                new DemoCodeData("646", "M S M S PU COLLEGE", "K SEEMA", "277909", "2009-10-08"),
                new DemoCodeData("647", "ASS PU COLLEGE", "I S RADHIKA", "277941", "2009-10-09"),
                new DemoCodeData("648", "MA TENGALIKAR PU COLLEGE", "PRAJWAL ITTIGUD", "277992", "2009-10-10"),
                new DemoCodeData("649", "MADANI PU COLLEGE", "KUMBARA VISHWAN", "278027", "2009-10-11"),
                new DemoCodeData("650", "MADEENA PU COLLEGE", "LINGARAJA B J", "278087", "2009-10-12"),
                new DemoCodeData("651", "MADELEINE PU COLLEGE", "ABDULSAMAD DAST", "280646", "2009-10-13"),
                new DemoCodeData("652", "MAHADEVA PU COLLEGE", "MUDALARA RENUKA", "284359", "2009-10-14"),
                new DemoCodeData("653", "MAHALINGASWAMY PU COLLEGE", "BHOVI ABHISHEK", "284471", "2009-10-15"),
                new DemoCodeData("654", "MAHANTASWAMY PU COLLEGE", "PUTTOLARA ANJAL", "284555", "2009-10-16"),
                new DemoCodeData("655", "MAHARSHI COMP PU COLLEGE", "SHREYAS KUNCHUR", "284596", "2009-10-17"),
                new DemoCodeData("656", "MAHASATEE PU COLLEGE", "HORAKERI KEERTH", "284713", "2009-10-18"),
                new DemoCodeData("657", "MAHATHMA PU COLLEGE", "HANUMANTHAPPA I", "284724", "2009-10-19"),
                new DemoCodeData("658", "MAHATHMAGANDHI PU COLLEGE", "UDAYAKUMARA MEN", "284776", "2009-10-20"),
                new DemoCodeData("659", "MAHATMA GANDHI PU COLLEGE", "SATTANMAPPARA H", "284797", "2009-10-21"),
                new DemoCodeData("660", "ASS PU COLLEGE", "BARIKARA HANUMA", "284882", "2009-10-22"),
                new DemoCodeData("661", "MALAPRABHA SS PU COLLEGE", "ANUSHA G", "284916", "2009-10-23"),
                new DemoCodeData("662", "MALATHESH INDP PU COLLEGE", "S MAHENDRA", "284938", "2009-10-24"),
                new DemoCodeData("663", "MALLIKA PU COLLEGE", "BUDDI MAHALINGE", "285016", "2009-10-25"),
                new DemoCodeData("664", "MALLIKARJUN PU COLLEGE", "MADIVALARA PAVI", "285058", "2009-10-26"),
                new DemoCodeData("665", "MALLIKARJUNA PU COLLEGE", "ADARSHA M", "285062", "2009-10-27"),
                new DemoCodeData("666", "MANASA INDP PU COLLEGE", "BUDDI CHANDRIKA", "285079", "2009-10-28"),
                new DemoCodeData("667", "MANASA PU COLLEGE", "REKHA K V", "285083", "2009-10-29"),
                new DemoCodeData("668", "MANDAVYA PU COLLEGE", "JAGALURU RENUKA", "285101", "2009-10-30"),
                new DemoCodeData("669", "MANGALORE INDP PU COLLEGE", "P S VIKAS", "285122", "2009-10-31"),
                new DemoCodeData("670", "MANIPAL PU COLLEGE", "VANAJA M N", "285176", "2009-11-01"),
                new DemoCodeData("671", "MANNAMMA COMP PU COLLEGE", "KAVANA H R", "285281", "2009-11-02"),
                new DemoCodeData("672", "MANORAMA PU COLLEGE", "SHIVARANJINI H", "285292", "2009-11-03"),
                new DemoCodeData("673", "MAPS PU COLLEGE", "SHWETHA K R", "285303", "2009-11-04"),
                new DemoCodeData("674", "MARGADARSHAN PU COLLEGE", "NIJBA MEHABUB A", "285843", "2009-11-05"),
                new DemoCodeData("675", "MARIMALLAPPA PU COLLEGE", "RAKSHITHA DUBBE", "286122", "2009-11-06"),
                new DemoCodeData("676", "MARIYAPPA COMP PU COLLEGE", "THANUSHRI BENNU", "286147", "2009-11-07"),
                new DemoCodeData("677", "MARUTHI GIRLS PU COLLEGE", "KANAKAMMA SATTU", "286151", "2009-11-08"),
                new DemoCodeData("678", "MARUTHI GIRLS PU COLLEGE", "ANUSHA KUDLIGI", "286166", "2009-11-09"),
                new DemoCodeData("679", "MARY MATHA PU COLLEGE", "SHREEDHARA ANGA", "286183", "2009-11-10"),
                new DemoCodeData("680", "MASTERS PU COLLEGE", "CHETAN KANCHIKE", "286232", "2009-11-11"),
                new DemoCodeData("681", "MATHOSHRI R H PU COLLEGE", "NAVINA GUGRI", "286279", "2009-11-12"),
                new DemoCodeData("682", "MATHOSRI ARGM PU COLLEGE", "RENUKA ELIGARA", "286285", "2009-11-13"),
                new DemoCodeData("683", "MATHRUCHAYA PU COLLEGE", "ANUSHA H", "286295", "2009-11-14"),
                new DemoCodeData("684", "MAULI PU COLLEGE", "BANGEPPANAVARA ", "286304", "2009-11-15"),
                new DemoCodeData("685", "MD KANSHIRAM PU COLLEGE", "MARIGONEPPA DAB", "286328", "2009-11-16"),
                new DemoCodeData("686", "MDS PU COLLEGE", "BANGEPPANAVARA ", "286335", "2009-11-17"),
                new DemoCodeData("687", "MDSP COMP PU COLLEGE", "ABHISHEKA PATRI", "286365", "2009-11-18"),
                new DemoCodeData("688", "MEC COMP PU COLLEGE", "N MANJUNATHA", "286382", "2009-11-19"),
                new DemoCodeData("689", "MELKAR GIRLS PU COLLEGE", "AKASHA H", "286417", "2009-11-20"),
                new DemoCodeData("690", "MES BRS PU COLLEGE", "KENCHAMMA KATTI", "286458", "2009-11-21"),
                new DemoCodeData("691", "MES CHAITHANYA PU COLLEGE", "BHOOMIKA SANDUR", "286563", "2009-11-22"),
                new DemoCodeData("692", "MES PU COLLEGE", "RAKSHITHA GUGRI", "286594", "2009-11-23"),
                new DemoCodeData("693", "AVK COLLEGE", "BHAVANA J H", "287925", "2009-11-24"),
                new DemoCodeData("694", "AVK PU COLLEGE", "HALESH H", "287936", "2009-11-25"),
                new DemoCodeData("695", "AYSHA PU COLLEGE", "LAKSHMI HIREKAB", "287953", "2009-11-26"),
                new DemoCodeData("696", "MGC PU COLLEGE", "BHARAMAPPA K", "287975", "2009-11-27"),
                new DemoCodeData("697", "MGD APPA WOMEN PU COLLEGE", "KOTRESH GADDER", "287984", "2009-11-28"),
                new DemoCodeData("698", "MGM COMP PU COLLEGE", "KARADI BHEEMAPP", "287999", "2009-11-29"),
                new DemoCodeData("699", "MGM PU COLLEGE", "KAMMARA SUDARSH", "288007", "2009-11-30"),
                new DemoCodeData("700", "MGMK PU COLLEGE", "HIREHADAGALI SU", "290964", "2009-12-01"),
                new DemoCodeData("701", "MGV CHINIWAR PU COLLEGE", "N M AFREEN BANU", "290988", "2009-12-02"),
                new DemoCodeData("702", "MH MEMORIAL PU COLLEGE", "KANAHALLI VIJAY", "291043", "2009-12-03"),
                new DemoCodeData("703", "MH MENASIGI PU COLLEGE", "B AFREEN BANU", "291066", "2009-12-04"),
                new DemoCodeData("704", "MILAGRES PU COLLEGE", "K SYED", "291083", "2009-12-05"),
                new DemoCodeData("705", "MILIND COMP PU COLLEGE", "KANAHALLI AJEYA", "291146", "2009-12-06"),
                new DemoCodeData("706", "MILLATH INDP PU COLLEGE", "MAGALADA RUCHIT", "291181", "2009-12-07"),
                new DemoCodeData("707", "MIRANDA COMP PU COLLEGE", "HARSHA R V", "291414", "2009-12-08"),
                new DemoCodeData("708", "MKCPM PU COLLEGE", "GANESH N B", "291479", "2009-12-09"),
                new DemoCodeData("709", "BBMP PU COLLEGE", "HAREESHA B M", "291692", "2009-12-10"),
                new DemoCodeData("710", "BENAKA PU COLLEGE", "KUPPANNA RAMATH", "297585", "2009-12-11"),
                new DemoCodeData("711", "MMK IND PU COLLEGE", "DODDANAMADA DEE", "301253", "2009-12-12"),
                new DemoCodeData("712", "MMK& SDM GIRLS PU COLLEGE", "DODDANAMADA SUD", "301278", "2009-12-13"),
                new DemoCodeData("713", "MMU PU COLLEGE", "KAMATHARA POOJA", "301316", "2009-12-14"),
                new DemoCodeData("714", "MODEL NATIONAL PU COLLEGE", "S M RAZIYA SULT", "301337", "2009-12-15"),
                new DemoCodeData("715", "MODERN COMP PU COLLEGE", "S RAKSHITA", "301371", "2009-12-16"),
                new DemoCodeData("716", "MODERN INDP PU COLLEGE", "DIVYA KASHINATH", "304053", "2009-12-17"),
                new DemoCodeData("717", "MOOKAMBIKA PU COLLEGE", "PRADEEP RAJU JA", "304308", "2009-12-18"),
                new DemoCodeData("718", "MOTHER TERESAS PU COLLEGE", "YUVARAJ SHEVU J", "304319", "2009-12-19"),
                new DemoCodeData("719", "MOTHER THERESA PU COLLEGE", "AKASH MASABINAL", "304322", "2009-12-20"),
                new DemoCodeData("720", "MOUNT CARMEL PU COLLEGE", "PRAVEEN SARENNA", "307342", "2009-12-21"),
                new DemoCodeData("721", "MOUNTAINVIEW PU COLLEGE", "RAMESH CHALAWAD", "307386", "2009-12-22"),
                new DemoCodeData("722", "MPES SDM PU COLLEGE", "KRISHNA KORTI", "307397", "2009-12-23"),
                new DemoCodeData("723", "MPL SASTRY IND PU COLLEGE", "SADASHIV MADAR", "307432", "2009-12-24"),
                new DemoCodeData("724", "BES PU COLLEGE", "SANTOSH VADDAR", "307506", "2009-12-25"),
                new DemoCodeData("725", "MPMG WOMENS PU COLLEGE", "KISHORKUMAR CHO", "307573", "2009-12-26"),
                new DemoCodeData("726", "MR AMBEDKAR PU COLLEGE", "SACHIN KYAMANNA", "309952", "2009-12-27"),
                new DemoCodeData("727", "MRA GIRLS COMP PU COLLEGE", "MANIKANTH NINGA", "310027", "2009-12-28"),
                new DemoCodeData("728", "MRA INDP PU COLLEGE", "SURESH BENNUR", "310113", "2009-12-29"),
                new DemoCodeData("729", "MS IRANI PU COLLEGE", "VIKAS RACHAPPA ", "310179", "2009-12-30"),
                new DemoCodeData("730", "ASS PU COLLEGE", "ARKAN KALAGURKI", "310203", "2009-12-31"),
                new DemoCodeData("731", "MUNDAJE PU COLLEGE", "LAXMI SADAPPA B", "312244", "2009-01-01"),
                new DemoCodeData("732", "MUNICIPAL PU COLLEGE", "TANVIR PHEERAMA", "312272", "2009-01-02"),
                new DemoCodeData("733", "MURALI PU COLLEGE", "PAVITRA SHREEMA", "312357", "2009-01-03"),
                new DemoCodeData("734", "MURNAD PU COLLEGE", "CHIGARI MANIKAN", "313471", "2009-01-04"),
                new DemoCodeData("735", "MV PATTAN COMP PU COLLEGE", "P AKASH CHOWHAN", "313489", "2009-01-05"),
                new DemoCodeData("736", "MV WOMENS COMP PU COLLEGE", "SREEKANTHA L M", "313493", "2009-01-06"),
                new DemoCodeData("737", "MYSORE INST PU COLLEGE", "RAJU Y", "313534", "2009-01-07"),
                new DemoCodeData("738", "N A PU COLLEGE", "PRADEEPA B", "313604", "2009-01-08"),
                new DemoCodeData("739", "NADAPRABHU PU COLLEGE", "SUNIL NAIK M", "313629", "2009-01-09"),
                new DemoCodeData("740", "NAGARA PU COLLEGE", "JOGAPLA UMESH", "313646", "2009-01-10"),
                new DemoCodeData("741", "NAGARJUNA PU COLLEGE", "P TARUN CHOWHAN", "313671", "2009-01-11"),
                new DemoCodeData("742", "AVK COLLEGE", "H CHANNABASAPPA", "313709", "2009-01-12"),
                new DemoCodeData("743", "AVK COLLEGE", "KANTHESHA DYAVA", "313723", "2009-01-13"),
                new DemoCodeData("744", "NALANDA COMP PU COLLEGE", "HOLALARA NAGARA", "313748", "2009-01-14"),
                new DemoCodeData("745", "NALANDA INTL PU COLLEGE", "RUHAN PATHAN A", "313781", "2009-01-15"),
                new DemoCodeData("746", "NALANDA PU COLLEGE", "MONIKA C", "314027", "2009-01-16"),
                new DemoCodeData("747", "AYSHA PU COLLEGE", "HALESHA LAMBANI", "314044", "2009-01-17"),
                new DemoCodeData("748", "NAMMA MANVI PU COLLEGE", "M ARPITHA", "314072", "2009-01-18"),
                new DemoCodeData("749", "NANDI CITY PU COLLEGE", "GOVINDA G", "314115", "2009-01-19"),
                new DemoCodeData("750", "NANDI PU COLLEGE", "MATTURU AKSHATH", "314221", "2009-01-20"),
                new DemoCodeData("751", "NANDI PU COLLEGE", "PAVAN KUMAR R", "314298", "2009-01-21"),
                new DemoCodeData("752", "NANDISWARA IND PU COLLEGE", "SANGEETA DORANA", "315108", "2009-01-22"),
                new DemoCodeData("753", "NARAYANAGURU PU COLLEGE", "KAVERI KARIGAR", "315164", "2009-01-23"),
                new DemoCodeData("754", "NARENDRA PU COLLEGE", "VIDYASHRI DALAW", "315192", "2009-01-24"),
                new DemoCodeData("755", "NATIONAL COMP PU COLLEGE", "IRANNA LINGARED", "315228", "2009-01-25"),
                new DemoCodeData("756", "NATIONAL INDP PU COLLEGE", "MAHESH BANDIVAD", "318317", "2009-01-26"),
                new DemoCodeData("757", "NATIONAL PU COLLEGE", "VIRUPAKSHI HIRE", "318429", "2009-01-27"),
                new DemoCodeData("758", "NAVACHETHANA PU COLLEGE", "ASHMA SONNAD", "318545", "2009-01-28"),
                new DemoCodeData("759", "NAVAYUGA PU COLLEGE", "SACHIN KALABURK", "318596", "2009-01-29"),
                new DemoCodeData("760", "NAVODAYA COMP PU COLLEGE", "SACHIN ALAMATTI", "321024", "2009-01-30"),
                new DemoCodeData("761", "NAVODAYA PU COLLEGE", "VIJAYALAKSHMI M", "321087", "2009-01-31"),
                new DemoCodeData("762", "BBMP PU COLLEGE", "AMEERKHAN JAINA", "321176", "2009-02-01"),
                new DemoCodeData("763", "NB SAVANTH PU COLLEGE", "ASHWINI HALAGLI", "321207", "2009-02-02"),
                new DemoCodeData("764", "ND COMP PU COLLEGE", "GOURAMMA NINGAP", "321472", "2009-02-03"),
                new DemoCodeData("765", "ND DARBAR PU COLLEGE", "KEERTI MANAGOOL", "321531", "2009-02-04"),
                new DemoCodeData("766", "NEGINHAL COMP PU COLLEGE", "MAJJIGERI KALPA", "322247", "2009-02-05"),
                new DemoCodeData("767", "NEHRU MEMORIAL PU COLLEGE", "K V POOJA", "322258", "2009-02-06"),
                new DemoCodeData("768", "NEW ALLIANCE PU COLLEGE", "KUNCHOORU MARUT", "322286", "2009-02-07"),
                new DemoCodeData("769", "NEW COMP PU COLLEGE", "KUNCHOORU ANILA", "322297", "2009-02-08"),
                new DemoCodeData("770", "NEW ENGLISH PU COLLEGE", "BULDERA RAMESHA", "322321", "2009-02-09"),
                new DemoCodeData("771", "NEW HORIZON PU COLLEGE", "KOLLERA VINAYAK", "322332", "2009-02-10"),
                new DemoCodeData("772", "NEW MODERN PU COLLEGE", "KOLLERA SUMANTH", "322384", "2009-02-11"),
                new DemoCodeData("773", "NEW PU COLLEGE", "BADIGERA PRABHU", "322421", "2009-02-12"),
                new DemoCodeData("774", "NEW PUBLIC PU COLLEGE", "KUNIRAMAPPARA L", "322461", "2009-02-13"),
                new DemoCodeData("775", "NIDAVANI PU COLLEGE", "KOLLER VEENA", "322483", "2009-02-14"),
                new DemoCodeData("776", "NIMISHAMBA PU COLLEGE", "UPPARA ANUSHA", "322505", "2009-02-15"),
                new DemoCodeData("777", "NIRANJANASWAMY PU COLLEGE", "KURUBANAHALLI N", "322528", "2009-02-16"),
                new DemoCodeData("778", "NIRMALA COMP PU COLLEGE", "KALLALLI KENCHA", "322552", "2009-02-17"),
                new DemoCodeData("779", "NISARGA INDP PU COLLEGE", "GEETHA K R", "322566", "2009-02-18"),
                new DemoCodeData("780", "NISARGA PU COLLEGE", "SINDHU BASAVARA", "322593", "2009-02-19"),
                new DemoCodeData("781", "BENAKA PU COLLEGE", "D PRIYANKA", "325178", "2009-02-20"),
                new DemoCodeData("782", "NISHARA PU COLLEGE", "RAVI N", "325258", "2009-02-21"),
                new DemoCodeData("783", "BES PU COLLEGE", "P PAVITHRA", "325316", "2009-02-22"),
                new DemoCodeData("784", "ASS PU COLLEGE", "J H NAGARAJA", "325379", "2009-02-23"),
                new DemoCodeData("785", "NITTE SAM PU COLLEGE", "G SHREENIVASA", "325401", "2009-02-24"),
                new DemoCodeData("786", "NIVEDITA INDP PU COLLEGE", "PRAGATI RAVINDR", "328571", "2009-02-25"),
                new DemoCodeData("787", "NOBLE PU COLLEGE", "POOJA MOHAN PAT", "328614", "2009-02-26"),
                new DemoCodeData("788", "AVK COLLEGE", "BANAKARA RAJESH", "331496", "2009-02-27"),
                new DemoCodeData("789", "NOORUNDESHWARA PU COLLEGE", "MOHAMMAD KAIF", "331579", "2009-02-28"),
                new DemoCodeData("790", "NORMA FENDRICH PU COLLEGE", "M MANOJ KUMAR", "331838", "2009-03-01"),
                new DemoCodeData("791", "NR PATIL PU COLLEGE", "S R MANASA", "332069", "2009-03-02"),
                new DemoCodeData("792", "AVK PU COLLEGE", "LAXMI YARANAL", "334485", "2009-03-03"),
                new DemoCodeData("793", "NS AMMANAGI PU COLLEGE", "GOUDAPLA RAKSHA", "334572", "2009-03-04"),
                new DemoCodeData("794", "AYSHA PU COLLEGE", "GOUDAPLA MANASA", "334757", "2009-03-05"),
                new DemoCodeData("795", "BBMP PU COLLEGE", "GOWDAPLA KAVYA ", "334783", "2009-03-06"),
                new DemoCodeData("796", "NTSS PU COLLEGE", "N LAKSHMI", "337911", "2009-03-07"),
                new DemoCodeData("797", "NUTANA INDP SC PU COLLEGE", "KAREKANAHALLI R", "338078", "2009-03-08"),
                new DemoCodeData("798", "OBALI INDP PU COLLEGE", "GHATINA SUNITHA", "338208", "2009-03-09"),
                new DemoCodeData("799", "BENAKA PU COLLEGE", "ARASIKERI KALAM", "338221", "2009-03-10"),
                new DemoCodeData("800", "OM PU COLLEGE", "N ASHWINI", "338322", "2009-03-11"),
                new DemoCodeData("801", "OM SAI PU COLLEGE", "SHAMA B K", "338366", "2009-03-12"),
                new DemoCodeData("802", "ORIENTAL PU COLLEGE", "A SUPRITHA", "338397", "2009-03-13"),
                new DemoCodeData("803", "OXFORD COMP PU COLLEGE", "ROJA ANGADI", "341088", "2009-03-14"),
                new DemoCodeData("804", "OXFORD INDP PU COLLEGE", "D H ARUN", "341094", "2009-03-15"),
                new DemoCodeData("805", "OXFORD JAIN PU COLLEGE", "K NAGARAJA", "341126", "2009-03-16"),
                new DemoCodeData("806", "OXFORD PU COLLEGE", "K H M SHASHANK", "341131", "2009-03-17"),
                new DemoCodeData("807", "PADMA PU COLLEGE", "B N VEERESH", "341205", "2009-03-18"),
                new DemoCodeData("808", "BES PU COLLEGE", "SUSMITA RAMESH ", "341471", "2009-03-19"),
                new DemoCodeData("809", "PADMAVATHI PU COLLEGE", "CHANDRASHEKHARA", "341679", "2009-03-20"),
                new DemoCodeData("810", "PADUA COMP PU COLLEGE", "BHORAMMA AYYAPP", "341939", "2009-03-21"),
                new DemoCodeData("811", "PALOMA PU COLLEGE", "K M PRADEEP", "341948", "2009-03-22"),
                new DemoCodeData("812", "PANCHAJANYA VP PU COLLEGE", "SANIKA SANJAY S", "344245", "2009-03-23"),
                new DemoCodeData("813", "PANDIT NEHRU PU COLLEGE", "SONALI RAJU RAV", "344261", "2009-03-24"),
                new DemoCodeData("814", "PAPERTOWN INDP PU COLLEGE", "PRATHAMESH KALA", "344281", "2009-03-25"),
                new DemoCodeData("815", "PARIGNANA INDP PU COLLEGE", "NEHA RAMKRUSHNA", "344342", "2009-03-26"),
                new DemoCodeData("816", "PARIVARTANA PU COLLEGE", "PRADEEP RAJU MA", "344359", "2009-03-27"),
                new DemoCodeData("817", "PARIVARTHANA PU COLLEGE", "DINESH KUMARA Y", "347636", "2009-03-28"),
                new DemoCodeData("818", "PATEL GULLAPPA PU COLLEGE", "SANGEETHA P", "347772", "2009-03-29"),
                new DemoCodeData("819", "PAVAN COMP PU COLLEGE", "APPUNAIK C", "347834", "2009-03-30"),
                new DemoCodeData("820", "PAVAN PU COLLEGE", "DARSHAN NAIK M", "347849", "2009-03-31"),
                new DemoCodeData("821", "PDJ PU COLLEGE", "SAGARNAIK P", "347901", "2009-04-01"),
                new DemoCodeData("822", "ASS PU COLLEGE", "SAHERA BANU", "354574", "2009-04-02"),
                new DemoCodeData("823", "PES INDP PU COLLEGE", "AJAY KUMAR S", "354596", "2009-04-03"),
                new DemoCodeData("824", "PES PU COLLEGE", "HUSAIN SAB MOOL", "354629", "2009-04-04"),
                new DemoCodeData("825", "AVK COLLEGE", "UMA BAI S", "354664", "2009-04-05"),
                new DemoCodeData("826", "PHOOLBUN COMP PU COLLEGE", "SHAZIYA BANU P", "354686", "2009-04-06"),
                new DemoCodeData("827", "PKB COMP PU COLLEGE", "DARSHAN H", "354713", "2009-04-07"),
                new DemoCodeData("828", "POMPEI PU COLLEGE", "MALIK RIHAN", "354724", "2009-04-08"),
                new DemoCodeData("829", "POORNA PU COLLEGE", "VISHAL C", "354746", "2009-04-09"),
                new DemoCodeData("830", "POORNAPRAJNA PU COLLEGE", "AYISHA BANU", "354804", "2009-04-10"),
                new DemoCodeData("831", "POORNIMA PU COLLEGE", "JABEER M", "354815", "2009-04-11"),
                new DemoCodeData("832", "PPSS COMP PU COLLEGE", "SANJANA S", "355824", "2009-04-12"),
                new DemoCodeData("833", "PRABHAVITUM SCIN PU COLLEGE", "ANJALI S NAYAKA", "355835", "2009-04-13"),
                new DemoCodeData("834", "PRABHU A&S&C PU COLLEGE", "PAVITHRA S", "355846", "2009-04-14"),
                new DemoCodeData("835", "PRAGATHI COMP PU COLLEGE", "KANNIKA H", "355902", "2009-04-15"),
                new DemoCodeData("836", "PRAGATHI GIRLS PU COLLEGE", "AKASH K S", "356018", "2009-04-16"),
                new DemoCodeData("837", "PRAGATHI PU COLLEGE", "ANUSHA B", "356021", "2009-04-17"),
                new DemoCodeData("838", "PRAKRUTHI PU COLLEGE", "SONIYA BANU", "356067", "2009-04-18"),
                new DemoCodeData("839", "AVK PU COLLEGE", "SWATHI R", "356154", "2009-04-19"),
                new DemoCodeData("840", "PRARTHANA PU COLLEGE", "RAGHAVA R", "359317", "2009-04-20"),
                new DemoCodeData("841", "PRAYUKTHI PU COLLEGE", "B D ANUSHA", "359331", "2009-04-21"),
                new DemoCodeData("842", "PRE UNIVERSITY COLLEGE", "M R VEERESHA", "359354", "2009-04-22"),
                new DemoCodeData("843", "PREETI COMP PU COLLEGE", "DEVARAJ B D", "359401", "2009-04-23"),
                new DemoCodeData("844", "PREMIER S&C PU COLLEGE", "T H BASAVARAJA", "359425", "2009-04-24"),
                new DemoCodeData("845", "PRERANA PU COLLEGE", "S B MANJUNATHA", "359444", "2009-04-25"),
                new DemoCodeData("846", "PRESIDENCY PU COLLEGE", "ANNAPURNA H N", "359452", "2009-04-26"),
                new DemoCodeData("847", "PRIDE INDP PU COLLEGE", "M B MANOJ", "359471", "2009-04-27"),
                new DemoCodeData("848", "PRIMUS PU COLLEGE", "ABHISHEKA M B", "359486", "2009-04-28"),
                new DemoCodeData("849", "PRIYADARSHINI PU COLLEGE", "AISHWARYA B M", "359493", "2009-04-29"),
                new DemoCodeData("850", "PRIYANKA INDP PU COLLEGE", "K V RAMYA", "359512", "2009-04-30"),
                new DemoCodeData("851", "PRM COMP PU COLLEGE", "B P DHRUVA", "359528", "2009-05-01"),
                new DemoCodeData("852", "PROGRESSIVE PU COLLEGE", "MARUTHI G D", "359536", "2009-05-02"),
                new DemoCodeData("853", "PTES PU COLLEGE", "K H PADMAPRIYA", "359574", "2009-05-03"),
                new DemoCodeData("854", "AYSHA PU COLLEGE", "KAVANA B K", "359587", "2009-05-04"),
                new DemoCodeData("855", "PUPIL TREE PU COLLEGE", "B V KARTHIKA", "361563", "2009-05-05"),
                new DemoCodeData("856", "PUSHPATHAI PU COLLEGE", "UMESHA U R", "361596", "2009-05-06"),
                new DemoCodeData("857", "BBMP PU COLLEGE", "LAKSHMI G B", "361605", "2009-05-07"),
                new DemoCodeData("858", "BENAKA PU COLLEGE", "B O MALATHESHA", "361666", "2009-05-08"),
                new DemoCodeData("859", "PVS COMP PU COLLEGE", "B M VIJAYALASHM", "361679", "2009-05-09"),
                new DemoCodeData("860", "QUWATHUL ISLAM PU COLLEGE", "K P RUDRESH", "361688", "2009-05-10"),
                new DemoCodeData("861", "R PARENTS ASSO PU COLLEGE", "P A POOJA", "361692", "2009-05-11"),
                new DemoCodeData("862", "R REHAN INDP PU COLLEGE", "MANJUNATHA H B", "361705", "2009-05-12"),
                new DemoCodeData("863", "RA PARVATHE PU COLLEGE", "D N BHARAMANA G", "361734", "2009-05-13"),
                new DemoCodeData("864", "RADHABAI DG PU COLLEGE", "RAMKUMAR SHEKAP", "364954", "2009-05-14"),
                new DemoCodeData("865", "RAFFLES INT PU COLLEGE", "SIDAGIRI", "365524", "2009-05-15"),
                new DemoCodeData("866", "RAJAJINAGAR PU COLLEGE", "VINOD MARUTI MA", "367149", "2009-05-16"),
                new DemoCodeData("867", "RAJESAB SAYYAD PU COLLEGE", "AKSHATA ASHOK R", "367225", "2009-05-17"),
                new DemoCodeData("868", "RAJESWARI COMP PU COLLEGE", "TAISIN SAIBANAS", "367245", "2009-05-18"),
                new DemoCodeData("869", "RAJIV GANDHI PU COLLEGE", "RANJANA ANKUSH ", "367263", "2009-05-19"),
                new DemoCodeData("870", "RAM & RAJ COMP PU COLLEGE", "BHAGYASHREE KRI", "367272", "2009-05-20"),
                new DemoCodeData("871", "RAMAKUNJESHWAR PU COLLEGE", "AKSHATA SADASHI", "367327", "2009-05-21"),
                new DemoCodeData("872", "RANIKERE COMP PU COLLEGE", "SAGAR LALASING ", "369335", "2009-05-22"),
                new DemoCodeData("873", "RASHTRIYA P U COLLEGE", "ASHWINI RATHOD", "369382", "2009-05-23"),
                new DemoCodeData("874", "RASTROTHANA PU COLLEGE", "PRIYANKA DHARU ", "369424", "2009-05-24"),
                new DemoCodeData("875", "RBANMS EVENING PU COLLEGE", "LALITA PAPA CHA", "369477", "2009-05-25"),
                new DemoCodeData("876", "BES PU COLLEGE", "VISHAL POMU CHA", "369499", "2009-05-26"),
                new DemoCodeData("877", "RD COMP PU COLLEGE", "SHRAVANKUMAR DH", "369501", "2009-05-27"),
                new DemoCodeData("878", "RD PATIL PU COLLEGE", "SONALI SURESH R", "369576", "2009-05-28"),
                new DemoCodeData("879", "RDS INDP PU COLLEGE", "VIKRAM SHRIMANT", "369587", "2009-05-29"),
                new DemoCodeData("880", "REDDY VEERANNA PU COLLEGE", "DADDENARA KAVER", "375222", "2009-05-30"),
                new DemoCodeData("881", "REDDYJANASANGA PU COLLEGE", "H DAKSHAYANI", "375428", "2009-05-31"),
                new DemoCodeData("882", "RENUKA COMP PU COLLEGE", "M SUJATHA", "375548", "2009-06-01"),
                new DemoCodeData("883", "RENUKADEVI IND PU COLLEGE", "C AFHRIN", "375624", "2009-06-02"),
                new DemoCodeData("884", "REVA PU COLLEGE", "MEHATAJ K", "375803", "2009-06-03"),
                new DemoCodeData("885", "ASS PU COLLEGE", "JAMAKHANDI ASLA", "375874", "2009-06-04"),
                new DemoCodeData("886", "RG KASHETTY PU COLLEGE", "NOORJAHAN", "375931", "2009-06-05"),
                new DemoCodeData("887", "RG PU COLLEGE", "PREMA GOPAL RAT", "376821", "2009-06-06"),
                new DemoCodeData("888", "RGM COMP PU COLLEGE", "YUVARAJ RATHOD", "376849", "2009-06-07"),
                new DemoCodeData("889", "RGV PU COLLEGE", "VANITA CHAVHAN", "376855", "2009-06-08"),
                new DemoCodeData("890", "RJS EVENING PU COLLEGE", "ANJALI CHAVAN", "376867", "2009-06-09"),
                new DemoCodeData("891", "AVK COLLEGE", "VIRESH PATTAR", "376897", "2009-06-10"),
                new DemoCodeData("892", "RLS COMP PU COLLEGE", "SANGEETA SOMESH", "377051", "2009-06-11"),
                new DemoCodeData("893", "RM BIRADAR PU COLLEGE", "VILAS DAMU CHAV", "386125", "2009-06-12"),
                new DemoCodeData("894", "RMG PU COLLEGE", "PREETI IRAPPA M", "386288", "2009-06-13"),
                new DemoCodeData("895", "RMS PU COLLEGE", "MADHUMATI SHANT", "386362", "2009-06-14"),
                new DemoCodeData("896", "RN SHETTY INDP PU COLLEGE", "SHREEKANT UKKAL", "386407", "2009-06-15"),
                new DemoCodeData("897", "RN SHETTY PU COLLEGE", "SANJANA SURESH ", "386424", "2009-06-16"),
                new DemoCodeData("898", "RNS COMP PU COLLEGE", "ROOPA AMBALANUR", "386549", "2009-06-17"),
                new DemoCodeData("899", "RNVK GIRLS PU COLLEGE", "SANGEETA ANGADI", "386716", "2009-06-18"),
                new DemoCodeData("900", "ROSA MYSTICA PU COLLEGE", "KALPANA WADED", "386775", "2009-06-19"),
                new DemoCodeData("901", "ROSARIO COMP PU COLLEGE", "MANJUNATH SAJJA", "386838", "2009-06-20"),
                new DemoCodeData("902", "ROTARY COMP PU COLLEGE", "M NISHA", "388408", "2009-06-21"),
                new DemoCodeData("903", "ROTARY PU COLLEGE", "KUNCHOORU BIBIJ", "388428", "2009-06-22"),
                new DemoCodeData("904", "ROYAL COMP PU COLLEGE", "MANJULA BAI", "388458", "2009-06-23"),
                new DemoCodeData("905", "ROYAL PALACE PU COLLEGE", "M ALIYA", "388472", "2009-06-24"),
                new DemoCodeData("906", "ROYAL PU COLLEGE", "GANGAMMA ARASAN", "388512", "2009-06-25"),
                new DemoCodeData("907", "RR GIRLS COMP PU COLLEGE", "B DEEKSHA", "388528", "2009-06-26"),
                new DemoCodeData("908", "RS WAGLE INDP PU COLLEGE", "POOJA BAI", "388538", "2009-06-27"),
                new DemoCodeData("909", "RSDC AMS INDP PU COLLEGE", "KAVITHA K", "388556", "2009-06-28"),
                new DemoCodeData("910", "AVK PU COLLEGE", "LAKSHMI BAI", "388565", "2009-06-29"),
                new DemoCodeData("911", "RTES PU COLLEGE", "TOPINA REKHA", "388578", "2009-06-30"),
                new DemoCodeData("912", "RURAL PU COLLEGE", "RATNAMMA T", "388592", "2009-07-01"),
                new DemoCodeData("913", "AYSHA PU COLLEGE", "M SUDHA", "388639", "2009-07-02"),
                new DemoCodeData("914", "RV COMP PU COLLEGE", "SHALINI KALLAHA", "388658", "2009-07-03"),
                new DemoCodeData("915", "RVP COMP PU COLLEGE", "AMRUTHA B", "388661", "2009-07-04"),
                new DemoCodeData("916", "RVS SMRS COMP PU COLLEGE", "N TAMANNA", "388701", "2009-07-05"),
                new DemoCodeData("917", "BBMP PU COLLEGE", "SAHANA KUMBARA", "388723", "2009-07-06"),
                new DemoCodeData("918", "S G M PU COLLEGE", "RAMANAGOUDRA RA", "388753", "2009-07-07"),
                new DemoCodeData("919", "S GOLLALESHWAR PU COLLEGE", "G NEELABAI", "388789", "2009-07-08"),
                new DemoCodeData("920", "S M S PU COLLEGE", "DANKI TABASUM", "388795", "2009-07-09"),
                new DemoCodeData("921", "S S M S PU COLLEGE", "C ANJALI", "388806", "2009-07-10"),
                new DemoCodeData("922", "S S SWAMIJI PU COLLEGE", "LAKSHMI BANAKAR", "388824", "2009-07-11"),
                new DemoCodeData("923", "S Y COMP P U COLLEGE", "ARASIKERI MUSKA", "388904", "2009-07-12"),
                new DemoCodeData("924", "SA COMP PU COLLEGE", "ARSHIYA K", "388922", "2009-07-13"),
                new DemoCodeData("925", "SACRED HEART PU COLLEGE", "SANGEETHA N", "388963", "2009-07-14"),
                new DemoCodeData("926", "SACRED PU COLLEGE", "DAFEDAR MAMAJAN", "388996", "2009-07-15"),
                new DemoCodeData("927", "SADALGA COMP PU COLLEGE", "SINDHU J", "389008", "2009-07-16"),
                new DemoCodeData("928", "BENAKA PU COLLEGE", "CHITRAVATHI B", "389019", "2009-07-17"),
                new DemoCodeData("929", "SADVIDYA COMP PU COLLEGE", "HAMPASAGARA AYI", "389026", "2009-07-18"),
                new DemoCodeData("930", "SAEC COMP PU COLLEGE", "NANDYALA SOUNDA", "389057", "2009-07-19"),
                new DemoCodeData("931", "SAGB COMP PU COLLEGE", "LAKSHMI K", "389064", "2009-07-20"),
                new DemoCodeData("932", "SAHYADRI COMP PU COLLEGE", "MAGALADA SAVITH", "389111", "2009-07-21"),
                new DemoCodeData("933", "SAHYADRI PU COLLEGE", "MANDALA MUSKAN", "389153", "2009-07-22"),
                new DemoCodeData("934", "SAI PRESIDENCY PU COLLEGE", "KARIYAMMA K", "389209", "2009-07-23"),
                new DemoCodeData("935", "SAINIKETHAN PU COLLEGE", "MEENAKSHI N C", "396111", "2009-07-24"),
                new DemoCodeData("936", "SAJJALASHREE PU COLLEGE", "RANJITHA D R", "396692", "2009-07-25"),
                new DemoCodeData("937", "SALIHATH GIRLS PU COLLEGE", "ASHA K", "396785", "2009-07-26"),
                new DemoCodeData("938", "SAMARTH PU COLLEGE", "SOUNDARYA V", "396864", "2009-07-27"),
                new DemoCodeData("939", "SAMBHRAM PU COLLEGE", "B N CHETHAN", "396898", "2009-07-28"),
                new DemoCodeData("940", "SAMRE PU SCIENCE COLLEGE", "RAMESH H", "399522", "2009-07-29"),
                new DemoCodeData("941", "SANGAMESHWAR PU COLLEGE", "RENUKA H", "399546", "2009-07-30"),
                new DemoCodeData("942", "SANJEEVINI PU COLLEGE", "SHRIDEVI K", "399551", "2009-07-31"),
                new DemoCodeData("943", "SANKALPA INDP PU COLLEGE", "CHANDRAPPA G", "399613", "2009-08-01"),
                new DemoCodeData("944", "SANKALPA PU COLLEGE", "SATHISHA G", "399631", "2009-08-02"),
                new DemoCodeData("945", "SANKET COMP PU COLLEGE", "AJJAIAH K", "399654", "2009-08-03"),
                new DemoCodeData("946", "SANKETHA INDP PU COLLEGE", "AKSHATHA D", "399678", "2009-08-04"),
                new DemoCodeData("947", "SANMARG PU COLLEGE", "POOJA S", "399695", "2009-08-05"),
                new DemoCodeData("948", "BES PU COLLEGE", "DEEPA H", "399709", "2009-08-06"),
                new DemoCodeData("949", "SAPTAGIRI COMP PU COLLEGE", "DADARADAMMA C", "399727", "2009-08-07"),
                new DemoCodeData("950", "SAQ COMP PU COLLEGE", "MANJUNATHA J", "399737", "2009-08-08"),
                new DemoCodeData("951", "SAR PATIL PU COLLEGE", "SANDEEPA.B", "399841", "2009-08-09"),
                new DemoCodeData("952", "SARADA VILAS PU COLLEGE", "AKHILESH G YARR", "404989", "2009-08-10"),
                new DemoCodeData("953", "SARANGA PU COLLEGE", "SHARANABASAVA", "405035", "2009-08-11"),
                new DemoCodeData("954", "SARASWATHI PU COLLEGE", "POOJA", "405069", "2009-08-12"),
                new DemoCodeData("955", "SARVAJANIKA PU COLLEGE", "P DIVYA", "405088", "2009-08-13"),
                new DemoCodeData("956", "SARVAJNA COMP PU COLLEGE", "NITIN KUMAR", "405116", "2009-08-14"),
                new DemoCodeData("957", "SARVODAYA COMP PU COLLEGE", "NARASAREDDY", "405137", "2009-08-15"),
                new DemoCodeData("958", "SARVODAYA P U SCIENCE COLLEGE ", "S VINAY", "405172", "2009-08-16"),
                new DemoCodeData("959", "SARVODAYA PU COLLEGE", "SURAJ", "405196", "2009-08-17"),
                new DemoCodeData("960", "SARVODAYA SS PU COLLEGE", "UMESH", "405214", "2009-08-18"),
                new DemoCodeData("961", "ASS PU COLLEGE", "T SHREYA", "405341", "2009-08-19"),
                new DemoCodeData("962", "SATHYAM PU COLLEGE", "NESHA LAVANYA T", "405466", "2009-08-20"),
                new DemoCodeData("963", "SAVITRIBAIPULE PU COLLEGE", "SHRIYA", "405499", "2009-08-21"),
                new DemoCodeData("964", "SB DESAI PU COLLEGE", "VINOD KUMAR", "405557", "2009-08-22"),
                new DemoCodeData("965", "SB IND PU COLLEGE", "K TANUJA", "405601", "2009-08-23"),
                new DemoCodeData("966", "AVK COLLEGE", "SHIVARAJA", "405938", "2009-08-24"),
                new DemoCodeData("967", "SB VVS WOMEN PU COLLEGE", "VIJETHA", "406416", "2009-08-25"),
                new DemoCodeData("968", "SBA COMP PU COLLEGE", "H SHARATH KUMAR", "407735", "2009-08-26"),
                new DemoCodeData("969", "SBBK INDP PU COLLEGE", "GOVINDA", "416359", "2009-08-27"),
                new DemoCodeData("970", "SBD GIRLS PU COLLEGE", "NARASIMHALU", "416616", "2009-08-28"),
                new DemoCodeData("971", "SBG PU COLLEGE", "PRANESH", "416678", "2009-08-29"),
                new DemoCodeData("972", "SBGG WOMENS PU COLLEGE", "MADHUKUMAR K", "418799", "2009-08-30"),
                new DemoCodeData("973", "SBL WOMENS PU COLLEGE", "SUNILA TALAVARA", "418913", "2009-08-31"),
                new DemoCodeData("974", "SBM COMP PU COLLEGE", "PARASHURAMA DAS", "418996", "2009-09-01"),
                new DemoCodeData("975", "SBMJ INDP PU COLLEGE", "RADHIKA KANCHIK", "419087", "2009-09-02"),
                new DemoCodeData("976", "SBRR MAHAJANA PU COLLEGE", "HANUMAKKA DASAP", "419596", "2009-09-03"),
                new DemoCodeData("977", "SBSH PU COLLEGE", "BUDI BASAVA", "421886", "2009-09-04"),
                new DemoCodeData("978", "SC GOURSHETTY PU COLLEGE", "G SANJANA RANI", "422048", "2009-09-05"),
                new DemoCodeData("979", "SC PRASADIK PU COLLEGE", "RUKIYA", "422275", "2009-09-06"),
                new DemoCodeData("980", "AVK PU COLLEGE", "ARCHANA KULKARN", "422361", "2009-09-07"),
                new DemoCodeData("981", "SC SHIVAJI IND PU COLLEGE", "SHARANA BASAVA", "422775", "2009-09-08"),
                new DemoCodeData("982", "SCAI COMP PU COLLEGE", "PAVITRA DALAPPA", "423259", "2009-09-09"),
                new DemoCodeData("983", "SCBG COMMERCE PU COLLEGE", "SHASHIKALA BASA", "423805", "2009-09-10"),
                new DemoCodeData("984", "SCBR COMP PU COLLEGE", "BHARATI SURESH ", "424309", "2009-09-11"),
                new DemoCodeData("985", "SCG MEMORIAL PU COLLEGE", "SHASHIKALA PUND", "424421", "2009-09-12"),
                new DemoCodeData("986", "SCMM COMP PU COLLEGE", "VIJAYAKUMAR SHR", "424614", "2009-09-13"),
                new DemoCodeData("987", "SCMS PU COLLEGE", "RAMEJA HASANASA", "424857", "2009-09-14"),
                new DemoCodeData("988", "SDM DANIGONDA PU COLLEGE", "SACHIN HANAMANT", "425193", "2009-09-15"),
                new DemoCodeData("989", "SDM PU COLLEGE", "GUDIYAPLARA SUD", "425464", "2009-09-16"),
                new DemoCodeData("990", "SDP TEMPLE PU COLLEGE", "GUDIHALLI SUDEE", "425516", "2009-09-17"),
                new DemoCodeData("991", "SDSG PU COLLEGE", "MALGERA HANUMAN", "425532", "2009-09-18"),
                new DemoCodeData("992", "SDT COMP PU COLLEGE", "TALAVARA BASAMM", "425541", "2009-09-19"),
                new DemoCodeData("993", "SDVS SANGHA'S PU COLLEGE", "TALAVAGALU DARS", "425559", "2009-09-20"),
                new DemoCodeData("994", "SEA COMP PU COLLEGE", "GUDIHALLI ASHA", "425576", "2009-09-21"),
                new DemoCodeData("995", "SECAB PU COLLEGE", "GUDIHALLI RAKSH", "425611", "2009-09-22"),
                new DemoCodeData("996", "AYSHA PU COLLEGE", "GUDIHALLI PRASH", "425622", "2009-09-23"),
                new DemoCodeData("997", "SES PU COLLEGE", "HARAPANAHALLI A", "425633", "2009-09-24"),
                new DemoCodeData("998", "SESHADRIPURAM PU COLLEGE", "KADABAGERI HANU", "425655", "2009-09-25"),
                new DemoCodeData("999", "SEVABHARATHI PU COLLEGE", "VADERAHALLI MAN", "425663", "2009-09-26"),
                new DemoCodeData("1000", "SFS COMP PU COLLEGE", "GURAPPARA MANUJ", "426818", "2009-09-27"),
                new DemoCodeData("1001", "SG COMP PU COLLEGE", "NICHAVVANAHALLI", "426833", "2009-09-28")


                ));
    }

    // Method to set demo data to TextViews
    private void setDemoData() {
        if (!demoList.isEmpty()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(demoList.size());
            DemoCodeData data = demoList.get(randomIndex);

            tvSchoolName.setText(data.getSchoolName());
            tvStudentName.setText(data.getStudentName());
            tvRollNumber.setText(data.getRollNumber());
            tvDOB.setText(data.getDateOfBirth());

            setBtCreate(data.getSchoolName(), data.getStudentName(), data.getRollNumber(), data.getDateOfBirth());
        }
    }

    private void setDobEdit() {
        setupEditTextAutoMoveAndClear(dobEditBox1, null, dobEditBox2);
        setupEditTextAutoMoveAndClear(dobEditBox2, dobEditBox1, dobEditBox3);
        setupEditTextAutoMoveAndClear(dobEditBox3, dobEditBox2, dobEditBox4);
        setupEditTextAutoMoveAndClear(dobEditBox4, dobEditBox3, dobEditBox5);
        setupEditTextAutoMoveAndClear(dobEditBox5, dobEditBox4, dobEditBox6);
        setupEditTextAutoMoveAndClear(dobEditBox6, dobEditBox5, dobEditBox7);
        setupEditTextAutoMoveAndClear(dobEditBox7, dobEditBox6, dobEditBox8);
        setupEditTextAutoMoveAndClear(dobEditBox8, dobEditBox7, null);
    }

    private void setBtCreate(String schoolName, String studentName, String rollName, String dateOfBirth) {
        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("VALUE", "VALUE: " + schoolName + ", " + studentName + ", " + rollName + ", " + dateOfBirth);
                // Retrieve user inputs each time the button is clicked
                String inputSchoolName = edSchoolName.getText().toString().trim();
                String inputStudentName = edStudentName.getText().toString().trim();
                String inputRollNumber = edRollNumber.getText().toString().trim();

                // Construct and format the date of birth from individual input boxes
                String dob = dobEditBox1.getText().toString().trim()
                        + dobEditBox2.getText().toString().trim()
                        + dobEditBox3.getText().toString().trim()
                        + dobEditBox4.getText().toString().trim() + "-"
                        + dobEditBox5.getText().toString().trim()
                        + dobEditBox6.getText().toString().trim() + "-"
                        + dobEditBox7.getText().toString().trim()
                        + dobEditBox8.getText().toString().trim();

                // Format the DOB to match the expected format
                String formattedDob = formatDOB(dob);

                // Validate each field
                if (inputSchoolName.isEmpty()) {
                    Toast.makeText(activity, "Please enter the Institute name", Toast.LENGTH_SHORT).show();
                } else if (!schoolName.equalsIgnoreCase(inputSchoolName)) {
                    Toast.makeText(activity, "Institute Name does not match!", Toast.LENGTH_SHORT).show();
                } else if (inputStudentName.isEmpty()) {
                    Toast.makeText(activity, "Please enter the student name", Toast.LENGTH_SHORT).show();
                } else if (!studentName.equalsIgnoreCase(inputStudentName)) {
                    Toast.makeText(activity, "Student Name does not match!", Toast.LENGTH_SHORT).show();
                } else if (inputRollNumber.isEmpty()) {
                    Toast.makeText(activity, "Please enter the roll number", Toast.LENGTH_SHORT).show();
                } else if (!rollName.equals(inputRollNumber)) {
                    Toast.makeText(activity, "Roll Number does not match!", Toast.LENGTH_SHORT).show();
                } else if (dob.isEmpty()) {
                    Toast.makeText(activity, "Please enter the date of birth", Toast.LENGTH_SHORT).show();
                } else if (!dateOfBirth.equals(formattedDob)) {
                    Toast.makeText(activity, "Date of Birth does not match!", Toast.LENGTH_SHORT).show();
                } else {
                    String starWorkPlanId = session.getData(Constant.START_WORK);

                    if (starWorkPlanId.isEmpty()) {
                        Toast.makeText(requireActivity(), "Activate the job plan and start the work", Toast.LENGTH_SHORT).show();
                    } else {
                        setDemoData();

                        // Pass data to CreateIDFragment after a successful validation
                        CreateIDFragment createIDFragment = new CreateIDFragment();

                        // Create a Bundle to hold the data
                        Bundle bundle = new Bundle();
                        bundle.putString("schoolName", inputSchoolName);
                        bundle.putString("studentName", inputStudentName);
                        bundle.putString("rollNumber", inputRollNumber);
                        bundle.putString("dob", formattedDob);

                        // Set the arguments to the fragment
                        createIDFragment.setArguments(bundle);

                        // Replace the current fragment with the new one
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.Container, createIDFragment)
                                .commitAllowingStateLoss();

                        incrementCodeCount();
                    }
                }
            }
        });
    }

    // Helper method to format the DOB
    private String formatDOB(String dob) {
        if (dob.length() == 8) { // Assuming the input is like "DDMMYYYY"
            return dob.substring(0, 2) + "-" + dob.substring(2, 4) + "-" + dob.substring(4);
        }
        return dob; // Return as-is if format doesn't match expected
    }



    private boolean isalidVDate(String date) {
        return date.matches(Constant.DATE_OF_BIRTH_PATTERN);
    }

    private void setBtSyncNow() {
        btnSyncNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                codeCount = 0;
//                session.clearData(Constant.CODE_COUNT);
//                session.setData(Constant.CODE_COUNT, String.valueOf(codeCount));
                syncNowApi();
            }
        });
    }

    private void initialCodeCount() {

        String earningWallet = session.getData(Constant.EARNING_WALLET);
        String todayCodes = session.getData(Constant.TODAY_CODES);
        String totalCodes = session.getData(Constant.TOTAL_CODES);
        String workedDays = session.getData(Constant.WORKED_DAYS);
//        String planId = session.getData("selected_plan_id");
//        isPlanSelected = String.valueOf((planId != null && !planId.equals("0")));

//        if (balance.isEmpty()) {
//            llWaiting.setVisibility(View.VISIBLE);
//            frame.setVisibility(View.GONE);
//        } else {
//            llWaiting.setVisibility(View.GONE);
//            frame.setVisibility(View.VISIBLE);
//        }

        tvEarningWallet.setText("₹" + (earningWallet != null ? earningWallet : "0"));
        tvTodayCodes.setText(todayCodes != null ? todayCodes : "N/A");
        tvTotalCodes.setText(totalCodes != null ? totalCodes : "N/A");
        tvWorkingDays.setText(workedDays != null ? workedDays : "N/A");

        Log.d("initialCodeCount","earningWallet: " + earningWallet);
        Log.d("initialCodeCount","TODAY_CODES: " + todayCodes);
        Log.d("initialCodeCount","TOTAL_CODES: " + totalCodes);
        Log.d("initialCodeCount","WORKED_DAYS: " + workedDays);
        Log.d("selected_plan_id","selected_plan_id: " + isPlanSelected);

        codeCount = session.getData(Constant.CODE_COUNT).isEmpty() ? 0 : Integer.parseInt(session.getData(Constant.CODE_COUNT));
        tvCodes.setText(String.valueOf(codeCount));
        int progressPercentage = (codeCount * 100) / MAX_CODE_COUNT;
        cbCodes.setProgress(progressPercentage);

        session.setBoolean(Constant.IS_PLAN_CHANGE, codeCount == 0);
//        Log.d("START_WORK", "START_WORK IS_PLAN_CHANGE: " + session.getData(Constant.IS_PLAN_CHANGE));

        if (codeCount == MAX_CODE_COUNT) {
            btnSyncNow.setEnabled(true);
            btCreate.setEnabled(false);
        } else {
            btnSyncNow.setEnabled(false);
            btCreate.setEnabled(true);
        }
    }

    public void syncNowApi() {
        Map<String, String> params = new HashMap<>();
            params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
//        params.put(Constant.PLAN_ID, "1");
            params.put(Constant.PLAN_ID, session.getData(Constant.START_WORK));
            ApiConfig.RequestToVolley((result, response) -> {
                if (result) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("message");
                        if (jsonObject.getBoolean(SUCCESS)) {
//                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                            if (activity instanceof MainActivity) {
                                ((MainActivity) activity).userDetails();
                            }
                            codeCount = 0;
                            session.clearData(Constant.CODE_COUNT);
                            session.setData(Constant.CODE_COUNT, String.valueOf(codeCount));
                            llWaiting.setVisibility(View.VISIBLE);
                            frame.setVisibility(View.GONE);
                            new Handler().postDelayed(() -> {
                                llWaiting.setVisibility(View.GONE);
                                frame.setVisibility(View.VISIBLE);
                                initialCodeCount();
                            }, 2000);
                        } else {
                            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("SYNC_CODE", "SYNC_CODE Error: " + e.getMessage());
                        Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, requireActivity(), Constant.SYNC_CODE, params, true);

            Log.d("SYNC_CODE", "SYNC_CODE: " + Constant.SYNC_CODE);
            Log.d("SYNC_CODE", "SYNC_CODE params: " + params);
    }

    private void incrementCodeCount() {
        if (codeCount < MAX_CODE_COUNT) {
            codeCount++;
            tvCodes.setText(String.valueOf(codeCount));
            int progressPercentage = (codeCount * 100) / MAX_CODE_COUNT;
            cbCodes.setProgress(progressPercentage);

            session.setBoolean(Constant.IS_PLAN_CHANGE, codeCount == 0);

            session.clearData(Constant.CODE_COUNT);
            session.setData(Constant.CODE_COUNT, String.valueOf(codeCount));

            if (codeCount == MAX_CODE_COUNT) {
                btnSyncNow.setEnabled(true);
                btCreate.setEnabled(false);
            } else {
                btnSyncNow.setEnabled(false);
                btCreate.setEnabled(true);
            }
        }
    }

    private void setupEditTextAutoMoveAndClear(EditText currentEditText, final EditText previousEditText, final EditText nextEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1 && nextEditText != null) {
                    nextEditText.requestFocus();
                } else if (charSequence.length() == 0 && previousEditText != null) {
                    previousEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void fetchSessionDataAndInitialize() {
        new Handler().postDelayed(() -> {
            llWaiting.setVisibility(View.GONE);
            frame.setVisibility(View.VISIBLE);
            initialCodeCount();
            planName();
//            dropDown();
        }, 2000);
    }
}

