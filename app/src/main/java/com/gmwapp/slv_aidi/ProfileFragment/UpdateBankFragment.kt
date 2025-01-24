package com.gmwapp.slv_aidi.ProfileFragment

import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gmwapp.slv_aidi.activities.MainActivity
import com.gmwapp.slv_aidi.databinding.FragmentUpdateBankBinding
import com.gmwapp.slv_aidi.helper.ApiConfig
import com.gmwapp.slv_aidi.helper.Constant
import com.gmwapp.slv_aidi.helper.Session
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

        // Set character limit for etHolderName to 25
        binding.etHolderName.filters = arrayOf(InputFilter.LengthFilter(25))

        // Set initial values in EditTexts from session data
        binding.etAccountNumber.setText(session.getData(Constant.ACCOUNT_NUM))
        binding.etHolderName.setText(session.getData(Constant.HOLDER_NAME))
        binding.etBankName.setText(session.getData(Constant.BANK))
        binding.etBranch.setText(session.getData(Constant.BRANCH))
        binding.etIfsc.setText(session.getData(Constant.IFSC))

        // Apply InputFilters to etBankName and etBranch
        val textOnlyFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.toString().matches(Regex("^[a-zA-Z ]*$"))) {
                null // Accept the input
            } else {
                "" // Reject invalid input
            }
        }
        binding.etBankName.filters = arrayOf(textOnlyFilter)
        binding.etBranch.filters = arrayOf(textOnlyFilter)

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
        val textOnlyRegex = Regex("^[a-zA-Z ]+$")
        val ifscCodeRegex = Regex("^[A-Za-z0-9]{4}0[A-Za-z0-9]{6}\$")
        // Validate user input
        when {
            binding.etAccountNumber.text.toString().trim().isEmpty() -> {
                binding.etAccountNumber.error = "Please enter account number"
                binding.etAccountNumber.requestFocus()
            }
            binding.etIfsc.text.toString().trim().isEmpty() -> {
                binding.etIfsc.error = "Please enter IFSC code"
                binding.etIfsc.requestFocus()
            }
            binding.etIfsc.text.toString().trim().length != 11 -> {
                binding.etIfsc.error = "IFSC code must be 11 characters"
                binding.etIfsc.requestFocus()
            }
            !binding.etIfsc.text.toString().trim().matches(ifscCodeRegex) -> {
                binding.etIfsc.error = "Invalid IFSC code format (5th letter must be 0)"
                binding.etIfsc.requestFocus()
            }
            binding.etHolderName.text.toString().trim().isEmpty() -> {
                binding.etHolderName.error = "Please enter holder name"
                binding.etHolderName.requestFocus()
            }
            binding.etBankName.text.toString().trim().isEmpty() -> {
                binding.etBankName.error = "Please enter bank name"
                binding.etBankName.requestFocus()
            }
            !binding.etBankName.text.toString().matches(textOnlyRegex) -> {
                Toast.makeText(requireContext(), "Bank name must contain only letters", Toast.LENGTH_SHORT).show()
                binding.etBankName.requestFocus()
            }
            binding.etBranch.text.toString().trim().isEmpty() -> {
                binding.etBranch.error = "Please enter branch"
                binding.etBranch.requestFocus()
            }
            !binding.etBranch.text.toString().matches(textOnlyRegex) -> {
                Toast.makeText(requireContext(), "Branch name must contain only letters", Toast.LENGTH_SHORT).show()
                binding.etBranch.requestFocus()
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
        params[Constant.ACCOUNT_NUM] = binding.etAccountNumber.text.toString().trim()
        params[Constant.HOLDER_NAME] = binding.etHolderName.text.toString().trim()
        params[Constant.BANK] = binding.etBankName.text.toString().trim()
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
//                        startActivity(Intent(requireActivity(), WithdrawalActivity::class.java))
//                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireActivity(), jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, requireActivity(), Constant.UPDATE_BANK_URL, params, true)

        Log.d("UPDATE_BANK_URL", "UPDATE_BANK_URL: " + Constant.UPDATE_BANK_URL)
        Log.d("UPDATE_BANK_URL", "UPDATE_BANK_URL params: $params")
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
                        binding.etAccountNumber.setText(jsonArray.getJSONObject(0).getString(Constant.ACCOUNT_NUM))
                        binding.etHolderName.setText(jsonArray.getJSONObject(0).getString(Constant.HOLDER_NAME))
                        binding.etBankName.setText(jsonArray.getJSONObject(0).getString(Constant.BANK))
                        binding.etBranch.setText(jsonArray.getJSONObject(0).getString(Constant.BRANCH))
                        binding.etIfsc.setText(jsonArray.getJSONObject(0).getString(Constant.IFSC))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, requireActivity(), Constant.BANK_DETAILS_URL, params, true)
        Log.d("BANK_DETAILS_URL", "BANK_DETAILS_URL: " + Constant.BANK_DETAILS_URL)
        Log.d("BANK_DETAILS_URL", "BANK_DETAILS_URL params: $params")
    }
}
