package com.gmwapp.slv_aidi.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.gmwapp.slv_aidi.R
import com.gmwapp.slv_aidi.databinding.ActivityMainBinding
import com.gmwapp.slv_aidi.fragment.HomeFragment
import com.gmwapp.slv_aidi.fragment.JobsFragment
import com.gmwapp.slv_aidi.fragment.NewProfileFragment
import com.gmwapp.slv_aidi.fragment.NotificationFragment
import com.gmwapp.slv_aidi.fragment.OutsourceJobFragment
import com.gmwapp.slv_aidi.helper.ApiConfig
import com.gmwapp.slv_aidi.helper.Constant
import com.gmwapp.slv_aidi.helper.DatabaseHelper
import com.gmwapp.slv_aidi.helper.Session
import com.gmwapp.slv_aidi.model.ExtraPlanModel
import com.gmwapp.slv_aidi.model.PlanListModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import com.zoho.commons.InitConfig
import com.zoho.livechat.android.listeners.InitListener
import com.zoho.salesiqembed.ZohoSalesIQ
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private var navbar: BottomNavigationView? = null
    var activity: Activity? = null
    var session: Session? = null
    var NOTIFY_CHAT: String? = null
    var fetch_time: Long = 0
    var databaseHelper: DatabaseHelper? = null

    lateinit var binding: ActivityMainBinding

    val ONESIGNAL_APP_ID = "26ce46d3-9d0a-45c1-93b6-bf0c60b43c5a"


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

        fm!!.beginTransaction().replace(R.id.Container, HomeFragment()).commit()

        navbar!!.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
//                R.id.Profile -> if (session!!.getInt(Constant.CODES) < session!!.getInt(
//                        Constant.SYNC_CODES
//                    )
//                ) {
//                    fm!!.beginTransaction().replace(
//                        R.id.Container, NewProfileFragment()
//                    ).commitAllowingStateLoss()
//                } else {
//                    Toast.makeText(activity, "Please Sync Codes", Toast.LENGTH_SHORT).show()
//                }
//
//                R.id.Team -> if (session!!.getInt(Constant.CODES) < session!!.getInt(Constant.SYNC_CODES)) {
//                    fm!!.beginTransaction().replace(
//                        R.id.Container,
//                        ExtraIncomeFragment()
//                    ).commitAllowingStateLoss()
//                } else {
//                    Toast.makeText(activity, "Please Sync Codes", Toast.LENGTH_SHORT).show()
//                }

                R.id.Home -> fm!!.beginTransaction().replace(
                    R.id.Container, HomeFragment()).commitAllowingStateLoss()
                R.id.job -> fm!!.beginTransaction().replace(
                    R.id.Container, JobsFragment()).commitAllowingStateLoss()
                R.id.Team -> fm!!.beginTransaction().replace(
                    R.id.Container, OutsourceJobFragment()).commitAllowingStateLoss()
//                    R.id.Container, ExtraIncomeFragment()).commitAllowingStateLoss()
                R.id.Profile -> fm!!.beginTransaction().replace(
                    R.id.Container, NewProfileFragment()).commitAllowingStateLoss()
            }
            true
        })

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String? ->
            session!!.setData(
                Constant.FCM_ID, token
            )
        }

        initializeZohoSalesIQ()

        loadNotification()

        initializeOneSignal()

        initializeSettings()

    }

    private fun initializeOneSignal() {
        OneSignal.Debug.logLevel = LogLevel.VERBOSE
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(false)
        }
        OneSignal.login(session!!.getData(Constant.USER_ID))
    }

    // Method to hide the BottomNavigationView
    fun hideBottomNavigation() {
        navbar?.visibility = View.GONE
    }

    // Method to show the BottomNavigationView
    fun showBottomNavigation() {
        navbar?.visibility = View.VISIBLE
    }

    @SuppressLint("NotifyDataSetChanged")
    fun loadNotification() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session!!.getData(Constant.USER_ID)

        ApiConfig.RequestToVolley({ result: Boolean, response: String? ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val gson = Gson()

                        val notificationId = session!!.getData("notification_id")

                        if (notificationId.isEmpty() && notificationId == null) {
                            session!!.setData("notification_id", "0")
                            Log.d("notificationId", "notificationId " + notificationId)
                        } else {
                            if (jsonArray.length() > 0) {
                            // Get the first item in the array
                            val firstNotificationObject = jsonArray.getJSONObject(0)

                            // Extract the 'id' from the first notification
                            val firstNotificationId = firstNotificationObject.getString("id")

                            // Store the 'id' in the session
                            session!!.setData("notification_id", firstNotificationId)

                             val latestNotificationId = session!!.getData("notification_id")

                            // Optional: Log or show the stored ID for verification
                            Log.d("Notification ID", latestNotificationId)
                                if (notificationId < latestNotificationId){
                                    showNotificationDialog()
                                }
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, this@MainActivity, Constant.NOTIFICATION_LIST_URL, params, true)

        Log.d("NOTIFICATION_LIST_URL","NOTIFICATION_LIST_URL: ${Constant.NOTIFICATION_LIST_URL}")
        Log.d("NOTIFICATION_LIST_URL","NOTIFICATION_LIST_URL params: $params")
    }

    private fun showNotificationDialog() {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_notification, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false) // Disable the back button
            .create()

        // Prevent closing when tapping outside the dialog
        dialog.setCanceledOnTouchOutside(false)

        val btRead = dialogView.findViewById<MaterialButton>(R.id.btRead)

        btRead.setOnClickListener {
            // Navigate to NotificationFragment
            val notificationFragment = NotificationFragment()
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()

            // Optional: Add a transition animation
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)

            // Replace the current fragment with NotificationFragment
            transaction.replace(R.id.Container, notificationFragment)
            transaction.addToBackStack(null) // Optional: Add to backstack to allow going back
            transaction.commit()

            // Dismiss the dialog after navigating
            dialog.dismiss()
        }

        dialog.show()
    }

    fun userDetails() {
        Log.d("is_logged_in", "is_logged_in " + session!!.getBoolean("is_logged_in"))
        val params = java.util.HashMap<String, String>()
        params[Constant.USER_ID] = session!!.getData(Constant.USER_ID)
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    Log.d("SIGNUP_RES", response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val userObject = jsonArray.getJSONObject(0)

                        session!!.setUserData(
                            userObject.getString(Constant.ID),
                            userObject.getString(Constant.NAME),
                            userObject.getString(Constant.MOBILE),
                            userObject.getString(Constant.PASSWORD),
                            userObject.getString(Constant.DOB),
                            userObject.getString(Constant.EMAIL),
                            userObject.getString(Constant.CITY),
                            userObject.getString(Constant.REFERRED_BY),
                            userObject.getString(Constant.EARN),
                            userObject.getString(Constant.WITHDRAWAL),
                            userObject.getString(Constant.WITHDRAWAL_STATUS),
                            userObject.getString(Constant.TOTAL_REFERRALS),
                            userObject.getString(Constant.TODAY_CODES),
                            userObject.getString(Constant.TOTAL_CODES),
                            userObject.getString(Constant.BALANCE),
                            userObject.getString(Constant.DEVICE_ID),
                            userObject.getString(Constant.STATUS),
                            userObject.getString(Constant.REFER_CODE),
                            userObject.getString(Constant.REFER_BONUS_SENT),
                            userObject.getString(Constant.REGISTER_BONUS_SENT),
                            userObject.getString(Constant.CODE_GENERATE),
                            userObject.getString(Constant.CODE_GENERATE_TIME),
                            userObject.getString(Constant.FCM_ID),
                            userObject.getString(Constant.LAST_UPDATED),
                            userObject.getString(Constant.JOINED_DATE),
                            userObject.getString(Constant.APP_VERSION),
                            userObject.getString(Constant.PER_CODE_COST),
                            userObject.getString(Constant.PER_CODE_VAL),
//                            userObject.getString(Constant.WORKED_DAYS),
                            userObject.getString(Constant.RECHARGE),
                            userObject.getString(Constant.TOTAL_ASSETS),
                            userObject.getString(Constant.BONUS_WALLET),
                            userObject.getString(Constant.TEAM_INCOME),
                            userObject.getString(Constant.EARNING_WALLET),
                            userObject.getString(Constant.TOTAL_RECHARGE),
                            userObject.getString(Constant.TOTAL_CODES_INCOME),
                            userObject.getString(Constant.TOTAL_REFERRAL_INCOME),
                            userObject.getString(Constant.TODAY_EARNINGS),
                            userObject.getString(Constant.TOTAL_EARNINGS),
                            userObject.getString(Constant.MIN_WITHDRAWAL),
                            userObject.getString(Constant.FREE_PLAN),
                            userObject.getString(Constant.PAID_PLAN),
                            userObject.getString(Constant.AD_LINK),
//                            planObject.getString("name"),
//                            planObject.getString("description"),
//                            planObject.getString("validity"),
//                            planObject.getString("daily_codes"),
//                            planObject.getString("per_code_rate"),
//                            planObject.getString("daily_earnings")
                        )

                        Log.d("Plan", "Plan")

                        // Parse and save plan data
                        val planArray = userObject.getJSONArray("plan_activated")
                        val plans = mutableListOf<PlanListModel>()
                        for (i in 0 until planArray.length()) {
                            val planObject = planArray.getJSONObject(i)
                            val plan = PlanListModel(
                                planObject.getString("id"),
                                planObject.getString("name"),
                                planObject.getString("description"),
                                planObject.getString("image"),
                                planObject.getString("demo_video"),
                                planObject.getString("monthly_codes"),
                                planObject.getString("monthly_earnings"),
                                planObject.getString("per_code_cost"),
                                planObject.getString("price"),
                                planObject.getString("type"),
                                planObject.getString("min_refers"),
                                "0",
                                planObject.getString("sub_description"),
                                planObject.getString("active_link"),
                                0,
                                0
                            )
                            plans.add(plan)
                        }
                        // Save the list of plans in session
                        session!!.setPlanData(plans)

                        Log.d("Plan", "Plan extra_plan_activated")

                        Log.d("Plan", "Parsing extra_plan_activated")

                        // Parse and save plan data
                        val extraPlanArray = userObject.getJSONArray("extra_plan_activated")
                        val extraPlans = mutableListOf<ExtraPlanModel>()
                        for (i in 0 until extraPlanArray.length()) {
                            val extraPlanObject = extraPlanArray.getJSONObject(i)
                            val extraPlan = ExtraPlanModel(
                                extraPlanObject.getString("id"),
                                extraPlanObject.optString("name", "N/A"),
                                extraPlanObject.optString("description", "N/A"),
                                extraPlanObject.optString("price", "0"),
                                "0" // Default status
                            )
                            extraPlans.add(extraPlan)
                        }

                        // Save the list of plans in session
                        session?.setExtraPlanData(extraPlans)


                    } else {
                        Toast.makeText(
                            this,
                            jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("Plan", "Plan error: ${e.message}")
                }
            } else {
                Toast.makeText(
                    this,
                    "Failed to retrieve data.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, this, Constant.USER_DETAILS_API, params, true)

        Log.d("USER_DETAILS_API", "USER_DETAILS_API: " + Constant.USER_DETAILS_API)
        Log.d("USER_DETAILS_API", "USER_DETAILS_API params: " + params)
    }

    fun selectBottomNavItem(itemId: Int) {
        navbar?.apply {
            selectedItemId = itemId
            Log.d("BottomNav", "Selected item ID: $itemId")  // Debugging log
        }
    }

    fun navigateToHome() {
        selectBottomNavItem(R.id.Home)  // Use selectBottomNavItem here
        fm!!.beginTransaction().replace(R.id.Container, HomeFragment()).commitAllowingStateLoss()
    }


    private fun walletApi() {
        finishAffinity()
    }

    // onstart
    override fun onStart() {
        super.onStart()
        userDetails()

    }

    // onstop
    override fun onStop() {
        super.onStop()
        userDetails()
        walletApi()
    }


    fun initializeSettings() {
        val params: Map<String, String> = java.util.HashMap()

        ApiConfig.RequestToVolley({ result: Boolean, response: String? ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    val message = jsonObject.getString("message")
                    val growVideo = jsonObject.getString("grow_video")
                    val jobVideo = jsonObject.getString("job_video")
                    val outsourceJobVideo = jsonObject.getString("outsource_job_video")

                    session!!.setData(Constant.GROW_VIDEO, growVideo)
                    session!!.setData(Constant.JOB_VIDEO, jobVideo)
                    session!!.setData(Constant.OUTSOURCE_JOB_VIDEO, outsourceJobVideo)

                    if (jsonObject.getBoolean("success")) {
                        val jsonArray = jsonObject.getJSONArray("data")
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Log.d("SETTINGS_URL", "SETTINGS_URL Error: " + e.message)
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }, this, Constant.SETTINGS_URL, params, true)
        Log.d("SETTINGS_URL", "SETTINGS_URL: " + Constant.SETTINGS_URL)
        Log.d("SETTINGS_URL", "SETTINGS_URL params: $params")
    }

    private fun initializeZohoSalesIQ() {
        val initConfig = InitConfig()
        ZohoSalesIQ.init(
            application,
            "5spwCGjIKo%2Fz6ssVNakmHbMTvtsszyor90%2BhrhHmnNgJcnpMvghcPXmu4dO6kxpO_in",
            "4%2Fd2z2OovwP9rRaj3CO5TQtzMKPKxu%2FFaEkvD5l3RKcCLPKYaPjW%2B%2BzKEVzDx8I3UedpF6j3RR3PecllV1z3JrF3PMI%2BXoxRDSvLRDVerhOt%2FtApSWo%2FVw%3D%3D",
            initConfig,
            object :
                InitListener {
                override fun onInitSuccess() {
                    // Initialization successful
                }

                override fun onInitError(errorCode: Int, errorMessage: String) {
                    // Handle initialization errors
                }
            }
        )
    }

    companion object {
        @JvmField
        var fm: FragmentManager? = null
    }
}