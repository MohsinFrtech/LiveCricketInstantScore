package com.ruthal.live.cricket.app.admanager

import android.app.Activity
import android.content.Context
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.facebook.ads.*
import com.facebook.ads.AdError
import com.facebook.ads.AdSize
import com.google.android.gms.ads.*
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.appinterfaces.AdManagerListener
import com.ruthal.live.cricket.app.constants.ApplicationConstants
import com.ruthal.live.cricket.app.constants.UnchangedConstants
import com.ruthal.live.cricket.app.databinding.LayoutFbNativeAdBinding
import com.ruthal.live.cricket.app.models.AppAd
import com.startapp.sdk.ads.banner.Banner
import com.startapp.sdk.ads.banner.BannerListener
import com.startapp.sdk.adsbase.StartAppAd
import com.startapp.sdk.adsbase.StartAppSDK
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener
import com.startapp.sdk.adsbase.adlisteners.AdEventListener
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAdsShowOptions
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize

class AdManager(
    private val context: Context,
    private val activity: Activity,
    val adManagerListener: AdManagerListener
) {

    private var adView: NativeAdView? = null
    private var nativeAdLayout: NativeAdLayout? = null
    private var fbNativeAd: NativeAd? = null
    private var facebookbinding: LayoutFbNativeAdBinding? = null
    private val taG = "AdManagerClass"
    private var bottomBanner: BannerView? = null
    private var bottomAdUnitId = "Banner_Android"
    private var topBannerUnity: BannerView? = null
    private var bannerAdValue = ""
    private var interstitialAdValue = ""
    private var nativeAdValue = ""
    private var startAppAd: StartAppAd? = null
    private var mInterstitialAd: InterstitialAd? = null
    private var facebookinterstitial: com.facebook.ads.InterstitialAd? = null
    private var adProvider = ""

    ///function will return provider
    fun checkProvider(list: List<AppAd>, location: String): String {
        adProvider = "none"
        for (listItem in list) {
            if (listItem.enable == true) {
                if (!listItem.ad_locations.isNullOrEmpty()) {
                    for (adLocation in listItem.ad_locations!!) {
                        if (adLocation.title.equals(location, true)) {
                            if (listItem.ad_provider.equals(UnchangedConstants.admob, true)) {
                                adProvider = UnchangedConstants.admob
                                checkAdValue(adLocation.title, listItem.ad_key, adProvider)
                            } else if (listItem.ad_provider.equals(
                                    UnchangedConstants.facebook,
                                    true
                                )
                            ) {
                                adProvider = UnchangedConstants.facebook
                                checkAdValue(adLocation.title, listItem.ad_key, adProvider)
                            } else if (listItem.ad_provider.equals(
                                    UnchangedConstants.unity,
                                    true
                                )
                            ) {
                                adProvider = UnchangedConstants.unity
                                checkAdValue(adLocation.title, listItem.ad_key, adProvider)
                            } else if (listItem.ad_provider.equals(
                                    UnchangedConstants.chartBoost,
                                    true
                                )
                            ) {
                                adProvider = UnchangedConstants.chartBoost
                                checkAdValue(adLocation.title, listItem.ad_key, adProvider)
                            } else if (listItem.ad_provider.equals(
                                    UnchangedConstants.startApp,
                                    true
                                )
                            ) {
                                adProvider = UnchangedConstants.startApp
                                checkAdValue(adLocation.title, listItem.ad_key, adProvider)
                            }

                        }


                    }

                }


            }

        }

        ////If provider exist then initialize sdk of the particular provider
        return adProvider
    }

    private fun checkAdValue(adLocation: String?, adKey: String?, provider: String) {
        if (adLocation.equals(UnchangedConstants.adMiddle, true) || adLocation.equals(
                UnchangedConstants.adBefore,
                true
            )
            || adLocation.equals(UnchangedConstants.adAfter, true)
        ) {
            interstitialAdValue = adKey.toString()
            if (provider.equals(UnchangedConstants.chartBoost, true)) {
                if (interstitialAdValue.contains(UnchangedConstants.mySecretCheckDel)) {
                    val yourArray: List<String> =
                        interstitialAdValue.split(UnchangedConstants.mySecretCheckDel)
                    ApplicationConstants.chartBoostAppID = yourArray[0].trim()
                    ApplicationConstants.chartBoostAppSig = yourArray[1].trim()
                }
            } else if (provider.equals(UnchangedConstants.admob, true)) {
                ApplicationConstants.admobInterstitial = interstitialAdValue
            } else if (provider.equals(UnchangedConstants.facebook, true)) {
                ApplicationConstants.facebookPlacementIdInterstitial = interstitialAdValue
            } else if (provider.equals(UnchangedConstants.startApp, true)) {
                ApplicationConstants.startAppId = interstitialAdValue
            } else if (provider.equals(UnchangedConstants.unity, true)) {
                ApplicationConstants.unityGameID = interstitialAdValue
            }

        } else if (adLocation.equals(UnchangedConstants.nativeAdLocation, true)) {
            nativeAdValue = adKey.toString()

            if (provider.equals(UnchangedConstants.admob, true)) {
                ApplicationConstants.nativeAdmob = nativeAdValue
            } else if (provider.equals(UnchangedConstants.facebook, true)) {
                ApplicationConstants.nativeFacebook = nativeAdValue
            }
        } else if (adLocation.equals(UnchangedConstants.adLocation1, true)
            || adLocation.equals(UnchangedConstants.adLocation2top, true)
            || adLocation.equals(UnchangedConstants.adLocation2bottom, true)
            || adLocation.equals(ApplicationConstants.adLocation2topPermanent, true)
        ) {
            bannerAdValue = adKey.toString()
            if (provider.equals(UnchangedConstants.admob, true)) {
                ApplicationConstants.admobBannerId = bannerAdValue
            } else if (provider.equals(UnchangedConstants.facebook, true)) {
                ApplicationConstants.fbPlacementIdBanner = bannerAdValue
            } else if (provider.equals(UnchangedConstants.startApp, true)) {
                ApplicationConstants.startAppId = bannerAdValue
            } else if (provider.equals(UnchangedConstants.unity, true)) {
                ApplicationConstants.unityGameID = bannerAdValue
            }

        }
    }

    fun loadAdProvider(
        provider: String,
        adLocation: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        startAppBanner: Banner?
    ) {

        if (provider.equals(UnchangedConstants.admob, true)) {

        } else if (provider.equals(UnchangedConstants.facebook, true)) {
            facebookSdkInitialization(
                adLocation,
                adView,
                linearLayout,
                relativeLayout,
                startAppBanner
            )
        } else if (provider.equals(UnchangedConstants.unity, true)) {
            //unity sdk initialization...
            unitySdkInitialization(adLocation, adView, linearLayout, relativeLayout, startAppBanner)
        } else if (provider.equals(UnchangedConstants.chartBoost, true)) {

        } else if (provider.equals(UnchangedConstants.startApp, true)) {

            startAppSdkInitialization(
                adLocation,
                adView,
                linearLayout,
                relativeLayout,
                startAppBanner
            )
        }
    }

    private fun unitySdkInitialization(
        adLocation: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?
    ) {

        if (ApplicationConstants.isUnitySdkInit) {
            loadAdAtParticularLocation(
                adLocation,
                UnchangedConstants.unity,
                adView,
                linearLayout,
                relativeLayout,
                banner
            )
        } else {
            UnityAds.initialize(
                context,
                ApplicationConstants.unityGameID,
                UnchangedConstants.unityTestMode,
                object : IUnityAdsInitializationListener {
                    override fun onInitializationComplete() {
                        if (UnityAds.isInitialized()) {

                            ApplicationConstants.isUnitySdkInit = true
                            loadAdAtParticularLocation(
                                adLocation,
                                UnchangedConstants.unity,
                                adView,
                                linearLayout,
                                relativeLayout,
                                banner
                            )

                        }

                    }

                    override fun onInitializationFailed(
                        p0: UnityAds.UnityAdsInitializationError?,
                        p1: String?
                    ) {
                        ApplicationConstants.isUnitySdkInit = false
                    }
                })

        }
    }


    fun showAds(adProviderShow: String) {
        if (adProviderShow.equals(UnchangedConstants.admob, true)) {

        } else if (adProviderShow.equals(UnchangedConstants.unity, true)) {
            showUnityAd()
        } else if (adProviderShow.equals(UnchangedConstants.chartBoost, true)) {

        } else if (adProviderShow.equals(UnchangedConstants.facebook, true)) {
            showFacebookAdInterstitial()
        } else if (adProviderShow.equals(UnchangedConstants.startApp, true)) {
//                showStartAppAd()
        }

    }

    private fun showUnityAd() {
        val showListener: IUnityAdsShowListener = object : IUnityAdsShowListener {
            override fun onUnityAdsShowFailure(
                placementId: String,
                error: UnityAds.UnityAdsShowError,
                message: String
            ) {
                adManagerListener.onAdFinish()

            }

            override fun onUnityAdsShowStart(placementId: String) {

            }

            override fun onUnityAdsShowClick(placementId: String) {

            }

            override fun onUnityAdsShowComplete(
                placementId: String,
                state: UnityAds.UnityAdsShowCompletionState
            ) {
                adManagerListener.onAdFinish()

            }
        }
        UnityAds.show(
            activity,
            "Interstitial_Android",
            UnityAdsShowOptions(),
            showListener
        )

    }



    private fun loadAdAtParticularLocation(
        locationName: String,
        adProviderName: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?
    ) {


        if (locationName.equals(UnchangedConstants.adLocation1, true) ||
            locationName.equals(UnchangedConstants.adLocation2top, true) ||
            locationName.equals(UnchangedConstants.adLocation2bottom, true) ||
            locationName.equals(ApplicationConstants.adLocation2topPermanent, true)
        ) {

            if (adProviderName.equals(UnchangedConstants.admob, true)) {

                //remaining....
            } else if (adProviderName.equals(UnchangedConstants.facebook, true)) {
                loadFaceBookBannerAd(context, linearLayout)
            } else if (adProviderName.equals(UnchangedConstants.startApp, true)) {
                setStartAppBanner(banner)
            } else if (adProviderName.equals(UnchangedConstants.unity, true)) {
                if (locationName.equals(UnchangedConstants.adLocation1, true)) {
                    setUpUnityBanner(relativeLayout)
                }
                if (locationName.equals(UnchangedConstants.adLocation2top, true)) {
                    setUpUnityBanner(relativeLayout)
                }

                if (locationName.equals(ApplicationConstants.adLocation2topPermanent, true)) {
                    setUpUnityBanner(relativeLayout)
                }

                if (locationName.equals(UnchangedConstants.adLocation2bottom, true)) {
                    setUpUnityBannerBottom(relativeLayout)
                }
            }


        } else if (locationName.equals(UnchangedConstants.nativeAdLocation)) {
            if (adProviderName.equals(UnchangedConstants.facebook, true)) {
                loadFacebookNativeAdWithoutPopulate()
            }
        } else {

            if (adProviderName.equals(UnchangedConstants.admob, true)) {

            } else if (adProviderName.equals(UnchangedConstants.unity, true)) {
                loadUnityAdInterstitial()
            } else if (adProviderName.equals(UnchangedConstants.chartBoost, true)) {
            } else if (adProviderName.equals(UnchangedConstants.facebook, true)) {
                loadFacebookInterstitialAd()
            } else if (adProviderName.equals(UnchangedConstants.startApp, true)) {
                loadStartAppAd()
            }

        }

    }

    fun loadFacebookNativeAdWithoutPopulate() {
        fbNativeAd = NativeAd(context, ApplicationConstants.nativeFacebook)
        val nativeAdListener: NativeAdListener = object : NativeAdListener {
            override fun onMediaDownloaded(ad: Ad) {

                // Native ad finished downloading all assets
            }

            override fun onError(ad: Ad?, adError: AdError) {


                // Native ad failed to load
            }

            override fun onAdLoaded(ad: Ad) {
                // Native ad is loaded and ready to be displayed
                if (fbNativeAd != null) {
                    ApplicationConstants.currentNativeAdFacebook = fbNativeAd
//                    inflateFbNativeAd(fbNativeAd!!, nativeAdLayout)

                }
            }

            override fun onAdClicked(ad: Ad) {
                // Native ad clicked
            }

            override fun onLoggingImpression(ad: Ad) {
                // Native ad impression
            }
        }

        // Request an ad
        fbNativeAd?.buildLoadAdConfig()
            ?.withAdListener(nativeAdListener)
            ?.build().let {
                it?.let { it1 ->
                    fbNativeAd?.loadAd(
                        it1
                    )
                }
            }
    }
    private fun loadUnityAdInterstitial() {
        val loadListener: IUnityAdsLoadListener = object : IUnityAdsLoadListener {
            override fun onUnityAdsAdLoaded(placementId: String) {
                adManagerListener.onAdLoad("success")

            }

            override fun onUnityAdsFailedToLoad(
                placementId: String,
                error: UnityAds.UnityAdsLoadError,
                message: String
            ) {

                adManagerListener.onAdLoad("failed")


            }
        }


        UnityAds.load("Interstitial_Android", loadListener)
    }

    private fun setUpUnityBannerBottom(relativeLayout: RelativeLayout?) {

        relativeLayout?.removeAllViews()
        bottomBanner = BannerView(activity, bottomAdUnitId, UnityBannerSize(320, 50))
        val bannerListener: BannerView.IListener = object : BannerView.IListener {
            override fun onBannerLoaded(bannerAdView: BannerView) {
                // Called when the banner is loaded.
//                logger.printLog(taG, "unityLoaded")

                if (bottomBanner != null) {
                    showBanner(relativeLayout, bottomBanner!!)
                }

            }

            override fun onBannerFailedToLoad(
                bannerAdView: BannerView,
                errorInfo: BannerErrorInfo
            ) {
//                logger.printLog(taG, "unityFailed" + "  " + errorInfo.errorMessage)
            }

            override fun onBannerClick(bannerAdView: BannerView) {

            }

            override fun onBannerLeftApplication(bannerAdView: BannerView) {
            }
        }
        bottomBanner?.listener = bannerListener
        bottomBanner?.load()
    }


    private fun setUpUnityBanner(relativeLayout: RelativeLayout?) {
        // Listener for banner events:

        relativeLayout?.removeAllViews()

        topBannerUnity = BannerView(activity, bottomAdUnitId, UnityBannerSize(320, 50))
        val bannerListener: BannerView.IListener = object : BannerView.IListener {
            override fun onBannerLoaded(bannerAdView: BannerView) {
                // Called when the banner is loaded.
//                logger.printLog(taG, "unityLoaded")

                if (topBannerUnity != null) {
                    showBanner(relativeLayout, topBannerUnity!!)
                }

            }

            override fun onBannerFailedToLoad(
                bannerAdView: BannerView,
                errorInfo: BannerErrorInfo
            ) {
//                logger.printLog(taG, "unityFailed" + "  " + errorInfo.errorMessage)
            }

            override fun onBannerClick(bannerAdView: BannerView) {

            }

            override fun onBannerLeftApplication(bannerAdView: BannerView) {
            }
        }
        topBannerUnity?.listener = bannerListener
        topBannerUnity?.load()


    }

    private fun showBanner(s: RelativeLayout?, insideBanner: BannerView) {
//        s?.removeAllViews()
        s?.addView(insideBanner)
    }


    private fun loadStartAppAd() {
        startAppAd = StartAppAd(context)
        startAppAd?.loadAd(object : AdEventListener {

            override fun onReceiveAd(p0: com.startapp.sdk.adsbase.Ad) {

                showStartAppAd()

            }

            override fun onFailedToReceiveAd(p0: com.startapp.sdk.adsbase.Ad?) {

            }
        })
    }


    private fun showStartAppAd() {
        startAppAd?.showAd(object : AdDisplayListener {
            override fun adHidden(ad: com.startapp.sdk.adsbase.Ad) {


            }

            override fun adDisplayed(ad: com.startapp.sdk.adsbase.Ad) {


            }

            override fun adClicked(ad: com.startapp.sdk.adsbase.Ad) {


            }

            override fun adNotDisplayed(ad: com.startapp.sdk.adsbase.Ad) {


            }
        })
    }


    private fun facebookSdkInitialization(
        adLocation: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?
    ) {
        if (ApplicationConstants.isInitFacebookSdk) {
            loadAdAtParticularLocation(
                adLocation,
                UnchangedConstants.facebook,
                adView,
                linearLayout,
                relativeLayout,
                banner
            )
        } else {
            AudienceNetworkAds
                .buildInitSettings(context)
                .withInitListener {
                    if (it.isSuccess) {
                        ApplicationConstants.isInitFacebookSdk = true
                        loadAdAtParticularLocation(
                            adLocation,
                            UnchangedConstants.facebook,
                            adView,
                            linearLayout,
                            relativeLayout,
                            banner
                        )
                    } else {

                        ApplicationConstants.isInitFacebookSdk = false
                    }

                }
                .initialize()
        }

    }

    ////startapp sdk initialization....
    private fun startAppSdkInitialization(
        adLocation: String,
        adView: LinearLayout?,
        linearLayout: LinearLayout?,
        relativeLayout: RelativeLayout?,
        banner: Banner?
    ) {

        if (ApplicationConstants.isStartAppSdkInit) {
            loadAdAtParticularLocation(
                adLocation,
                UnchangedConstants.startApp,
                adView,
                linearLayout,
                relativeLayout,
                banner
            )
        } else {
            try {
                StartAppSDK.init(context, ApplicationConstants.startAppId, false)
                StartAppAd.disableSplash()
                ApplicationConstants.isStartAppSdkInit = true
                loadAdAtParticularLocation(
                    adLocation,
                    UnchangedConstants.startApp,
                    adView,
                    linearLayout,
                    relativeLayout,
                    banner
                )
            } catch (e: Exception) {


            }
        }
    }


    private fun setStartAppBanner(bannerView: Banner?) {
        val banner = Banner(activity, object : BannerListener {
            override fun onReceiveAd(p0: View?) {

                bannerView?.visibility = View.VISIBLE
                bannerView?.showBanner()
            }

            override fun onFailedToReceiveAd(p0: View?) {
                bannerView?.visibility = View.GONE

            }

            override fun onImpression(p0: View?) {

            }

            override fun onClick(p0: View?) {

            }


        })
        banner.loadAd()
    }


    ///LoadFacebook banner ad.....
    private fun loadFaceBookBannerAd(context: Context?, adContainer: LinearLayout?) {
        val adView =
            AdView(
                context,
                ApplicationConstants.fbPlacementIdBanner,
                AdSize.BANNER_HEIGHT_50
            )
        // AdSettings.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        // Add the ad view to your activity layout
        adContainer?.removeAllViews()
        adContainer?.addView(adView)

        val adListener: AdListener = object : AdListener {
            override fun onError(ad: Ad?, adError: AdError) {
                // Ad error callback
                Log.d("ErrorAd", "ad" + adError.errorMessage)
            }

            override fun onAdLoaded(ad: Ad?) {
                // Ad loaded callback

                adContainer?.visibility = View.VISIBLE
            }

            override fun onAdClicked(ad: Ad?) {
                // Ad clicked callback
            }

            override fun onLoggingImpression(ad: Ad?) {
                // Ad impression logged callback
            }
        }
        // Request an ad
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build())


    }


    fun loadFacebookNativeAd(nativeAdLayout: NativeAdLayout) {
        fbNativeAd = NativeAd(context, ApplicationConstants.nativeFacebook)
        val nativeAdListener: NativeAdListener = object : NativeAdListener {
            override fun onMediaDownloaded(ad: Ad) {

                // Native ad finished downloading all assets
            }

            override fun onError(ad: Ad?, adError: AdError) {


                // Native ad failed to load
            }

            override fun onAdLoaded(ad: Ad) {
                // Native ad is loaded and ready to be displayed
                if (fbNativeAd != null) {
                    inflateFbNativeAd(fbNativeAd!!, nativeAdLayout)

                }
            }

            override fun onAdClicked(ad: Ad) {
                // Native ad clicked
            }

            override fun onLoggingImpression(ad: Ad) {
                // Native ad impression
            }
        }

        // Request an ad
        fbNativeAd?.buildLoadAdConfig()
            ?.withAdListener(nativeAdListener)
            ?.build().let {
                it?.let { it1 ->
                    fbNativeAd?.loadAd(
                        it1
                    )
                }
            }
    }


    //    ////To inflate facebook native view
    fun inflateFbNativeAd(
        fbNativeAd: NativeAd, nativeAdLayout2: NativeAdLayout
    ) {
        fbNativeAd.unregisterView()
        nativeAdLayout = nativeAdLayout2
        val inflater = LayoutInflater.from(context)
        val fbAdView =
            inflater.inflate(
                R.layout.layout_fb_native_ad,
                nativeAdLayout,
                false
            ) as LinearLayout?
        nativeAdLayout?.addView(fbAdView)
        facebookbinding = fbAdView?.let { DataBindingUtil.bind(it) }
        // Add the AdOptionsView
        val adOptionsView = AdOptionsView(context, fbNativeAd, nativeAdLayout)
        facebookbinding?.adChoicesContainer?.removeAllViews()
        facebookbinding?.adChoicesContainer?.addView(adOptionsView, 0)
        // Set the Text.
        facebookbinding?.nativeAdTitle?.text = fbNativeAd.advertiserName
        facebookbinding?.nativeAdBody?.text = fbNativeAd.adBodyText
        facebookbinding?.nativeAdSocialContext?.text = fbNativeAd.adSocialContext
        if (fbNativeAd.hasCallToAction()) {
            facebookbinding?.nativeAdCallToAction?.visibility = View.VISIBLE
        } else {
            facebookbinding?.nativeAdCallToAction?.visibility = View.INVISIBLE
        }
        facebookbinding?.nativeAdCallToAction?.text = fbNativeAd.adCallToAction
        facebookbinding?.nativeAdSponsoredLabel?.text = fbNativeAd.sponsoredTranslation

        val clickableViews = ArrayList<View>()
        facebookbinding?.nativeAdTitle?.let { clickableViews.add(it) }
        facebookbinding?.nativeAdCallToAction?.let { clickableViews.add(it) }


        fbNativeAd.registerViewForInteraction(
            fbAdView,
            facebookbinding?.nativeAdMedia,
            facebookbinding?.nativeAdIcon,
            clickableViews
        )
    }


    ///load facebook interstitial....
    private fun loadFacebookInterstitialAd() {
        facebookinterstitial =
            InterstitialAd(context, ApplicationConstants.facebookPlacementIdInterstitial)
        val adListener = object : InterstitialAdListener {

            override fun onInterstitialDisplayed(p0: Ad?) {

            }

            override fun onAdClicked(p0: Ad?) {
                adManagerListener.onAdFinish()
            }

            override fun onInterstitialDismissed(p0: Ad?) {
                adManagerListener.onAdFinish()
            }

            override fun onError(p0: Ad?, p1: AdError?) {
                adManagerListener.onAdLoad("failed")
            }

            override fun onAdLoaded(p0: Ad?) {
                adManagerListener.onAdLoad("success")
            }

            override fun onLoggingImpression(p0: Ad?) {

            }


        }
        val loadAdConfig = facebookinterstitial!!.buildLoadAdConfig()
            .withAdListener(adListener)
            .build()

        facebookinterstitial!!.loadAd(loadAdConfig)

    }


    /// show facebook interstitial....
    private fun showFacebookAdInterstitial() {
        if (facebookinterstitial != null) {
            if (facebookinterstitial!!.isAdLoaded) {

                try {
                    facebookinterstitial!!.show()
                } catch (e: Throwable) {

                }


            } else {

                adManagerListener.onAdFinish()
            }
        } else {

            adManagerListener.onAdFinish()
        }


    }


}