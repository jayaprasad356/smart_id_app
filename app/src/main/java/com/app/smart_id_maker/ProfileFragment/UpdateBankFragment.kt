package com.app.smart_id_maker.ProfileFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.app.smart_id_maker.activities.MainActivity
import com.app.smart_id_maker.activities.WithdrawalActivity
import com.app.smart_id_maker.databinding.FragmentUpdateBankBinding
import com.app.smart_id_maker.helper.ApiConfig
import com.app.smart_id_maker.helper.Constant
import com.app.smart_id_maker.helper.Session
import org.json.JSONException
import org.json.JSONObject

class UpdateBankFragment : Fragment() {

    private lateinit var binding: FragmentUpdateBankBinding
    private lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using view binding
        binding = FragmentUpdateBankBinding.inflate(inflater, container, false)
        session = Session(requireContext())

        // Hide Bottom Navigation
        (requireActivity() as MainActivity).binding.bottomNavigation.visibility = View.GONE

        // Set initial values in EditTexts from session data
        binding.etAccountnum.setText(session.getData(Constant.ACCOUNT_NUM))
        binding.etHolderName.setText(session.getData(Constant.HOLDER_NAME))
        binding.etBankname.setText(session.getData(Constant.BANK))
        binding.etBranch.setText(session.getData(Constant.BRANCH))
        binding.etIfsc.setText(session.getData(Constant.IFSC))

        // Load bank details from the API
        bankDetailsApi()

        // Set back button listener
        binding.ibBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Update bank details on button click
        binding.btnUpdateBank.setOnClickListener {
            validateAndSubmitBankDetails()
        }

        return binding.root
    }

    private fun validateAndSubmitBankDetails() {
        // Validate user input
        when {
            binding.etAccountnum.text.toString().trim().isEmpty() -> {
                binding.etAccountnum.error = "Please enter account number"
                binding.etAccountnum.requestFocus()
            }
            binding.etHolderName.text.toString().trim().isEmpty() -> {
                binding.etHolderName.error = "Please enter holder name"
                binding.etHolderName.requestFocus()
            }
            binding.etBankname.text.toString().trim().isEmpty() -> {
                binding.etBankname.error = "Please enter bank name"
                binding.etBankname.requestFocus()
            }
            binding.etBranch.text.toString().trim().isEmpty() -> {
                binding.etBranch.error = "Please enter branch"
                binding.etBranch.requestFocus()
            }
            binding.etIfsc.text.toString().trim().isEmpty() -> {
                binding.etIfsc.error = "Please enter IFSC code"
                binding.etIfsc.requestFocus()
            }
            else -> {
                // Call update bank API if all fields are valid
                updateBank()
            }
        }
    }

    private fun updateBank() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.ACCOUNT_NUM] = binding.etAccountnum.text.toString().trim()
        params[Constant.HOLDER_NAME] = binding.etHolderName.text.toString().trim()
        params[Constant.BANK] = binding.etBankname.text.toString().trim()
        params[Constant.BRANCH] = binding.etBranch.text.toString().trim()
        params[Constant.IFSC] = binding.etIfsc.text.toString().trim()

        // Make the API call to update bank details
        ApiConfig.RequestToVolley({ result, response ->
            Log.d("BANK_DETAILS_RES", response ?: "No response")
            if (result) {
                try {
                    val jsonObject = JSONObject(response ?: "")
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Toast.makeText(
                            requireActivity(),
                            jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()

                        // Save updated bank details to session
                        val bankArray = jsonObject.getJSONArray(Constant.DATA)
                        session.setData(Constant.ACCOUNT_NUM, bankArray.getJSONObject(0).getString(Constant.ACCOUNT_NUM))
                        session.setData(Constant.HOLDER_NAME, bankArray.getJSONObject(0).getString(Constant.HOLDER_NAME))
                        session.setData(Constant.BANK, bankArray.getJSONObject(0).getString(Constant.BANK))
                        session.setData(Constant.BRANCH, bankArray.getJSONObject(0).getString(Constant.BRANCH))
                        session.setData(Constant.IFSC, bankArray.getJSONObject(0).getString(Constant.IFSC))

                        // Navigate to WithdrawalActivity
                        startActivity(Intent(requireActivity(), WithdrawalActivity::class.java))
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireActivity(), jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, requireActivity(), Constant.UPDATE_BANK_URL, params, true)
    }

    private fun bankDetailsApi() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)

        // Fetch bank details from the API
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response ?: "")
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        val jsonArray = jsonObject.getJSONArray(Constant.DATA)
                        binding.etAccountnum.setText(jsonArray.getJSONObject(0).getString(Constant.ACCOUNT_NUM))
                        binding.etHolderName.setText(jsonArray.getJSONObject(0).getString(Constant.HOLDER_NAME))
                        binding.etBankname.setText(jsonArray.getJSONObject(0).getString(Constant.BANK))
                        binding.etBranch.setText(jsonArray.getJSONObject(0).getString(Constant.BRANCH))
                        binding.etIfsc.setText(jsonArray.getJSONObject(0).getString(Constant.IFSC))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, requireActivity(), Constant.BANK_DETAILS_URL, params, true)
    }
}
