package com.app.smart_id_maker.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.app.smart_id_maker.R
import com.app.smart_id_maker.databinding.ActivityMainBinding
import com.app.smart_id_maker.fragment.ExtraIncomeFragment
import com.app.smart_id_maker.fragment.HomeFragment
import com.app.smart_id_maker.fragment.NewProfileFragment
import com.app.smart_id_maker.fragment.TeamFragment
import com.app.smart_id_maker.fragment.WalletFragment
import com.app.smart_id_maker.helper.ApiConfig
import com.app.smart_id_maker.helper.Constant
import com.app.smart_id_maker.helper.DatabaseHelper
import com.app.smart_id_maker.helper.Session
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private var navbar: BottomNavigationView? = null
    var activity: Activity? = null
    var session: Session? = null
    var NOTIFY_CHAT: String? = null
    var dialog: Dialog? = null
    var fetch_time: Long = 0
    var databaseHelper: DatabaseHelper? = null
    var RandomId: String? = null
    var reference: DatabaseReference? = null
    var btnChatUs: Button? = null

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fm = supportFragmentManager
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)

        navbar = findViewById(R.id.bottomNavigation)
        navbar!!.setSelectedItemId(R.id.Home)
        activity = this@MainActivity
        databaseHelper = DatabaseHelper(activity)
        session = Session(activity)
        NOTIFY_CHAT = intent.getStringExtra("NOTIFY_CHAT")
        if (session!!.getBoolean(Constant.CHECK_NOTIFICATION)) {
            showReadNotificationPopup()
        }

        fm!!.beginTransaction().replace(R.id.Container, HomeFragment()).commit()

        navbar!!.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Profile -> if (session!!.getInt(Constant.CODES) < session!!.getInt(
                        Constant.SYNC_CODES
                    )
                ) {
                    fm!!.beginTransaction().replace(R.id.Container, NewProfileFragment())
                        .commitAllowingStateLoss()
                } else {
                    Toast.makeText(activity, "Please Sync Codes", Toast.LENGTH_SHORT).show()
                }

                R.id.Team -> if (session!!.getInt(Constant.CODES) < session!!.getInt(Constant.SYNC_CODES)) {
                    fm!!.beginTransaction().replace(R.id.Container, TeamFragment())
                        .commitAllowingStateLoss()
                } else {
                    Toast.makeText(activity, "Please Sync Codes", Toast.LENGTH_SHORT).show()
                }

                R.id.Home -> fm!!.beginTransaction().replace(R.id.Container, HomeFragment())
                    .commitAllowingStateLoss()

                R.id.job -> fm!!.beginTransaction().replace(R.id.Container, ExtraIncomeFragment())
                    .commitAllowingStateLoss()
            }
            true
        })

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String? ->
            session!!.setData(
                Constant.FCM_ID, token
            )
        }
    }

    //    private void checkJoining() {
    //        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constant.JOINING_TICKET).child(session.getData(Constant.MOBILE));
    //        FirebaseDatabase.getInstance()
    //                .getReference(Constant.JOINING_TICKET).child(session.getData(Constant.MOBILE)).addValueEventListener(new ValueEventListener() {
    //                    @Override
    //                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
    //                        if (dataSnapshot.exists()) {
    //                            Ticket user = dataSnapshot.getValue(Ticket.class);
    //                            Log.d("NOT_EXIST",user.getType() + " - "+session.getData(Constant.MOBILE));
    //
    //                            sendChat(user.getId(),user.getName(),user.getCategory(),user.getType(),user.getDescription());
    //
    //                        }
    //                        else {
    //                            joinTicket();
    //
    //                        }
    //                    }
    //
    //                    @Override
    //                    public void onCancelled(@NonNull DatabaseError databaseError) {
    //
    //                    }
    //                });
    //    }
    private fun joinTicket() {
        val tsLong = System.currentTimeMillis() / 1000
        RandomId = session!!.getData(Constant.USER_ID) + "_" + tsLong.toString()
        reference = FirebaseDatabase.getInstance().getReference(Constant.JOINING_TICKET).child(
            session!!.getData(Constant.MOBILE)
        )
        val hashMap = HashMap<String, Any>()
        hashMap[Constant.ID] = RandomId!!
        hashMap[Constant.CATEGORY] = "Joining"
        hashMap[Constant.DESCRIPTION] = "Enquiry For Joining"
        hashMap[Constant.USER_ID] = session!!.getData(Constant.USER_ID)
        hashMap[Constant.NAME] = session!!.getData(Constant.NAME)
        hashMap[Constant.MOBILE] = session!!.getData(Constant.MOBILE)
        hashMap[Constant.TYPE] = Constant.JOINING_TICKET
        hashMap[Constant.SUPPORT] = "Admin"
        hashMap[Constant.REFERRED_BY] = session!!.getData(Constant.REFERRED_BY)
        hashMap[Constant.TIMESTAMP] = tsLong.toString()
        reference!!.setValue(hashMap).addOnCompleteListener { task1: Task<Void?>? -> }
    }

    //    private void sendChat(String id, String name, String category, String type, String description) {
    //
    //        //Log.d("CHAT_DETAILS","USER_ID - "+id + "\nName - "+name+"\nCategory - "+category+"\nType - "+type +"Description - "+description);
    //        final Intent intent = new Intent(activity, MessageActivity.class);
    //        intent.putExtra(EXTRA_USER_ID, id);
    //        intent.putExtra(TICKET_ID, id);
    //        intent.putExtra(NAME, name);
    //        intent.putExtra(TYPE, type);
    //        intent.putExtra(DESCRIPTION, description);
    //        intent.putExtra(CATEGORY, category);
    //        startActivity(intent);
    //    }
    private fun importUrl() {
        val params: Map<String, String> = HashMap()
        ApiConfig.RequestToVolley({ result: Boolean, response: String? ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        databaseHelper!!.deleteUrls()
                        val `object` = JSONObject(response)
                        val jsonArray = `object`.getJSONArray(Constant.DATA)
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            if (jsonObject1 != null) {
                                databaseHelper!!.AddtoUrl(
                                    jsonObject1.getString(Constant.ID), jsonObject1.getString(
                                        Constant.URL
                                    ), "0"
                                )
                            } else {
                                break
                            }
                        }
                    } else {
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.IMPORT_URLS, params, false)
    }

    private fun exploreApi(type: String) {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session!!.getData(Constant.USER_ID)
        params[Constant.TYPE] = type
        ApiConfig.RequestToVolley(
            { result: Boolean, response: String? -> },
            activity,
            Constant.EXPLORE_URL,
            params,
            false
        )
    }

    // Do something with the data
    // coming from the AlertDialog
    private fun sendDialogDataToActivity(data: String) {
        Toast.makeText(
            this,
            data,
            Toast.LENGTH_SHORT
        )
            .show()
    }

    override fun onStart() {
        super.onStart()
    }

    var doubleBackToExitPressedOnce: Boolean = false

    //    @Override
    //    public void onBackPressed() {
    //        if (doubleBackToExitPressedOnce) {
    //            onStop();
    //        }
    //
    //        this.doubleBackToExitPressedOnce = true;
    //        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
    //
    //        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
    //            @Override
    //            public void run() {
    //                doubleBackToExitPressedOnce = false;
    //            }
    //        }, 2000);
    //    }
    fun walletApi() {
        finishAffinity()
    }

    override fun onStop() {
        super.onStop()
        walletApi()
    }


    private fun showWallet() {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("Please wait...")
        progressDialog.setMessage("Fetching Transactions")
        progressDialog.show()
        Handler().postDelayed({
            progressDialog.dismiss()
            fm!!.beginTransaction().replace(R.id.Container, WalletFragment())
                .commitAllowingStateLoss()
        }, fetch_time)
    }

    fun showReadNotificationPopup() {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage("New Notification Available.")
            .setCancelable(false)
            .setPositiveButton("Click here to read") { dialog, id ->
                //                        Intent intent = new Intent(activity, NotificaionActivity.class);
//                        startActivity(intent);
            }
        val alert = builder.create()
        alert.show()
    }

    companion object {
        @JvmField
        var fm: FragmentManager? = null
    }
}