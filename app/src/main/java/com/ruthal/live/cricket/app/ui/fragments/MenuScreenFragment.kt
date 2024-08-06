package com.ruthal.live.cricket.app.ui.fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ruthal.live.cricket.app.BuildConfig
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.databinding.MenuScreenLayoutBinding

class MenuScreenFragment:Fragment() {
    var binding:MenuScreenLayoutBinding?=null
    var count=0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.menu_screen_layout,container,false)
        binding=DataBindingUtil.bind(layout)

        binding?.notificationMenuEnd?.setOnClickListener {
            if (count==0){
                binding?.notificationMenuEnd?.setImageResource(R.drawable.checked_box)
                count++
            }
            else
            {
                binding?.notificationMenuEnd?.setImageResource(R.drawable.unchecked_box)
                count=0
            }
        }


        binding?.menuIcon?.setOnClickListener {
            findNavController().popBackStack()
        }

        //Email click listener
        binding?.emailLay?.setOnClickListener {
          contactUs()
        }

        //Share click listener..
        binding?.shareLay?.setOnClickListener {
           shareUsFunction()
        }

        //
        binding?.rateUsLay?.setOnClickListener {
           rateClicked()
        }

        binding?.versionText?.text= "Release: "+BuildConfig.VERSION_NAME

        binding?.termsLay?.setOnClickListener {
          showPrivacyPolicyPage()
        }
        return layout
    }

    private fun showPrivacyPolicyPage() {
        try {
            val url = "https://www.freeprivacypolicy.com/live/b6751fa2-b7c3-4b03-ad75-eacfea5e62a9"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        } catch (e: ActivityNotFoundException) {

        }
    }

    private fun shareUsFunction() {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(
                Intent.EXTRA_TEXT, "Please download this app for live  streaming.\n" +
                        "https://play.google.com/store/apps/details?id=" + requireContext().packageName
            )
            intent.type = "text/plain"
            startActivity(intent)
        } catch (e: Exception) {
            Log.d("Exception","msg"+e.message)
        }
    }

    private fun contactUs() {
        try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:") // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, Array(1) { "" })
            intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
            startActivity(Intent.createChooser(intent, "Send Email..."))
        }
        catch (e:Exception){
            Log.d("Exception","msg"+e.message)
        }

    }

    private fun rateClicked() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${requireContext().packageName}")
                )
            )
        } catch (e: ActivityNotFoundException) {

            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=${requireContext().packageName}")
                    )
                )
            } catch (e: ActivityNotFoundException) {

            }

        }
    }

}