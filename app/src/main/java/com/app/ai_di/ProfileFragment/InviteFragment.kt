package com.app.ai_di.ProfileFragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.ai_di.activities.MainActivity
import com.app.ai_di.databinding.FragmentInviteBinding
import com.app.ai_di.helper.Constant
import com.app.ai_di.helper.Session

class InviteFragment : Fragment() {

    lateinit var binding: FragmentInviteBinding
    lateinit var session: Session

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInviteBinding.inflate(inflater, container, false)
        session = Session(requireContext())

        (activity as MainActivity).binding.bottomNavigation.visibility = View.GONE

        binding.ibBack.setOnClickListener(View.OnClickListener { v: View? ->
            requireActivity().onBackPressed()
        })


        // Set click listener for the "Refer" button
        if (session != null && session.getData(Constant.REFER_CODE) != null) {
            binding.btnRefer.setOnClickListener(View.OnClickListener { v: View? ->
                val baseUrl = "https://aidiapp.in/" // Replace with your actual base URL
                shareTextAndUrl(
                    """Click this link to join Ai-Di App ☺️
Use My Refer Code ${session.getData(Constant.REFER_CODE)} While Creating Account.""", baseUrl
                )
            })
        } else {
            val baseUrl = "https://aidiapp.in/" // Replace with your actual base URL
            shareTextAndUrl(
                """
            Click this link to join Ai-Di App ☺️
            Use My Refer Code ID123 While Creating Account.
            """.trimIndent(), baseUrl
            )
        }

        var referCode = session.getData(Constant.REFER_CODE)

        // Check if session and data are not null, then set the text
        if (session.getData(Constant.REFER_CODE) != null) {
            binding.btnReferText.text = session.getData(Constant.REFER_CODE)
        } else {
            binding.btnReferText.text = "123456"
        }

        binding.btnReferText.setOnClickListener(View.OnClickListener { v: View? ->
            if (session.getData(Constant.REFER_CODE) == null || session.getData(Constant.REFER_CODE).isEmpty()) {
                referCode = "123456" // Default refer code
            }
            val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Refer Code", referCode)
            clipboard.setPrimaryClip(clip)
        })

        return binding.root

    }

    // Function to generate referral URL
    private fun generateReferralUrl(baseUrl: String, referralCode: String): String {
        return "$baseUrl?ref=$referralCode"
    }

    // Function to share text and URL
    private fun shareTextAndUrl(message: String, url: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        shareIntent.putExtra(
            Intent.EXTRA_TEXT, """
     $message
     $url
     """.trimIndent()
        )
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

}