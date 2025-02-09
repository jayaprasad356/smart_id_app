package com.gmwapp.slv_aidi.fragment;

import static android.app.Activity.RESULT_OK;
import static com.gmwapp.slv_aidi.helper.Constant.SUCCESS;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gmwapp.slv_aidi.Adapter.JobPlanAdapter;
import com.gmwapp.slv_aidi.Adapter.PlanListAdapter;
import com.gmwapp.slv_aidi.R;
import com.gmwapp.slv_aidi.activities.MainActivity;
import com.gmwapp.slv_aidi.helper.ApiConfig;
import com.gmwapp.slv_aidi.helper.Constant;
import com.gmwapp.slv_aidi.helper.Session;
import com.gmwapp.slv_aidi.model.DemoCodeData;
import com.gmwapp.slv_aidi.model.PlanListModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.gson.Gson;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    EditText edPasskey;
    EditText dobEditBox1, dobEditBox2, dobEditBox3, dobEditBox4, dobEditBox5, dobEditBox6, dobEditBox7, dobEditBox8;
    CircularProgressIndicator cbCodes;
    TextView tvCodes, tvEarningWallet, tvTodayCodes, tvTotalCodes, tvWorkingDays, tvPlanName;
    TextView tvSchoolName, tvStudentName, tvRollNumber, tvDOB, tvPasskey;
    LinearLayout llWaiting;
    MaterialCardView rlSelectPlan;
    NestedScrollView frame;
    Spinner dropdownSpinner;
    YouTubePlayer mPlayer;

    int codeCount = 0;
    final int MAX_CODE_COUNT = 50;
    String isPlanSelected;
    private int lastIndex = -1;
    List<DemoCodeData> demoList;
    private int currentIndex = 0;
    private PlanListAdapter planListAdapter;
    private List<PlanListModel> planListModel;
//    private RecyclerView rvPlanList;


    View root;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
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
        edPasskey = root.findViewById(R.id.edPasskey);
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
        tvPasskey = root.findViewById(R.id.tvPasskey);
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

        planListModel = new ArrayList<>();

        llWaiting.setVisibility(View.VISIBLE);
        frame.setVisibility(View.GONE);

        fetchSessionDataAndInitialize();
//        initialCodeCount();
//        initializeDemoList();
//        setDemoData();
        setDobEdit();
//        setBtCreate();
        setBtSyncNow();

//        loadPlans();

//        showAdVideo("https://www.youtube.com/shorts/6RC_0H4875w");

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

        // Retrieve the saved data list
        List<DemoCodeData> savedDataList = session.getDemoDataList();

        // Retrieve the currentIndex from SharedPreferences
        SharedPreferences prefs = activity.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentIndex = prefs.getInt("currentIndex", 0);

        // Check if data is available
        if (savedDataList != null && !savedDataList.isEmpty()) {
            // Ensure currentIndex is within bounds
                if (currentIndex >= savedDataList.size()) {
                    session.clearData("extra_plan_activated");
                    Log.d("DATA_CLEARED", "Stored data has been cleared.");
                    initializeDemoList();
                    currentIndex = 0; // Loop back to the first item
                }

            // Display the current item
            updateUIWithData(savedDataList.get(currentIndex));

            // Increment and save the updated index
            currentIndex++;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("currentIndex", currentIndex);
            editor.apply();
        } else {
            Log.d("DATA_ERROR", "savedDataList is null or empty.");
            initializeDemoList();

            tvSchoolName.setText("SSJ INDP PU COLLEGE");
            tvStudentName.setText("AISHWARYA M S");
            tvRollNumber.setText("7459508");
            tvDOB.setText("2009-08-25");
            tvPasskey.setText("A2fdK95@kP#z*1j!%8IL");

            setBtCreate("SSJ INDP PU COLLEGE", "AISHWARYA M S", "7459508", "A2fdK95@kP#z*1j!%8IL", "2009-08-25");

//            new Handler().postDelayed(() -> {
//                List<DemoCodeData> retryData = session.getDemoDataList();
//                updateUIWithData(retryData.get(5));
//            }, 2000);
        }

        return root;
    }

    private void updateUIWithData(DemoCodeData data) {
        Integer getId = data.getId();
        String getCollege = data.getCollege();
        String getName = data.getName();
        String getBatchNumber = data.getBatchNumber();
        String getPass_key = data.getPass_key();
        String getDate = data.getDate();

        Log.d("SAVED_DATA",
                "ID: " + getId +
                        ", College: " + getCollege +
                        ", Name: " + getName +
                        ", Batch: " + getBatchNumber +
                        ", Passkey: " + getPass_key +
                        ", Date: " + getDate);

        if (getPass_key == null || getPass_key.isEmpty()) {
            Log.d("DATA_ERROR", "savedDataList is null or empty.");
            initializeDemoList();

            tvSchoolName.setText("SSJ INDP PU COLLEGE");
            tvStudentName.setText("AISHWARYA M S");
            tvRollNumber.setText("7459508");
            tvDOB.setText("2009-08-25");
            tvPasskey.setText("A2fdK95@kP#z*1j!%8IL");

            setBtCreate("SSJ INDP PU COLLEGE", "AISHWARYA M S", "7459508", "A2fdK95@kP#z*1j!%8IL", "2009-08-25");

//            new Handler().postDelayed(() -> {
//                List<DemoCodeData> retryData = session.getDemoDataList();
//                updateUIWithData(retryData.get(5));
//            }, 2000);
        } else {

            tvSchoolName.setText(getCollege);
            tvStudentName.setText(getName);
            tvRollNumber.setText(getBatchNumber);
            tvDOB.setText(getDate);
            tvPasskey.setText(getPass_key);

            // Set up additional logic, if required
            setBtCreate(getCollege, getName, getBatchNumber, getPass_key, getDate);
        }
    }


    private void planName() {
        String startWorkPlanName = session.getData(Constant.START_WORK_PLAN_NAME);
        String workedDays = session.getData(Constant.START_WORK_PLAN_WORK_DAY);
        Log.d("START_WORK", "START_WORK startWorkPlanName: " + startWorkPlanName);

        if (startWorkPlanName.isEmpty()) {
            rlSelectPlan.setVisibility(View.GONE);
        } else {
            rlSelectPlan.setVisibility(View.VISIBLE);
            tvPlanName.setText(startWorkPlanName);
            tvWorkingDays.setText(String.valueOf(workedDays));
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
//                        if (jsonArray.length() > 0) {
//                            JSONObject firstItem = jsonArray.getJSONObject(0);
//
//                            // Extract the fields from the first object
//                            String college = firstItem.getString("college");
//                            String name = firstItem.getString("name");
//                            String batchNumber = firstItem.getString("batch_number");
//                            String date = firstItem.getString("date");
//
//                            // Set data to TextViews
//                            tvSchoolName.setText(college);
//                            tvStudentName.setText(name);
//                            tvRollNumber.setText(batchNumber);
//                            tvDOB.setText(date);
//
//                            // Set up additional logic, if required
//                            setBtCreate(college, name, batchNumber, date);
//                        } else {
//                            Toast.makeText(requireActivity(), "No data available", Toast.LENGTH_SHORT).show();
//                        }

                        // Parse JSON array into a list of DemoCodeData
                        List<DemoCodeData> dataList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject item = jsonArray.getJSONObject(i);
                            DemoCodeData demoCodeData = new DemoCodeData();
                            demoCodeData.setId(item.getInt("id"));
                            demoCodeData.setCollege(item.getString("college"));
                            demoCodeData.setName(item.getString("name"));
                            demoCodeData.setBatchNumber(item.getString("batch_number"));
                            demoCodeData.setPass_key(item.getString("pass_key"));
                            demoCodeData.setDate(item.getString("date"));
                            dataList.add(demoCodeData);
                        }

                        // Save the list in session
                        session.setDemoDataList(dataList);

//                        // Set the first item's data to TextViews as an example
//                        if (!dataList.isEmpty()) {
//                            DemoCodeData firstItem = dataList.get(0);
//                            tvSchoolName.setText(firstItem.getCollege());
//                            tvStudentName.setText(firstItem.getName());
//                            tvRollNumber.setText(firstItem.getBatchNumber());
//                            tvDOB.setText(firstItem.getDate());
//                        }
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

//    private void noDemoList() {
//        demoList = new ArrayList<>(Arrays.asList(
//                new DemoCodeData(1, "SSJ INDP PU COLLEGE", "AISHWARYA M S", "7459508", "2009-08-25"),
//                new DemoCodeData(2, "AKSHAMALA PU COLLEGE", "SHWETHA H M", "9504325", "2009-08-21"),
//                new DemoCodeData(3, "SRI GS PU COLLEGE", "YASHASWINI H K", "31959846", "2009-06-27"),
//                ...
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

    private void setBtCreate(String schoolName, String studentName, String rollName, String passkey, String dateOfBirth) {
        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("VALUE", "VALUE: " + schoolName + ", " + studentName + ", " + rollName + ", " + dateOfBirth + ", " + passkey);
                // Retrieve user inputs each time the button is clicked
                String inputSchoolName = edSchoolName.getText().toString().trim();
                String inputStudentName = edStudentName.getText().toString().trim();
                String inputRollNumber = edRollNumber.getText().toString().trim();
                String inputPasskey = edPasskey.getText().toString().trim();

                // Construct and format the date of birth from individual input boxes
                String dob = dobEditBox1.getText().toString().trim()
                        + dobEditBox2.getText().toString().trim()
                        + dobEditBox3.getText().toString().trim()
                        + dobEditBox4.getText().toString().trim() + "-"
                        + dobEditBox5.getText().toString().trim()
                        + dobEditBox6.getText().toString().trim() + "-"
                        + dobEditBox7.getText().toString().trim()
                        + dobEditBox8.getText().toString().trim();

                Log.d("VALUE", "VALUE: " + dob);

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
                } else if (dob.isEmpty() || dob.equals("--")) {
                    Toast.makeText(activity, "Please enter the date of birth", Toast.LENGTH_SHORT).show();
                } else if (!dateOfBirth.trim().equals(formattedDob.trim())) {
                    Toast.makeText(activity, "Date of Birth does not match!", Toast.LENGTH_SHORT).show();
                } else if (inputPasskey.isEmpty()) {
                    Toast.makeText(activity, "Please enter the passkey", Toast.LENGTH_SHORT).show();
                } else if (!passkey.trim().equals(inputPasskey.trim())) {
                    Toast.makeText(activity, "Passkey does not match!", Toast.LENGTH_SHORT).show();
                } else {
                    String starWorkPlanId = session.getData(Constant.START_WORK);

                    if (starWorkPlanId.isEmpty()) {
                        Toast.makeText(requireActivity(), "Activate the job plan and start the work", Toast.LENGTH_SHORT).show();
                    } else {
//                        setDemoData();
//                        initializeDemoList();

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
                // Show the progress bar
                ProgressBar progressBar = root.findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);

                // Start a 3-second timer
                new Handler().postDelayed(() -> {
                    // Hide the progress bar
                    progressBar.setVisibility(View.GONE);
//                    codeCount = 0;
//                    session.clearData(Constant.CODE_COUNT);
//                    session.setData(Constant.CODE_COUNT,

                    syncNowApi();
                }, 3000); // 3-second delay
            }
        });
    }

    private void initialCodeCount() {

        Log.d("initialCodeCount", "session.getData(Constant.START_WORK): " + session.getData(Constant.START_WORK));

//        if (!session.getData(Constant.START_WORK).isEmpty() && session.getData(Constant.START_WORK) != null) {
//            myPlanListApi();
//        }

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

        Log.d("initialCodeCount", "earningWallet: " + earningWallet);
        Log.d("initialCodeCount", "TODAY_CODES: " + todayCodes);
        Log.d("initialCodeCount", "TOTAL_CODES: " + totalCodes);
        Log.d("selected_plan_id", "selected_plan_id: " + isPlanSelected);

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

        String free_status = session.getData(Constant.FREE_PLAN_STATUS);
        String paid_status = session.getData(Constant.PAID_PLAN_STATUS);

        if(Objects.equals(free_status, "1") && Objects.equals(paid_status, "0")) {
            showPlanListDialog();
        }

        Log.d("SYNC_CODE", "SYNC_CODE: " + Constant.SYNC_CODE);
        Log.d("SYNC_CODE", "SYNC_CODE params: " + params);
    }

//    public void myPlanListApi() {
//        if (!isAdded()) return;  // Check if the fragment is attached before proceeding
//
//        Map<String, String> params = new HashMap<>();
//        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
//        params.put(Constant.PLAN_ID, session.getData(Constant.START_WORK));
//
//        ApiConfig.RequestToVolley((result, response) -> {
//            if (result && isAdded()) {  // Ensure the fragment is still attached here
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    String message = jsonObject.getString("message");
//                    if (jsonObject.getBoolean(SUCCESS)) {
//                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
//                        if (jsonArray.length() > 0) {
//                            JSONObject planData = jsonArray.getJSONObject(0);
//                            int workedDays = planData.optInt("worked_days", 0);
//
//                            // Ensure TextView update only if fragment is attached
//                            if (isAdded()) {
//                                tvWorkingDays.setText(String.valueOf(workedDays));
//                            }
//                        } else {
//                            if (isAdded()) {
//                                tvWorkingDays.setText("N/A");
//                            }
//                        }
//                    } else {
//                        if (isAdded()) {
//                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.d("MY_PLAN_LIST", "MY_PLAN_LIST Error: " + e.getMessage());
//                    if (isAdded()) {
//                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        }, getActivity(), Constant.MY_PLAN_LIST, params, true);
//        Log.d("MY_PLAN_LIST", "MY_PLAN_LIST: " + Constant.MY_PLAN_LIST);
//        Log.d("MY_PLAN_LIST", "MY_PLAN_LIST params: " + params);
//    }

    private void incrementCodeCount() {
        if (codeCount < MAX_CODE_COUNT) {
            codeCount++ ;
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1 && nextEditText != null) {
                    nextEditText.requestFocus();
                } else if (charSequence.length() == 0 && previousEditText != null) {
                    previousEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
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

    @SuppressLint("NotifyDataSetChanged")
    public void loadPlans() {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.USER_ID, session.getData(Constant.USER_ID));
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(SUCCESS)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        Gson g = new Gson();

                        // Clear the plan list before adding new data
                        planListModel.clear();

                        // Parse response and add to plan list
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            PlanListModel group = g.fromJson(jsonObject1.toString(), PlanListModel.class);
                            planListModel.add(group);
                        }

                        // Set adapter after data is loaded
//                        planListAdapter = new PlanListAdapter(activity, planListModel, this);
//                        rvPlanList.setAdapter(planListAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, activity, Constant.PLAN_LIST_URL, params, true);

        Log.d("PLAN_LIST_URL", "PLAN_LIST_URL: " + Constant.PLAN_LIST_URL);
        Log.d("PLAN_LIST_URL", "PLAN_LIST_URL params: " + params);
    }

    private void showPlanListDialog() {

        // Inflate custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_active_plan_custom, null);

        // Initialize RecyclerView in the dialog layout
        RecyclerView rvPlanList = dialogView.findViewById(R.id.rvPlanList);
        rvPlanList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the adapter with your data
        PlanListAdapter planListAdapter = new PlanListAdapter(activity, planListModel, this);
        rvPlanList.setAdapter(planListAdapter);

        // Create and show the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        // Close button action
        ImageButton btClose = dialogView.findViewById(R.id.btClose);
        btClose.setOnClickListener(v -> dialog.dismiss());

        // Show dialog
        dialog.show();
    }

    private String extractVideoId(String url) {
        String videoId = null;
        try {
            Uri uri = Uri.parse(url);

            // Check if the URL is a regular YouTube video URL
            if (uri.getQueryParameter("v") != null) {
                videoId = uri.getQueryParameter("v");
            }
            // Check if the URL is a YouTube Shorts URL
            else if (url.contains("/shorts/")) {
                // Extract the video ID from the URL path (after "/shorts/")
                String[] urlParts = url.split("/shorts/");
                if (urlParts.length > 1) {
                    videoId = urlParts[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoId;
    }

    public static void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int idx = 0; idx < group.getChildCount(); idx++) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showAdVideo(String youtubeUrl) {
        // Extract the video ID from the URL
        String videoId = extractVideoId(youtubeUrl);
        if (videoId == null || videoId.isEmpty()) {
            Toast.makeText(getContext(), "Invalid YouTube URL", Toast.LENGTH_SHORT).show();
            return;
        }

        // Inflate custom dialog layout
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_ad, null);

        // Initialize YouTubePlayerView
        YouTubePlayerView ytPlayerView = dialogView.findViewById(R.id.ytPlayerView);
//        MaterialCardView cvDummy = dialogView.findViewById(R.id.cv_dummy);
        getLifecycle().addObserver(ytPlayerView);
        enableDisableView(ytPlayerView, true);

        ytPlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(videoId, 0);
            }
        });

        ytPlayerView.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // Code for when the user touches the screen
                    Log.d("YouTubePlayerView", "Touch event: ACTION_DOWN");
                    break;

                case MotionEvent.ACTION_MOVE:
                    // Code for when the user moves their finger on the screen
                    Log.d("YouTubePlayerView", "Touch event: ACTION_MOVE");
                    break;

                case MotionEvent.ACTION_UP:
                    // Code for when the user lifts their finger off the screen
                    Log.d("YouTubePlayerView", "Touch event: ACTION_UP");
                    break;

                default:
                    break;
            }
            return true; // Return true to indicate the touch event is handled
        });

        // Create a FrameLayout to wrap YouTubePlayerView and intercept touch events
//        FrameLayout touchInterceptor = dialogView.findViewById(R.id.touchInterceptor);
//        cvDummy.setEnabled(false); // Consume the touch event to block interaction with the video

        // Create and show the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false) // Prevent the dialog from being dismissed by tapping outside
                .create();

        // Initialize the close button and countdown TextView
        ImageButton btClose = dialogView.findViewById(R.id.btClose);
        TextView tvCountdown = dialogView.findViewById(R.id.tvCountdown);

        btClose.setEnabled(false); // Disable the button initially
        btClose.setAlpha(0.5f);    // Make it less visible when disabled

        // Start a 40-second countdown
        new CountDownTimer(40000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Update the countdown timer text
                tvCountdown.setText((millisUntilFinished / 1000) + "");
            }

            public void onFinish() {
                // Enable the button and update UI
                btClose.setEnabled(true);
                btClose.setAlpha(1.0f); // Make it fully visible
                tvCountdown.setText("Skip");
            }
        }.start();

        btClose.setOnClickListener(v -> {
            dialog.dismiss();
            ytPlayerView.release();
        });

        // Show dialog
        dialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            mPlayer.play();
        }
    }
}

