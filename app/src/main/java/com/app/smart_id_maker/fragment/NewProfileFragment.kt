package com.app.smart_id_maker.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.app.smart_id_maker.ProfileFragment.InviteFragment
import com.app.smart_id_maker.R
import com.app.smart_id_maker.activities.InviteActivity
import com.app.smart_id_maker.activities.MyProfileActivity
import com.app.smart_id_maker.activities.MybanckActivity
import com.app.smart_id_maker.activities.SetPasswordActivity
import com.app.smart_id_maker.activities.TransactionActivity
import com.app.smart_id_maker.activities.UpdateBankActivity
import com.app.smart_id_maker.activities.WithdrawalActivity
import com.app.smart_id_maker.activities.WithdrawalStatusActivity
import com.app.smart_id_maker.helper.Constant
import com.app.smart_id_maker.helper.Session

class NewProfileFragment : Fragment() {
    var session: Session? = null
    var activity: Activity? = null


    var tvName: TextView? = null
    var tvMobile: TextView? = null

    var rlwithdrawhistory: RelativeLayout? = null
    var rlhistory: RelativeLayout? = null
    var rlUpdateprofile: RelativeLayout? = null
    var rlChangepassword: RelativeLayout? = null
    var rlmyBank: RelativeLayout? = null
    var rlInvite: RelativeLayout? = null
    var rlLogout: RelativeLayout? = null
    var rlWithdraw: RelativeLayout? = null
    var rlUpdateBank: RelativeLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_new_profile, container, false)
        activity = requireActivity()
        session = Session(activity)



        tvName = root.findViewById(R.id.tvName)
        tvMobile = root.findViewById(R.id.tvMobile)


        tvName!!.setText(session!!.getData(Constant.NAME))
        tvMobile!!.setText(session!!.getData(Constant.MOBILE))

        rlwithdrawhistory = root.findViewById(R.id.rlwithdrawhistory)
        rlhistory = root.findViewById(R.id.rlhistory)
        rlUpdateprofile = root.findViewById(R.id.rlUpdateprofile)
        rlChangepassword = root.findViewById(R.id.rlChangepassword)
        rlmyBank = root.findViewById(R.id.rlmyBank)
        rlLogout = root.findViewById(R.id.rlLogout)
        rlInvite = root.findViewById(R.id.rlInvite)
        rlWithdraw = root.findViewById(R.id.rlWithdraw)
        rlUpdateBank = root.findViewById(R.id.rlUpdateBank)


        rlwithdrawhistory!!.setOnClickListener(View.OnClickListener {
            requireActivity().startActivity(
                Intent(
                    activity,
                    WithdrawalStatusActivity::class.java
                )
            )
        })


        rlUpdateBank!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(activity, UpdateBankActivity::class.java)
            startActivity(intent)
        })

        rlWithdraw!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(getActivity(), WithdrawalActivity::class.java)
            startActivity(intent)
        })

        rlInvite!!.setOnClickListener(View.OnClickListener { v: View? ->
            val fragmentManager = parentFragmentManager
            val transaction = fragmentManager.beginTransaction()
            val inviteFragment = InviteFragment()

            // Replace current fragment with MyOrderFragment
            transaction.replace(R.id.Container, inviteFragment)
            transaction.addToBackStack(null) // Optional: Add to backstack to allow going back
            transaction.commit()

        })

        //        rlInvite.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               Intent intent =  new Intent(activity, InviteActivity.class);
//               activity.startActivity(intent);
//            }
//        });
        rlhistory!!.setOnClickListener(View.OnClickListener {
            requireActivity().startActivity(
                Intent(
                    activity,
                    TransactionActivity::class.java
                )
            )
        })


        rlUpdateprofile!!.setOnClickListener(View.OnClickListener {
            requireActivity().startActivity(
                Intent(
                    activity,
                    MyProfileActivity::class.java
                )
            )
        })
        rlmyBank!!.setOnClickListener(View.OnClickListener {
            requireActivity().startActivity(
                Intent(
                    activity,
                    MybanckActivity::class.java
                )
            )
        })

        rlChangepassword!!.setOnClickListener(View.OnClickListener {
            requireActivity().startActivity(
                Intent(
                    activity,
                    SetPasswordActivity::class.java
                )
            )
        })


        rlLogout!!.setOnClickListener(View.OnClickListener { session!!.logoutUser(activity) })






        return root
    }
}