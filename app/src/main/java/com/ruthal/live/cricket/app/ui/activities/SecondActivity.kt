package com.ruthal.live.cricket.app.ui.activities

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.facebook.ads.AdSettings
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.admanager.AdManager
import com.ruthal.live.cricket.app.appinterfaces.AdManagerListener
import com.ruthal.live.cricket.app.appinterfaces.CppSuccessListener
import com.ruthal.live.cricket.app.appinterfaces.DialogListener
import com.ruthal.live.cricket.app.constants.ApplicationConstants
import com.ruthal.live.cricket.app.constants.ApplicationConstants.NODEAPI_BASE
import com.ruthal.live.cricket.app.constants.ApplicationConstants.appVersionCode
import com.ruthal.live.cricket.app.constants.ApplicationConstants.nativeAdProviderName
import com.ruthal.live.cricket.app.constants.UnchangedConstants
import com.ruthal.live.cricket.app.cppfileshandling.NativeClass
import com.ruthal.live.cricket.app.databinding.SecondScreenLayoutBinding
import com.ruthal.live.cricket.app.fixtures.viewmodels.MatchesViewModel
import com.ruthal.live.cricket.app.models.AppAd
import com.ruthal.live.cricket.app.models.ApplicationConfiguration
import com.ruthal.live.cricket.app.network.GeneralApiResponseListener
import com.ruthal.live.cricket.app.viewmodels.StreamingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SecondActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    CppSuccessListener, GeneralApiResponseListener, DialogListener, AdManagerListener {

    private var bindingSecond: SecondScreenLayoutBinding? = null
    private var navController: NavController? = null
    private var nativeClass: NativeClass? = null
    private val streamingViewModel by lazy {
        ViewModelProvider(this)[StreamingViewModel::class.java]
    }
    private val scoreViewModel by lazy {
        ViewModelProvider(this)[MatchesViewModel::class.java]
    }
    private var intentLink: String = ""
    private var backBoolean = false
    private var adManager: AdManager? = null
    private var adProviderName = "none"
    private var navigationTap = 0
    private var showNavigationAd = 2
    private var time = "0"
    private var adStatus = false
    private var booleanVpn: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSecond = DataBindingUtil.setContentView(this, R.layout.second_screen_layout)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        AdSettings.addTestDevice("83906755-d55a-4dc9-b08f-8bc6b4599ea8")
        nativeClass = NativeClass(this, this)
        adManager = AdManager(this, this, this)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (backBoolean) {
                    if (!isFinishing) {
                        showDialogue()
                    }
                } else {
                    bindingSecond?.navHostFragment?.findNavController()
                        ?.popBackStack()
                }
            }
        })

        setUpNavigationGraph()
    }

    override fun onResume() {
        super.onResume()
//        checkVpn()
    }

    private fun checkVpn() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                    val booleanVpnCheck = hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                    booleanVpn = booleanVpnCheck == true
                }
            } else {
                booleanVpn = false
            }
        }

        if (booleanVpn != null) {
            if (booleanVpn!!) {
                if (bindingSecond?.adblockLayout?.isVisible!!) {
                    /////////

                } else {
                    bindingSecond?.adblockLayout?.visibility = View.VISIBLE

                }
            } else {
                bindingSecond?.adblockLayout?.visibility = View.GONE

            }
        }

    }


    private fun showDialogue() {
        val dialog = Dialog(this)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(true)
        dialog?.setContentView(R.layout.custom_layout2)

        val textExit = dialog?.findViewById(R.id.yes) as Button
        val textRate2 = dialog.findViewById(R.id.no) as Button
        val textRate3 = dialog.findViewById(R.id.icon_clcik) as ImageView
        textExit.setOnClickListener {
            rateClicked()
        }

        textRate2.setOnClickListener {
            ApplicationConstants.app_update_dialog = false
            dialog.dismiss()
            finishAffinity()

        }

        textRate3.setOnClickListener {
            rateClicked()
        }

        if (!isFinishing) {
            dialog.show()
        }

    }

    private fun rateClicked() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$packageName")
                )
            )
        } catch (e: ActivityNotFoundException) {

            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            } catch (e: ActivityNotFoundException) {

            }

        }
    }

    private fun setUpNavigationGraph() {
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        bindingSecond?.bottomNav?.setupWithNavController(navController!!)
        navController!!.addOnDestinationChangedListener(this)


        bindingSecond?.bottomNav?.setOnItemSelectedListener { item ->
            try {
                navigationTap += 1

                if (navigationTap == showNavigationAd) {
                    //showInterAd

                    if (adProviderName.equals(UnchangedConstants.startApp, true)) {
                        adManager?.loadAdProvider(
                            adProviderName, UnchangedConstants.adTap,
                            null, null, null, null
                        )
                    }

                    if (adStatus) {
                        if (!adProviderName.equals("none", true)) {
                            adManager?.showAds(adProviderName)

                            navigationTap = 0
                            showNavigationAd += 1
                        }

                    } else {
                        navigationTap = 0
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (item.itemId != navController!!.currentDestination?.id) {
                NavigationUI.onNavDestinationSelected(item, navController!!)
            }

            false
        }

    }


    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.home -> {
                backBoolean = true
            }

            else -> {
                backBoolean = false
            }
        }
    }

    override fun onCppSuccess() {
        lifecycleScope.launch(Dispatchers.Main) {
            observeStreamingData()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scoreViewModel?.stopWebSocket()
    }

    private fun observeStreamingData() {
        streamingViewModel?.getLiveEvents()
        scoreViewModel?.getMatches()

        scoreViewModel.isSocketUrl.observe(this) {
            if (it == true) {
                scoreViewModel.runSocketCode()
            }
        }
        streamingViewModel.dataModelList.observe(this, Observer {
            if (!it.extra_2.isNullOrEmpty()) {
                nativeClass?.replaceChar = "goi"
                nativeClass?.performCalculation(it.extra_2!!)
            }
            if (!it.app_ads.isNullOrEmpty()) {
                checkForAds(it.app_ads!!)
            }
            if (!it.app_version.isNullOrEmpty()) {
                try {
                    val version = appVersionCode
                    try {
                        val parsedInt = it.app_version!!.toInt()
                        if (parsedInt > version) {
                            if (!ApplicationConstants.app_update_dialog) {
//                                showAppUpdateDialog(it.app_update_text, it.is_permanent_dialog)
                                ApplicationConstants.app_update_dialog = true
                            }
                        }
                    } catch (nfe: java.lang.NumberFormatException) {


                    }
                } catch (e: PackageManager.NameNotFoundException) {

                }
            }
            if (!it.application_configurations.isNullOrEmpty()) {
                showSplashMethod(it.application_configurations)
            }

            if (!it.app_version.isNullOrEmpty()) {
                try {
                    val version = appVersionCode
                    try {
                        val parsedInt = it.app_version!!.toInt()
                        if (parsedInt > version) {
                            if (!ApplicationConstants.app_update_dialog) {
                                showAppUpdateDialog(it.app_update_text, it.is_permanent_dialog)
                                ApplicationConstants.app_update_dialog = true
                            }
                        }
                    } catch (nfe: java.lang.NumberFormatException) {


                    }
                } catch (e: PackageManager.NameNotFoundException) {

                }
            }


        })
    }
    private fun showAppUpdateDialog(appUpdateText: String?, permanent: Boolean?) {
        val dialog = Dialog(this)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.app_update_layout)
        val textExit = dialog?.findViewById(R.id.no_thanks) as Button
        val textRate2 = dialog.findViewById(R.id.update) as Button
        val textUpdate = dialog.findViewById(R.id.app_update_txt) as TextView

        if (permanent == true) {
            dialog.setCancelable(false)
            textExit.text = resources.getString(R.string.Exit)
        } else {
            textExit.text = resources.getString(R.string.noThanks)
            dialog.setCancelable(true)
        }

        if (appUpdateText != null) {
            textUpdate.text = appUpdateText
        }

        textExit.setOnClickListener {
            if (permanent == true) {
                ApplicationConstants.app_update_dialog = false
                dialog.dismiss()
                finishAffinity()
            } else {

                dialog.dismiss()

            }
        }

        textRate2.setOnClickListener {
            rateClicked()
        }


        dialog.show()
    }

    private fun checkForAds(appAds: List<AppAd>) {
        if (!appAds.isNullOrEmpty()) {
            nativeAdProviderName =
                adManager?.checkProvider(appAds, UnchangedConstants.nativeAdLocation)
                    .toString()
            ApplicationConstants.adLocation1Provider =
                adManager?.checkProvider(appAds, UnchangedConstants.adLocation1)
                    .toString()
            if (nativeAdProviderName.equals(UnchangedConstants.facebook, true)) {

                if (nativeAdProviderName != null) {
                    adManager?.loadAdProvider(
                        nativeAdProviderName, UnchangedConstants.nativeAdLocation, null,
                        null, null, null
                    )
                }
            }

            adProviderName =
                adManager?.checkProvider(appAds, UnchangedConstants.adTap).toString()
            if (adProviderName != "none") {
                //interstitial Ad loading
                loadTapAd()
            }
        }
    }
    private fun loadTapAd() {
        //interstitial Ad loading
        if (!adProviderName.equals(UnchangedConstants.startApp, true)) {
            adManager?.loadAdProvider(
                adProviderName, UnchangedConstants.adTap,
                null, null, null, null
            )
        }

    }

    private fun showSplashMethod(applicationConfigurations: List<ApplicationConfiguration>?) {
        var splashScreenStatus = false
        if (applicationConfigurations != null) {
            ////if configuration array is not empty
            if (applicationConfigurations.isNotEmpty()) {
                val refresh = Handler(Looper.getMainLooper())
                refresh.post {
                    run {
                        for (configuration in applicationConfigurations) {

                            ///For setting time
                            if (configuration.key?.equals("Time", true)!!) {
                                if (configuration.value != null) {
                                    time = configuration.value!!
                                }

                                if (splashScreenStatus) {
                                    if (!ApplicationConstants.splash_status) {
                                        ApplicationConstants.splash_status = true
                                        try {
                                            var timer: Int = time.toInt()
                                            timer *= 1000
                                            bindingSecond?.splashLayout?.visibility = View.VISIBLE
                                            bindingSecond?.splashButton?.setOnClickListener {
                                                try {
                                                    val uri =
                                                        Uri.parse(intentLink)
                                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                                    startActivity(intent)
                                                } catch (e: Exception) {
                                                    Log.d("Exception", "" + e.message)

                                                }
                                            }
                                            Handler(Looper.getMainLooper()).postDelayed({
                                                bindingSecond?.splashLayout?.visibility = View.GONE
                                            }, timer.toLong())
                                        } catch (e: NumberFormatException) {

                                            Log.d("Exception", "" + e.message)

                                        }

                                    }
                                }


                            }

                            ///For setting button text
                            if (configuration.key.equals("ButtonText", true)) {
                                if (configuration.value != null) {
                                    bindingSecond?.splashButton?.text = configuration.value
                                }
                            }

                            if (configuration.key.equals("baseURL", true)) {
                                if (configuration.value != null) {
                                    NODEAPI_BASE= configuration.value.toString()
                                }
                            }

                            ///For setting heading
                            if (configuration.key.equals("Heading", true)) {
                                if (configuration.value != null) {
                                    bindingSecond?.splashHeading?.text = configuration.value
                                }

                            }

                            ///For setting link
                            if (configuration.key.equals("ButtonLink", true)) {
                                if (configuration.value != null) {
                                    intentLink = configuration.value!!
                                }

                            }

                            ///For setting body
                            if (configuration.key.equals("DetailText", true)) {
                                if (configuration.value != null) {
                                    bindingSecond?.splashBody?.text = configuration.value
                                }

                            }

                            ///For setting show button
                            if (configuration.key.equals("ShowButton", true)) {
                                if (configuration.value != null) {
                                    if (configuration.value.equals("True", true)) {
                                        bindingSecond?.splashButton?.visibility = View.VISIBLE
                                    } else {
                                        bindingSecond?.splashButton?.visibility = View.GONE
                                    }

                                }

                            }

                            ///For checking splash is on and off
                            if (configuration.key.equals("ShowSplash", true)) {
                                if (configuration.value.equals("true", true)) {
                                    if (!splashScreenStatus) {
                                        splashScreenStatus = true
                                    }

                                } else {
                                    splashScreenStatus = false
                                    bindingSecond?.splashLayout?.visibility = View.GONE
                                }

                            }


                        }////loop to iterate through configuration array
                    }
                }


            }


        }


    }

    override fun onPositiveDialogText(key: String) {

    }

    override fun onNegativeDialogText(key: String) {

    }

    override fun onStarted() {

    }

    override fun onSuccess() {

    }

    override fun onFailure(message: String) {

    }

    override fun onAdLoad(value: String) {
        adStatus = value.equals("success", true)

    }

    override fun onAdFinish() {
        adStatus = false

        if (adProviderName != "none") {
            loadTapAd()
        }
    }

}