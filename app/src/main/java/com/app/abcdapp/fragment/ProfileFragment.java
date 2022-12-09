package com.app.abcdapp.fragment;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.app.abcdapp.BuildConfig;
import com.app.abcdapp.R;
import com.app.abcdapp.activities.NotificaionActivity;
import com.app.abcdapp.activities.ReferEarnActivity;
import com.app.abcdapp.activities.UpdateProfileActivity;
import com.app.abcdapp.helper.Constant;
import com.app.abcdapp.helper.Session;
import com.app.abcdapp.model.Notification;


public class ProfileFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    CardView cardView1;
    ImageView imgMenu;
    Session session;
    Activity activity;
    Button btncopy;

    private ClipboardManager myClipboard;
    private ClipData myClip;

    TextView tvName,tvMobile,tvEarn,tvWithdrawal,tvCodes,tvBalance,tvRefercode,tvTotalRefer;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root  =  inflater.inflate(R.layout.fragment_profile, container, false);


        activity = getActivity();
        session = new Session(getActivity());

        cardView1 = root.findViewById(R.id.cardView1);
        imgMenu = root.findViewById(R.id.imgMenu);
        tvName = root.findViewById(R.id.tvName);
        tvMobile = root.findViewById(R.id.tvMobile);
        tvEarn = root.findViewById(R.id.tvEarn);
        tvWithdrawal = root.findViewById(R.id.tvWithdrawal);
        tvCodes = root.findViewById(R.id.tvCodes);
        tvBalance = root.findViewById(R.id.tvBalance);
        btncopy = root.findViewById(R.id.btncopy);
        tvRefercode = root.findViewById(R.id.tvRefercode);
        tvTotalRefer = root.findViewById(R.id.tvTotalRefer);

        tvName.setText(session.getData(Constant.NAME));
        tvMobile.setText(session.getData(Constant.MOBILE));
        tvEarn.setText(session.getData(Constant.EARN)) ;
        tvWithdrawal.setText(session.getData(Constant.WITHDRAWAL));
        tvCodes.setText(session.getInt(Constant.TOTAL_CODES) + "");
        tvBalance.setText(session.getData(Constant.BALANCE));
        tvTotalRefer.setText(session.getData(Constant.TOTAL_REFERRALS));
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ReferEarnActivity.class);
                startActivity(intent);
            }
        });


        tvRefercode.setText(" Your Refer Code : "+session.getData(Constant.REFER_CODE));
        String text = tvRefercode.getText().toString();
        btncopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String shareMessage= "\nDOWNLOAD THE APP AND GET UNLIMITED EARNING .you can also Download App from below link and enter referral code while login-"+"\n"+text+"\n";
                    shareMessage = shareMessage +"\n\n";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }

            }
        });



        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showpopup(view);
            }
        });




        return root;
    }

    private void showpopup(View v) {

        PopupMenu popup = new PopupMenu(getActivity(),v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menu);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.logoutitem){
            session.logoutUser(activity);
        }

        else if (item.getItemId() == R.id.notification){

            Intent intent = new Intent(activity, NotificaionActivity.class);
            startActivity(intent);

        }

        else if (item.getItemId() == R.id.ReferEarn){

            Intent intent = new Intent(activity,ReferEarnActivity.class);
            startActivity(intent);

        }
        else if (item.getItemId() == R.id.Uptdatepofile){

            Intent intent = new Intent(activity, UpdateProfileActivity.class);
            startActivity(intent);

        }
        return false;
    }
}