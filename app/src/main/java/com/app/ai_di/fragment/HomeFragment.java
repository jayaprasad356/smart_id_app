package com.app.ai_di.fragment;

import static com.app.ai_di.helper.Constant.SUCCESS;
import static com.zoho.livechat.android.provider.ZohoLDContentProvider.dbHelper;

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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.ai_di.Adapter.JobPlanAdapter;
import com.app.ai_di.R;
import com.app.ai_di.activities.MainActivity;
import com.app.ai_di.helper.ApiConfig;
import com.app.ai_di.helper.Constant;
import com.app.ai_di.helper.DatabaseHelper;
import com.app.ai_di.helper.SQLDatabaseHelper;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    Session session;
    Activity activity;

    MaterialButton btCreate, btnSyncNow, btnNotification;
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
    final int MAX_CODE_COUNT = 50;
    String isPlanSelected;
    SQLDatabaseHelper sqlDatabaseHelper;

    private int lastIndex = -1;

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
        btnNotification = root.findViewById(R.id.btnNotification);
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

        sqlDatabaseHelper = new SQLDatabaseHelper(activity);

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
//        setDemoData();
        setDobEdit();
//        setBtCreate();
        setBtSyncNow();

        // Set OnClickListener for btnNotification
        btnNotification.setOnClickListener(v -> {
            // Navigate to NotificationFragment
            NotificationFragment notificationFragment = new NotificationFragment();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Optional: Add a transition animation
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

            // Replace the current fragment with NotificationFragment
            transaction.replace(R.id.Container, notificationFragment);
            transaction.addToBackStack(null); // Optional: Add to backstack to allow going back
            transaction.commit();
        });

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

    public void initializeDemoList() {
        Map<String, String> params = new HashMap<>();

        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");

                    if (jsonObject.getBoolean("success")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        // Ensure the array has at least one item
                        if (jsonArray.length() > 0) {
                            JSONObject firstItem = jsonArray.getJSONObject(0);

                            // Extract the fields from the first object
                            String college = firstItem.getString("college");
                            String name = firstItem.getString("name");
                            String batchNumber = firstItem.getString("batch_number");
                            String date = firstItem.getString("date");

                            // Set data to TextViews
                            tvSchoolName.setText(college);
                            tvStudentName.setText(name);
                            tvRollNumber.setText(batchNumber);
                            tvDOB.setText(date);

                            // Set up additional logic, if required
                            setBtCreate(college, name, batchNumber, date);
                        } else {
                            Toast.makeText(requireActivity(), "No data available", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("DATA_LIST", "DATA_LIST Error: " + e.getMessage());
                    Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, requireActivity(), Constant.DATA_LIST, params, true);

        Log.d("DATA_LIST", "DATA_LIST: " + Constant.DATA_LIST);
        Log.d("DATA_LIST", "DATA_LIST params: " + params);
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

//    private void initializeDemoList() {
//        demoList = new ArrayList<>(Arrays.asList(
//                new DemoCodeData("1", "AAKASH PU COLLEGE", "KAJAL CHAVAN", "2034", "2009-01-01"),
//                new DemoCodeData("2", "AARALGOUDAR PU COLLEGE", "AKSHATA VIJAY B", "2088", "2009-01-02"),
//                new DemoCodeData("3", "AB JATTI COMP PU COLLEGE", "KAVERI PAWAR", "2151", "2009-01-03"),
//                ));
//    }

    // Method to set demo data to TextViews
//    private void setDemoData() {
//        if (!demoList.isEmpty()) {
//            int randomIndex = ThreadLocalRandom.current().nextInt(demoList.size());
//            DemoCodeData data = demoList.get(randomIndex);
//
//            tvSchoolName.setText(data.getSchoolName());
//            tvStudentName.setText(data.getStudentName());
//            tvRollNumber.setText(data.getRollNumber());
//            tvDOB.setText(data.getDateOfBirth());
//
//            setBtCreate(data.getSchoolName(), data.getStudentName(), data.getRollNumber(), data.getDateOfBirth());
//        }
//    }

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
                } else if (!schoolName.trim().equals(inputSchoolName.trim())) {
                    Toast.makeText(activity, "Institute Name does not match!", Toast.LENGTH_SHORT).show();
                } else if (inputStudentName.isEmpty()) {
                    Toast.makeText(activity, "Please enter the student name", Toast.LENGTH_SHORT).show();
                } else if (!studentName.trim().equals(inputStudentName.trim())) {
                    Toast.makeText(activity, "Student Name does not match!", Toast.LENGTH_SHORT).show();
                } else if (inputRollNumber.isEmpty()) {
                    Toast.makeText(activity, "Please enter the roll number", Toast.LENGTH_SHORT).show();
                } else if (!rollName.trim().equals(inputRollNumber.trim())) {
                    Toast.makeText(activity, "Roll Number does not match!", Toast.LENGTH_SHORT).show();
                } else if (dob.isEmpty()) {
                    Toast.makeText(activity, "Please enter the date of birth", Toast.LENGTH_SHORT).show();
                } else if (!dateOfBirth.trim().equals(formattedDob.trim())) {
                    Toast.makeText(activity, "Date of Birth does not match!", Toast.LENGTH_SHORT).show();
                } else {
                    String starWorkPlanId = session.getData(Constant.START_WORK);

                    if (starWorkPlanId.isEmpty()) {
                        Toast.makeText(requireActivity(), "Activate the job plan and start the work", Toast.LENGTH_SHORT).show();
                    } else {
//                        setDemoData();
                        initializeDemoList();

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

        Log.d("initialCodeCount","session.getData(Constant.START_WORK): " + session.getData(Constant.START_WORK));

        if (!session.getData(Constant.START_WORK).isEmpty() && session.getData(Constant.START_WORK) != null) {
            myPlanListApi();
        }

        String earningWallet = session.getData(Constant.EARNING_WALLET);
        String todayCodes = session.getData(Constant.TODAY_CODES);
        String totalCodes = session.getData(Constant.TOTAL_CODES);
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

        Log.d("initialCodeCount","earningWallet: " + earningWallet);
        Log.d("initialCodeCount","TODAY_CODES: " + todayCodes);
        Log.d("initialCodeCount","TOTAL_CODES: " + totalCodes);
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
    public void myPlanListApi() {
        if (!isAdded()) return;  // Check if the fragment is attached before proceeding

        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        params.put(Constant.PLAN_ID, session.getData(Constant.START_WORK));

        ApiConfig.RequestToVolley((result, response) -> {
            if (result && isAdded()) {  // Ensure the fragment is still attached here
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    if (jsonObject.getBoolean(SUCCESS)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        if (jsonArray.length() > 0) {
                            JSONObject planData = jsonArray.getJSONObject(0);
                            int workedDays = planData.optInt("worked_days", 0);

                            // Ensure TextView update only if fragment is attached
                            if (isAdded()) {
                                tvWorkingDays.setText(String.valueOf(workedDays));
                            }
                        } else {
                            if (isAdded()) {
                                tvWorkingDays.setText("N/A");
                            }
                        }
                    } else {
                        if (isAdded()) {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("MY_PLAN_LIST", "MY_PLAN_LIST Error: " + e.getMessage());
                    if (isAdded()) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, getActivity(), Constant.MY_PLAN_LIST, params, true);
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

