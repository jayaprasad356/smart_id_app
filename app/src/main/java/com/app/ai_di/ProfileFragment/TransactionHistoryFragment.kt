package com.app.ai_di.ProfileFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.ai_di.Adapter.TransactionAdapter
import com.app.ai_di.activities.MainActivity
import com.app.ai_di.databinding.FragmentTransactionHistoryBinding
import com.app.ai_di.helper.ApiConfig
import com.app.ai_di.helper.Constant
import com.app.ai_di.helper.Session
import com.app.ai_di.model.Transanction
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

class TransactionHistoryFragment : Fragment() {
    lateinit var binding: FragmentTransactionHistoryBinding
    lateinit var session: Session
    lateinit var transactionAdapter: TransactionAdapter
    var transactions: MutableList<Transanction> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransactionHistoryBinding.inflate(inflater, container, false)
        session = Session(requireContext())

        (activity as MainActivity).binding.bottomNavigation.visibility = View.GONE

        binding.ibBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        setupRecyclerView()

        loadTransactions()

        return binding.root
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(requireContext(), transactions)
        binding.rvTransaction.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransaction.adapter = transactionAdapter
    }

    private fun loadTransactions() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)

        ApiConfig.RequestToVolley({ result: Boolean, response: String? ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        val gson = Gson()

                        transactions.clear()

                        for (i in 0 until jsonArray.length()) {
                            val jsonObject1 = jsonArray.getJSONObject(i)
                            // Change PlanListModel to Transanction
                            val transaction = gson.fromJson(
                                jsonObject1.toString(),
                                Transanction::class.java
                            )
                            transactions.add(transaction)
                        }

                        transactionAdapter.notifyDataSetChanged()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, activity, Constant.TRNSACTION_LIST_URL, params, true)

        Log.d("TRNSACTION_LIST_URL", "TRNSACTION_LIST_URL: " + Constant.TRNSACTION_LIST_URL)
        Log.d("TRNSACTION_LIST_URL", "TRNSACTION_LIST_URL params: $params")
    }

}
