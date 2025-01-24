package com.gmwapp.slv_aidi.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.gmwapp.slv_aidi.R;
import com.gmwapp.slv_aidi.activities.MainActivity;
import com.gmwapp.slv_aidi.helper.Session;

public class TradingFragment extends Fragment {
    private Session session;
    private Activity activity;
    private ImageButton ibBack;

    FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trading, container, false);

        activity = getActivity();
        session = new Session(activity);

        ibBack = view.findViewById(R.id.ibBack);

        fragmentManager = getParentFragmentManager();

        // Hide BottomNavigationView
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).hideBottomNavigation();
        }

        // Back button click listener
        ibBack.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Show BottomNavigationView again when fragment is destroyed
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).showBottomNavigation();
        }
    }
}