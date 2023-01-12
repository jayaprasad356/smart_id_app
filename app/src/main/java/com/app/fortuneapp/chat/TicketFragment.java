package com.app.fortuneapp.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.app.fortuneapp.R;
import com.app.fortuneapp.activities.RiseTicketActivity;
import com.app.fortuneapp.chat.adapters.TicketAdapters;
import com.app.fortuneapp.chat.managers.Utils;
import com.app.fortuneapp.chat.models.Ticket;
import com.app.fortuneapp.helper.Constant;
import com.app.fortuneapp.helper.Session;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TicketFragment extends Fragment {
    public RecyclerView mRecyclerView;
    public ArrayList<Ticket> mTickets,AllTickets;
    public TicketAdapters ticketAdapters;
    Activity activity;
    Session session;
    String type = "";
    private Button riseTicketBtn;


    public TicketFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_ticket, container, false);
        activity = getActivity();
        session = new Session(activity);
        mRecyclerView = root.findViewById(R.id.recyclerView);
        riseTicketBtn = root.findViewById(R.id.btnRiseTicket);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        mRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        readTickets();

        riseTicketBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), RiseTicketActivity.class));
            }
        });
        Utils.uploadToken(session.getData(Constant.FCM_ID),session.getData(Constant.USER_ID));


        return root;
    }

    private void readTickets() {

        mTickets = new ArrayList<>();
        Query reference;
        reference = Utils.getQueryPendingTicketByMyId(session.getData(Constant.MOBILE));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Ticket user = snapshot.getValue(Ticket.class);
                        assert user != null;
                        mTickets.add(user);
                    }

                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference = Utils.getQueryOpenedTicketByMyId(session.getData(Constant.MOBILE));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Ticket user = snapshot.getValue(Ticket.class);
                        assert user != null;
                        mTickets.add(user);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference = Utils.getQueryClosedTicketByMyId(session.getData(Constant.MOBILE));
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Ticket user = snapshot.getValue(Ticket.class);
                        assert user != null;
                        mTickets.add(user);
                    }

                }
                setAdatper();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
    private void setAdatper() {
        ticketAdapters = new TicketAdapters(activity, mTickets);
        mRecyclerView.setAdapter(ticketAdapters);
    }
}