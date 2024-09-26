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
import com.app.smart_id_maker.ProfileFragment.MyBankFragment
import com.app.smart_id_maker.ProfileFragment.MyProfileFragment
import com.app.smart_id_maker.ProfileFragment.SetPasswordFragment
import com.app.smart_id_maker.ProfileFragment.TransactionHistoryFragment
import com.app.smart_id_maker.ProfileFragment.UpdateBankFragment
import com.app.smart_id_maker.ProfileFragment.WithdrawalFragment
import com.app.smart_id_maker.ProfileFragment.WithdrawalHistoryFragment
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
    var rlTransectionHistory: RelativeLayout? = null
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

        val fragmentManager = parentFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val inviteFragment = InviteFragment()
        val withdrawalFragment = WithdrawalFragment()
        val withdrawalHistoryFragment = WithdrawalHistoryFragment()
        val transactionHistoryFragment = TransactionHistoryFragment()
        val myProfileFragment = MyProfileFragment()
        val setPasswordFragment = SetPasswordFragment()
        val updateBankFragment = UpdateBankFragment()
        val myBankFragment = MyBankFragment()

        tvName!!.setText(session!!.getData(Constant.NAME))
        tvMobile!!.setText(session!!.getData(Constant.MOBILE))

        rlwithdrawhistory = root.findViewById(R.id.rlwithdrawhistory)
        rlTransectionHistory = root.findViewById(R.id.rlTransectionHistory)
        rlUpdateprofile = root.findViewById(R.id.rlUpdateprofile)
        rlChangepassword = root.findViewById(R.id.rlChangepassword)
        rlmyBank = root.findViewById(R.id.rlmyBank)
        rlLogout = root.findViewById(R.id.rlLogout)
        rlInvite = root.findViewById(R.id.rlInvite)
        rlWithdraw = root.findViewById(R.id.rlWithdraw)
        rlUpdateBank = root.findViewById(R.id.rlUpdateBank)

        rlwithdrawhistory!!.setOnClickListener(View.OnClickListener {
            // Replace current fragment with withdrawalHistoryFragment
            transaction.replace(R.id.Container, withdrawalHistoryFragment)
            transaction.addToBackStack(null) // Optional: Add to backstack to allow going back
            transaction.commit()
        })

        rlUpdateBank!!.setOnClickListener(View.OnClickListener {
            // Replace current fragment with updateBankFragment
            transaction.replace(R.id.Container, updateBankFragment)
            transaction.addToBackStack(null) // Optional: Add to backstack to allow going back
            transaction.commit()
        })

        rlWithdraw!!.setOnClickListener(View.OnClickListener {
            // Replace current fragment with withdrawalFragment
            transaction.replace(R.id.Container, withdrawalFragment)
            transaction.addToBackStack(null) // Optional: Add to backstack to allow going back
            transaction.commit()
        })

        rlInvite!!.setOnClickListener(View.OnClickListener { v: View? ->
            // Replace current fragment with inviteFragment
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
        rlTransectionHistory!!.setOnClickListener(View.OnClickListener {
            // Replace current fragment with transactionHistoryFragment
            transaction.replace(R.id.Container, transactionHistoryFragment)
            transaction.addToBackStack(null) // Optional: Add to backstack to allow going back
            transaction.commit()
        })


        rlUpdateprofile!!.setOnClickListener(View.OnClickListener {
            // Replace current fragment with myProfileFragment
            transaction.replace(R.id.Container, myProfileFragment)
            transaction.addToBackStack(null) // Optional: Add to backstack to allow going back
            transaction.commit()
        })
        rlmyBank!!.setOnClickListener(View.OnClickListener {
            // Replace current fragment with myBankFragment
            transaction.replace(R.id.Container, myBankFragment)
            transaction.addToBackStack(null) // Optional: Add to backstack to allow going back
            transaction.commit()
        })

        rlChangepassword!!.setOnClickListener(View.OnClickListener {
            // Replace current fragment with setPasswordFragment
            transaction.replace(R.id.Container, setPasswordFragment)
            transaction.addToBackStack(null) // Optional: Add to backstack to allow going back
            transaction.commit()
        })


        rlLogout!!.setOnClickListener(View.OnClickListener { session!!.logoutUser(activity) })






        return root
    }
}