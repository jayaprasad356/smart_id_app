package com.app.smart_id_maker.ProfileFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.smart_id_maker.Adapter.RedeemedAdapter
import com.app.smart_id_maker.R
import com.app.smart_id_maker.activities.MainActivity
import com.app.smart_id_maker.databinding.FragmentWithdrawalBinding
import com.app.smart_id_maker.helper.ApiConfig
import com.app.smart_id_maker.helper.Constant
import com.app.smart_id_maker.helper.Session
import com.app.smart_id_maker.model.Redeem
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

class WithdrawalFragment : Fragment() {

    lateinit var binding: FragmentWithdrawalBinding
    lateinit var session: Session
    lateinit var redeemedAdapter: RedeemedAdapter
    var type: String = "bank_transfer"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWithdrawalBinding.inflate(inflater, container, false)
        session = Session(requireContext())

        // Hide Bottom Navigation
        (requireActivity() as MainActivity).binding.bottomNavigation.visibility = View.GONE

        // Set up views and values using binding
        binding.tvBalance.text = "Available Balance = ₹" + session.getData(Constant.BALANCE)
        binding.tvminumumRedeem.text = "Minimum Redeem = ₹" + session.getData(Constant.MIN_WITHDRAWAL)

        val linearLayoutManager = LinearLayoutManager(requireActivity())
        binding.recycler.layoutManager = linearLayoutManager

        redeemList() // Fetch redeemed data

        // Set Back Button Listener
        binding.ibBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Set Withdrawal Button Listener
        binding.btnWithdrawal.setOnClickListener {
            val amount = binding.etAmount.text.toString().trim()
            when {
                amount.isEmpty() || amount == "0" -> {
                    binding.etAmount.error = "Enter amount"
                    binding.etAmount.requestFocus()
                }
                amount.toDouble() < 250 -> {
                    Toast.makeText(
                        requireActivity(),
                        "Minimum " + session.getData(Constant.MIN_WITHDRAWAL) + " balance required",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                amount.toDouble() > session.getData(Constant.BALANCE).toDouble() -> {
                    Toast.makeText(requireActivity(), "Insufficient balance", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    type = if (binding.rbBanktransfer.isChecked) {
                        "bank_transfer"
                    } else {
                        "cash_payment"
                    }
                    withdrawalApi()
                }
            }
        }

        return binding.root
    }

    private fun redeemList() {
        val params = HashMap<String, String>()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)

        ApiConfig.RequestToVolley({ result: Boolean, response: String? ->
            Log.d("WITHDRAWAL_RES", response ?: "No response")
            if (result) {
                try {
                    val jsonObject = JSONObject(response ?: "")
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val g = Gson()
                        val redeems = ArrayList<Redeem>()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            val group = g.fromJson(jsonObject1.toString(), Redeem::class.java)
                            redeems.add(group)
                        }

                        redeemedAdapter = RedeemedAdapter(requireActivity(), redeems)
                        binding.recycler.adapter = redeemedAdapter
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }, requireActivity(), Constant.WITHDRAWAL_LIST_URL, params, true)
    }

    private fun withdrawalApi() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.AMOUNT] = binding.etAmount.text.toString().trim()
        params[Constant.TYPE] = type

        ApiConfig.RequestToVolley({ result: Boolean, response: String? ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response ?: "")
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Toast.makeText(requireActivity(), jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                        session.setData(Constant.BALANCE, jsonObject.getString(Constant.BALANCE))
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finish() // Close current activity
                    } else {
                        Toast.makeText(requireActivity(), jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, requireActivity(), Constant.WITHDRAWAL_URL, params, true)
    }
}
