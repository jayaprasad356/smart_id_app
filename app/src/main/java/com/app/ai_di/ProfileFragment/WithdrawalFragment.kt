package com.app.ai_di.ProfileFragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ai_di.Adapter.RedeemedAdapter
import com.app.ai_di.activities.MainActivity
import com.app.ai_di.databinding.FragmentWithdrawalBinding
import com.app.ai_di.helper.ApiConfig
import com.app.ai_di.helper.Constant
import com.app.ai_di.helper.Session
import com.app.ai_di.model.Redeem
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
        binding.tvBalance.text = "Available Balance = ₹${session.getData(Constant.BALANCE)}"
        binding.tvminumumRedeem.text = "Minimum Redeem = ₹${session.getData(Constant.MIN_WITHDRAWAL)}"
        binding.tvEarningBalance.text = "₹${session.getData(Constant.EARNING_WALLET)}"
        binding.tvBonusBalance.text = "₹${session.getData(Constant.BONUS_WALLET)}"

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
            val mimAmount = session.getData(Constant.MIN_WITHDRAWAL)
            when {
                amount.isEmpty() || amount == "0" -> {
                    binding.etAmount.error = "Enter amount"
                    binding.etAmount.requestFocus()
                }
                amount.toDouble() < mimAmount.toDouble() -> {
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

        // Set ADD MAIN BALANCE Button Listener
        binding.btnAddEarningToWallet.setOnClickListener {
            addToMainBalance("earning_wallet")
        }

        // Set ADD MAIN BALANCE Button Listener
        binding.btnAddBonusToWallet.setOnClickListener {
            addToMainBalance("bonus_wallet")
        }

        binding.btWatchDemo.setOnClickListener(View.OnClickListener { v: View? ->
            val url = Constant.WITHDRAWAL_DEMO_VIDEO_URL
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(url))
            startActivity(intent)
        })

        return binding.root
    }

    private fun reloadFragment() {
        parentFragmentManager.beginTransaction().apply {
            replace(this@WithdrawalFragment.id, WithdrawalFragment())
            commit()
        }
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

        Log.d("WITHDRAWAL_LIST_URL","WITHDRAWAL_LIST_URL: ${Constant.WITHDRAWAL_LIST_URL}")
        Log.d("WITHDRAWAL_LIST_URL","WITHDRAWAL_LIST_URL params: $params")
    }

    private fun addToMainBalance(walletType: String) {
        val params = HashMap<String, String>()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.Wallet_TYPE] = walletType

        ApiConfig.RequestToVolley({ result: Boolean, response: String? ->
            Log.d("ADD_MAIN_BALANCE_RES", response ?: "No response")
            if (result) {
                try {
                    val jsonObject = JSONObject(response ?: "")
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)

                        (activity as? MainActivity)?.userDetails()

                        Toast.makeText(
                            requireActivity(),
                            jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()

                        if (walletType == "earning_wallet") {
                            binding.btnAddEarningToWalletLoader.visibility = View.VISIBLE
                            binding.btnAddEarningToWallet.visibility = View.GONE
                        } else if (walletType == "bonus_wallet") {
                            binding.btnAddBonusToWalletLoader.visibility = View.VISIBLE
                            binding.btnAddBonusToWallet.visibility = View.GONE
                        }
                        Handler().postDelayed({
                            if (walletType == "earning_wallet") {
                                binding.btnAddEarningToWalletLoader.visibility = View.GONE
                                binding.btnAddEarningToWallet.visibility = View.VISIBLE
                            } else if (walletType == "bonus_wallet") {
                                binding.btnAddBonusToWalletLoader.visibility = View.GONE
                                binding.btnAddBonusToWallet.visibility = View.VISIBLE
                            }
                            reloadFragment()
                        }, 2000)

                    } else {
                        Toast.makeText(
                            requireActivity(),
                            jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        requireActivity(),
                        "" + e.printStackTrace(),
                        Toast.LENGTH_SHORT
                    ).show()
                    e.printStackTrace()
                }
            }
        }, requireActivity(), Constant.ADD_MAIN_BALANCE, params, true)

        Log.d("ADD_MAIN_BALANCE","ADD_MAIN_BALANCE: ${Constant.WITHDRAWAL_LIST_URL}")
        Log.d("ADD_MAIN_BALANCE","ADD_MAIN_BALANCE params: $params")
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
                        (activity as? MainActivity)?.userDetails()
                        redeemList()
//                        startActivity(Intent(requireActivity(), MainActivity::class.java))
//                        requireActivity().finish() // Close current activity
                        binding.btnWithdrawalLoader.visibility = View.VISIBLE
                        binding.btnWithdrawal.visibility = View.GONE
                        Handler().postDelayed({
                            binding.btnWithdrawalLoader.visibility = View.GONE
                            binding.btnWithdrawal.visibility = View.VISIBLE
                            reloadFragment()
                        }, 2000)
                    } else {
                        Toast.makeText(requireActivity(), jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, requireActivity(), Constant.WITHDRAWAL_URL, params, true)

        Log.d("WITHDRAWAL_URL","WITHDRAWAL_URL: ${Constant.WITHDRAWAL_URL}")
        Log.d("WITHDRAWAL_URL","WITHDRAWAL_URL params: $params")
    }
}
