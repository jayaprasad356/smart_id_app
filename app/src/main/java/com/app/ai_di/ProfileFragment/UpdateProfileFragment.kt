package com.app.ai_di.ProfileFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.app.ai_di.activities.MainActivity
import com.app.ai_di.databinding.FragmentUpdateProfileBinding
import com.app.ai_di.helper.ApiConfig
import com.app.ai_di.helper.Constant
import com.app.ai_di.helper.Session
import org.json.JSONException
import org.json.JSONObject

class UpdateProfileFragment : Fragment() {

    lateinit var binding: FragmentUpdateProfileBinding
    lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        session = Session(requireContext())

        (activity as MainActivity).binding.bottomNavigation.visibility = View.GONE

        profileDetails()

        binding.etPhoneNumber.isEnabled = false

        binding.ibBack.setOnClickListener(View.OnClickListener { v: View? ->
            requireActivity().onBackPressed()
        })

        binding.btnUpdateProfile.setOnClickListener(View.OnClickListener { v: View? ->
            validateAndSubmitProfileDetails()
        })

        return binding.root

    }

    private fun validateAndSubmitProfileDetails() {
        // Validate user input
        when {
            binding.etName.text.toString().trim().isEmpty() -> {
                binding.etName.error = "Please enter Name"
                binding.etName.requestFocus()
            }
            binding.etEmail.text.toString().trim().isEmpty() -> {
                binding.etEmail.error = "Please enter email"
                binding.etEmail.requestFocus()
            }
            !binding.etEmail.text.toString().trim().matches(Constant.EMAIL_PATTERN.toRegex()) -> {
                binding.etEmail.error = "Please enter a valid email"
                binding.etEmail.requestFocus()
            }
            binding.etCity.text.toString().trim().isEmpty() -> {
                binding.etCity.error = "Please enter city"
                binding.etCity.requestFocus()
            }
            binding.etDateOfBirth.text.toString().trim().isEmpty() -> {
                binding.etDateOfBirth.error = "Please enter date of birth"
                binding.etDateOfBirth.requestFocus()
            }
            else -> {
                // Call update bank API if all fields are valid
                updateProfile()
            }
        }
    }

    private fun updateProfile() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.NAME] = binding.etName.text.toString().trim()
        params[Constant.MOBILE] = binding.etPhoneNumber.text.toString().trim()
        params[Constant.EMAIL] = binding.etEmail.text.toString().trim()
        params[Constant.CITY] = binding.etCity.text.toString().trim()
        params[Constant.DOB] = binding.etDateOfBirth.text.toString().trim()

        // Make the API call to update bank details
        ApiConfig.RequestToVolley({ result, response ->
            if (result) {
                try {
                    val jsonObject = JSONObject(response ?: "")
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        Toast.makeText(
                            requireActivity(),
                            jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()

                        val profileArray = jsonObject.getJSONArray(Constant.DATA)
                        session.setData(
                            Constant.NAME, profileArray.getJSONObject(0).getString(
                                Constant.NAME))
                        session.setData(
                            Constant.MOBILE, profileArray.getJSONObject(0).getString(
                                Constant.MOBILE))
                        session.setData(Constant.EMAIL, profileArray.getJSONObject(0).getString(Constant.EMAIL))
                        session.setData(
                            Constant.CITY, profileArray.getJSONObject(0).getString(
                                Constant.CITY))
                        session.setData(Constant.DOB, profileArray.getJSONObject(0).getString(Constant.DOB))

                        profileDetails()

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
        }, requireActivity(), Constant.UPDATE_PROFILE, params, true)
    }

    private fun profileDetails() {

        binding.etName.setText(session.getData(Constant.NAME))
        binding.etPhoneNumber.setText(session.getData(Constant.MOBILE))
        binding.etEmail.setText(session.getData(Constant.EMAIL))
        binding.etCity.setText(session.getData(Constant.CITY))
        binding.etDateOfBirth.setText(session.getData(Constant.DOB))

    }
}