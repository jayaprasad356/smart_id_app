package com.app.fortuneapp.fragment;

import static com.app.fortuneapp.helper.Constant.getHistoryDays;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.helper.DatabaseHelper;
import com.app.fortuneapp.helper.Session;
import com.app.fortuneapp.java.GenericTextWatcher;
import com.app.fortuneapp.R;
import com.app.fortuneapp.model.GenerateCodes;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    TextView tvName,tvPincode,tvCity, tvId,tvTodayCodes,tvTotalCodes,tvHistorydays;
    EditText edName,edPincode,edCity;
    Button btnGenerate;


    EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four,otp_textbox_five,otp_textbox_six,otp_textbox_seven,otp_textbox_eight,otp_textbox_nine,otp_textbox_ten;
    DatabaseHelper databaseHelper;
    ArrayList<GenerateCodes> generateCodes = new ArrayList<GenerateCodes>();
    Session session;
    Activity activity;
    ScrollView frame;
    LinearLayout llWaiting;

    String Idnumber = "";

    Handler handler;
    long code_generate_time = 0;



    public HomeFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        activity = getActivity();
        session = new Session(activity);

        databaseHelper = new DatabaseHelper(getActivity());

        handler = new Handler();
        code_generate_time = Long.parseLong(session.getData(Constant.CODE_GENERATE_TIME)) * 1000;


        GotoActivity();





        tvName = root.findViewById(R.id.tvName);
        tvPincode = root.findViewById(R.id.tvPincode);
        tvCity = root.findViewById(R.id.tvCity);
        tvId = root.findViewById(R.id.tvId);
        edName = root.findViewById(R.id.edName);
        edPincode = root.findViewById(R.id.edPincode);
        edCity = root.findViewById(R.id.edCity);
        tvTodayCodes = root.findViewById(R.id.tvTodayCodes);
        tvTotalCodes = root.findViewById(R.id.tvTotalCodes);
        tvHistorydays = root.findViewById(R.id.tvHistorydays);
        btnGenerate = root.findViewById(R.id.btnGenerate);
        frame = root.findViewById(R.id.frame);
        llWaiting = root.findViewById(R.id.llWaiting);


        otp_textbox_one = root.findViewById(R.id.otp_edit_box1);
        otp_textbox_two = root.findViewById(R.id.otp_edit_box2);
        otp_textbox_three = root.findViewById(R.id.otp_edit_box3);
        otp_textbox_four = root.findViewById(R.id.otp_edit_box4);
        otp_textbox_five = root.findViewById(R.id.otp_edit_box5);
        otp_textbox_six = root.findViewById(R.id.otp_edit_box6);
        otp_textbox_seven = root.findViewById(R.id.otp_edit_box7);
        otp_textbox_eight = root.findViewById(R.id.otp_edit_box8);
        otp_textbox_nine = root.findViewById(R.id.otp_edit_box9);
        otp_textbox_ten = root.findViewById(R.id.otp_edit_box10);

        tvTodayCodes.setText((session.getInt(Constant.TODAY_CODES) + session.getInt(Constant.CODES))+ "");
        tvTotalCodes.setText((session.getInt(Constant.TOTAL_CODES) + session.getInt(Constant.CODES))+ "");
        tvHistorydays.setText(getHistoryDays(session.getData(Constant.JOINED_DATE)));
        EditText[] edit = {otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four,otp_textbox_five,otp_textbox_six,otp_textbox_seven,otp_textbox_eight,otp_textbox_nine,otp_textbox_ten};
        otp_textbox_one.addTextChangedListener(new GenericTextWatcher(otp_textbox_one, edit));
        otp_textbox_two.addTextChangedListener(new GenericTextWatcher(otp_textbox_two, edit));
        otp_textbox_three.addTextChangedListener(new GenericTextWatcher(otp_textbox_three, edit));
        otp_textbox_four.addTextChangedListener(new GenericTextWatcher(otp_textbox_four, edit));
        otp_textbox_five.addTextChangedListener(new GenericTextWatcher(otp_textbox_five, edit));
        otp_textbox_six.addTextChangedListener(new GenericTextWatcher(otp_textbox_six, edit));
        otp_textbox_seven.addTextChangedListener(new GenericTextWatcher(otp_textbox_seven, edit));
        otp_textbox_eight.addTextChangedListener(new GenericTextWatcher(otp_textbox_eight, edit));
        otp_textbox_nine.addTextChangedListener(new GenericTextWatcher(otp_textbox_nine, edit));
        otp_textbox_ten.addTextChangedListener(new GenericTextWatcher(otp_textbox_ten, edit));
        generateCodes = databaseHelper.getAllCodes();

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Idnumber = otp_textbox_one.getText().toString().trim() + otp_textbox_two.getText().toString().trim() +
                        otp_textbox_three.getText().toString().trim() + otp_textbox_four.getText().toString().trim() + otp_textbox_five.getText().toString().trim() +
                        otp_textbox_six.getText().toString().trim() + otp_textbox_seven.getText().toString().trim() + otp_textbox_eight.getText().toString().trim() +
                        otp_textbox_nine.getText().toString().trim() + otp_textbox_ten.getText().toString().trim();





                if (!tvName.getText().toString().trim().equals(edName.getText().toString().trim())){

                   // Toast.makeText(getActivity(), "Name not match", Toast.LENGTH_SHORT).show();
                    edName.setError("Name not match");
                    edName.requestFocus();
                    return;

                }
                else if (!tvId.getText().toString().trim().equals(Idnumber.toString().trim())){


                    // Toast.makeText(getActivity(), "Id number not match", Toast.LENGTH_SHORT).show();
                    otp_textbox_ten.setError("Id number not match");
                    otp_textbox_ten.requestFocus();
                    return;
                }
                else if (!tvCity.getText().toString().trim().equals(edCity.getText().toString().trim())){

                   // Toast.makeText(getActivity(), "City not match", Toast.LENGTH_SHORT).show();
                    edCity.setError("City not match");
                    edCity.requestFocus();
                    return;
                }
                else if (!tvPincode.getText().toString().trim().equals(edPincode.getText().toString().trim())){

                   // Toast.makeText(getActivity(), "Pin code not match", Toast.LENGTH_SHORT).show();
                    edPincode.setError("Pin code not match");
                    edPincode.requestFocus();
                    return;
                }



                else {
                    if (session.getData(Constant.CODE_GENERATE).equals("1")){
                        session.setInt(Constant.CODES,session.getInt(Constant.CODES) + 1);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.beginTransaction().replace(R.id.Container, new GenrateQRFragment()).commit();

                    }else {
                        Toast.makeText(activity, "You are Restricted for Generating Code", Toast.LENGTH_SHORT).show();
                    }


                }

            }
        });



        return root;
    }


    private void GotoActivity()
    {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {


                frame.setVisibility(View.VISIBLE);
                llWaiting.setVisibility(View.GONE);



            }
        },code_generate_time);
    }


    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onResume() {
        super.onResume();


        tvName.setText(generateCodes.get(0).getStudent_name());
        tvPincode.setText(generateCodes.get(0).getPin_code());
        tvCity.setText(generateCodes.get(0).getEcity());
        tvId.setText(generateCodes.get(0).getId_number());


    }



}