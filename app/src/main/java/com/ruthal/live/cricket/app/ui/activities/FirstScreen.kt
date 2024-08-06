package com.ruthal.live.cricket.app.ui.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.appinterfaces.DialogListener
import com.ruthal.live.cricket.app.databinding.FirstScreenLayoutBinding
import com.ruthal.live.cricket.app.utils.CodeUtils.checkDeviceRootedOrNot
import com.ruthal.live.cricket.app.utils.CodeUtils.checkInternetIsAvailable
import com.ruthal.live.cricket.app.utils.CodeUtils.checkRunningDeviceIsReal
import com.ruthal.live.cricket.app.utils.CodeUtils.navigateToNextScreen
import com.ruthal.live.cricket.app.utils.CodeUtils.visibility
import com.ruthal.live.cricket.app.utils.CustomDialogue

class FirstScreen : AppCompatActivity(), DialogListener {

    private var bindingFirst: FirstScreenLayoutBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingFirst = DataBindingUtil.setContentView(this, R.layout.first_screen_layout)

        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        bindingFirst?.progressDialogSplash?.visibility(true)

        bindingFirst?.retry?.setOnClickListener {
            checkNecessities()
        }

    }

    override fun onResume() {
        super.onResume()
        checkNecessities()
    }

    private fun checkNecessities() {
        if (checkInternetIsAvailable(this)) {
            bindingFirst?.noInternetLay?.visibility(false)
            Handler(Looper.getMainLooper()).postDelayed({
                checkRunningDevice()
            }, 2000)
        } else {
            bindingFirst?.noInternetLay?.visibility(true)
            bindingFirst?.progressDialogSplash?.visibility(false)
        }
    }

    private fun checkRunningDevice() {
        if (!checkRunningDeviceIsReal()) {
            if (!checkDeviceRootedOrNot(this)) {
                navigateToMainUiScreen()
            }
        } else {
            CustomDialogue(this).showDialog(
                this, "Alert!", "Please use application on real device",
                "", "Ok", "baseValue"
            )
        }
    }

    private fun navigateToMainUiScreen() {
        bindingFirst?.progressDialogSplash?.visibility(false)
        navigateToNextScreen(this, SecondActivity::class.java)
    }

    override fun onPositiveDialogText(key: String) {
        finishAffinity()
    }

    override fun onNegativeDialogText(key: String) {
        finishAffinity()
    }


}