package com.app.smart_id_maker.ProfileFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.app.smart_id_maker.R
import com.app.smart_id_maker.activities.MainActivity
import com.app.smart_id_maker.databinding.FragmentMyProfileBinding
import com.app.smart_id_maker.helper.Session

class MyProfileFragment : Fragment() {


    lateinit var binding: FragmentMyProfileBinding
    lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        session = Session(requireContext())

        (activity as MainActivity).binding.bottomNavigation.visibility = View.GONE



        binding.ibBack.setOnClickListener(View.OnClickListener { v: View? ->
            requireActivity().onBackPressed()
        })


        return binding.root

    }


}