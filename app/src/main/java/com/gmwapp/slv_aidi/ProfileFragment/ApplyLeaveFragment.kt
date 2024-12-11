package com.gmwapp.slv_aidi.ProfileFragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmwapp.slv_aidi.Adapter.LeaveAdapter
import com.gmwapp.slv_aidi.activities.MainActivity
import com.gmwapp.slv_aidi.databinding.FragmentApplyLeaveBinding
import com.gmwapp.slv_aidi.helper.ApiConfig
import com.gmwapp.slv_aidi.helper.Constant
import com.gmwapp.slv_aidi.helper.Session
import com.gmwapp.slv_aidi.model.LeaveListModel
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar

class ApplyLeaveFragment : Fragment() {
    lateinit var binding: FragmentApplyLeaveBinding
    lateinit var session: Session
    lateinit var leaveAdapter: LeaveAdapter
    var leaveList: MutableList<LeaveListModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentApplyLeaveBinding.inflate(inflater, container, false)
        session = Session(requireContext())

        (activity as MainActivity).binding.bottomNavigation.visibility = View.GONE

        binding.ibBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        setupRecyclerView()

        loadLeaveList()

        setupDOBPicker()

        binding.btnSubmit.setOnClickListener {
            applyLeaveApi()
        }


        return binding.root
    }

    private fun setupRecyclerView() {
        leaveAdapter = LeaveAdapter(requireContext(), leaveList)
        binding.rvLeaveList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLeaveList.adapter = leaveAdapter
    }

    private fun setupDOBPicker() {
        binding.edLeaveDate.setOnClickListener(View.OnClickListener { v: View? ->
            val c = Calendar.getInstance()
            val year = c[Calendar.YEAR]
            val month = c[Calendar.MONTH]
            val day = c[Calendar.DAY_OF_MONTH]

            @SuppressLint("SetTextI18n") val datePickerDialog = DatePickerDialog(
                requireActivity(),
                { view: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                    binding.edLeaveDate.setText(
                        year1.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth
                    )
                },
                year, month, day
            )
            datePickerDialog.show()
        })
    }

    private fun loadLeaveList () {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)

        ApiConfig.RequestToVolley({ result: Boolean, response: String? ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val gson = Gson()

                        leaveList.clear()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            // Change PlanListModel to Transanction
                            val leave = gson.fromJson(
                                jsonObject1.toString(),
                                LeaveListModel::class.java
                            )
                            leaveList.add(leave)
                        }

                        leaveAdapter.notifyDataSetChanged()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.LEAVE_LIST_URL, params, true)

        Log.d("LEAVE_LIST_URL", "LEAVE_LIST_URL: " + Constant.LEAVE_LIST_URL)
        Log.d("LEAVE_LIST_URL", "LEAVE_LIST_URL params: $params")
    }

    private fun applyLeaveApi() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.LEAVE_DATE] = binding.edLeaveDate.toString()
        params[Constant.REASON] = binding.edReason.toString()

        ApiConfig.RequestToVolley({ result: Boolean, response: String? ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                            leaveAdapter.notifyDataSetChanged()
                        Toast.makeText(requireActivity(), jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.LEAVE_LIST_URL, params, true)

        Log.d("LEAVE_LIST_URL", "LEAVE_LIST_URL: " + Constant.LEAVE_LIST_URL)
        Log.d("LEAVE_LIST_URL", "LEAVE_LIST_URL params: $params")
    }

}