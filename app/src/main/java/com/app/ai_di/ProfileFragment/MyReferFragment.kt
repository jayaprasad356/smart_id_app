package com.app.ai_di.ProfileFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ai_di.Adapter.ReferUserAdapter
import com.app.ai_di.Adapter.TransactionAdapter
import com.app.ai_di.activities.MainActivity
import com.app.ai_di.databinding.FragmentMyReferBinding
import com.app.ai_di.helper.ApiConfig
import com.app.ai_di.helper.Constant
import com.app.ai_di.helper.Session
import com.app.ai_di.model.ReferPlansModel
import org.json.JSONException
import org.json.JSONObject

class MyReferFragment : Fragment() {
    lateinit var binding: FragmentMyReferBinding
    lateinit var session: Session
    lateinit var referUserAdapter: ReferUserAdapter
    var referPlans: MutableList<ReferPlansModel> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyReferBinding.inflate(inflater, container, false)
        session = Session(requireContext())

        (activity as MainActivity).binding.bottomNavigation.visibility = View.GONE

        binding.ibBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        setupRecyclerView()

        loadRefer()

        return binding.root
    }

    private fun setupRecyclerView() {
        referUserAdapter = ReferUserAdapter(requireActivity(), referPlans)
        binding.rvReferList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReferList.adapter = referUserAdapter
    }

    fun loadRefer() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)

        ApiConfig.RequestToVolley({ result: Boolean, response: String? ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        referPlans.clear()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            val plansObject = jsonObject1.getJSONObject("plans")

                            val basicPlan = plansObject.optString("Basic Plan - ₹ 2999", "0")
                            val standardPlan = plansObject.optString("Standard Plan - ₹ 3999", "0")
                            val freeTrail = plansObject.optString("Free Trail Earning - 4 Days", "0")
                            val advancedPlan = plansObject.optString("Advanced Plan - ₹5999", "0")

                            val mobile = jsonObject1.optString("mobile", "N/A")
                            val joinedDate = jsonObject1.optString("joined_date", "N/A")

                            val referPlan = ReferPlansModel(basicPlan, standardPlan, advancedPlan, freeTrail, mobile, joinedDate)
                            referPlans.add(referPlan)
                        }

                        referUserAdapter.notifyDataSetChanged()
                    }
                } catch (e: JSONException) {
                    Log.d("MY_TEAM", "MY_TEAM Error: " + e.message)
                    e.printStackTrace()
                }
            }
        }, activity, Constant.MY_TEAM, params, true)

        Log.d("MY_TEAM", "MY_TEAM: " + Constant.MY_TEAM)
        Log.d("MY_TEAM", "MY_TEAM params: $params")
    }


}