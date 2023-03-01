package com.app.fortuneapp.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import com.app.fortuneapp.R;
import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.helper.Session;

public class InFoFragment extends Fragment {
    Button btnRegularTask, btnChampionTask, btnDownloadDetails, btnDemoVideoChamp, btnDemoVideoRegular;
    Session session;
    //  TextView tvDashboard;
    WebView webView;

    public InFoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_in_fo, container, false);
        session = new Session(getActivity());
        btnRegularTask = root.findViewById(R.id.btn_trail_regular);
        btnChampionTask = root.findViewById(R.id.btn_trail_champion);
        btnDownloadDetails = root.findViewById(R.id.btnDownloadDetails);
        btnDemoVideoChamp = root.findViewById(R.id.btn_demo_video_champ);
        btnDemoVideoRegular = root.findViewById(R.id.btn_demo_video_regular);
        // tvDashboard=root.findViewById(R.id.tvDashboard);
        webView=root.findViewById(R.id.webview);
        webView.setVerticalScrollBarEnabled(true);
        webView.loadDataWithBaseURL("", session.getData(Constant.MAIN_CONTENT), "text/html", "UTF-8", "");
        btnRegularTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.Container, new HomeFragment()).commit();
            }
        });
        btnChampionTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.Container, new FindMissingFragment()).commit();
            }
        });
        btnDownloadDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("" + session.getData(Constant.JOB_DETAILS_LINK)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
//        tvDashboard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String url = "https://dashboard.abcdapp.in/";
//                Intent i = new Intent(Intent.ACTION_VIEW);
//                i.setData(Uri.parse(url));
//                startActivity(i);
//            }
//        });
        btnDemoVideoChamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("" + session.getData(Constant.CHAMPION_DEMO_LINK)); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        btnDemoVideoRegular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://youtu.be/SIQOM_ALnDI"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        return root;
    }
}