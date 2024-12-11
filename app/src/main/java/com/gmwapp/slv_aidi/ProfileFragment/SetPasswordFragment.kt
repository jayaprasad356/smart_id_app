package com.gmwapp.slv_aidi.ProfileFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gmwapp.slv_aidi.activities.MainActivity
import com.gmwapp.slv_aidi.databinding.FragmentSetPasswordBinding
import com.gmwapp.slv_aidi.helper.ApiConfig
import com.gmwapp.slv_aidi.helper.Constant
import com.gmwapp.slv_aidi.helper.Session
import org.json.JSONException
import org.json.JSONObject

class SetPasswordFragment : Fragment() {


    lateinit var binding: FragmentSetPasswordBinding
    lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSetPasswordBinding.inflate(inflater, container, false)
        session = Session(requireContext())

        (activity as MainActivity).binding.bottomNavigation.visibility = View.GONE

        binding.ibBack.setOnClickListener(View.OnClickListener { v: View? ->
            requireActivity().onBackPressed()
        })

        binding.btnUpdatePassword.setOnClickListener(View.OnClickListener { v: View? ->
            updatePassword()
        })

        return binding.root

    }

    private fun updatePassword() {
        val params: MutableMap<String, String> = HashMap()
        params[Constant.USER_ID] = session.getData(Constant.USER_ID)
        params[Constant.PASSWORD] = binding.etPassword.text.toString().trim()
        params[Constant.CONFIRM_PASSWORD] = binding.etConfirmPassword.text.toString().trim()

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
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            jsonObject.getString(Constant.MESSAGE),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, requireActivity(), Constant.CHANGE_PASSWORD, params, true)
    }

}