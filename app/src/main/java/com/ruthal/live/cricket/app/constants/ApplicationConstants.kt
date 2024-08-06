package com.ruthal.live.cricket.app.constants

import com.ruthal.live.cricket.app.BuildConfig
import com.ruthal.live.cricket.app.fixtures.models.ScoresModel
import com.ruthal.live.cricket.app.models.Channel
import com.ruthal.live.cricket.app.models.Event

object ApplicationConstants {
    var SCORE_API_BASEURL = ""
    var NODEAPI_BASE = ""
    var passwordVal=""
    var channel_url=""
    var Streaming_API_BASE_URL = ""
    var Ip_Api_Base_Url = "https://ip-api.streamingucms.com/"
    var SCORETOKEN = ""
    var webSocketUrl = ""
    var socketAuth = ""
    enum class MatchesFormats(val key: String) {
        T20("t20"), ODI("odi"), TEST("test")
    }
    var currentNativeAdFacebook:com.facebook.ads.NativeAd?=null

    var appId = "10"
    var appVersionCode = BuildConfig.VERSION_CODE
    var appTaken = ""
    var passValue = ""
    var numberValues = ""
    var myUserLock1 = "locked"
    var videoFinish=false
    var selectedMatch: ScoresModel?=null
    var selectedEvent: Event?=null
    var splash_status = false

    var parsedString = ""

    var myUserCheck1 = "myUserCheck1"
    var app_update_dialog = false
    var networkIp = "userIp"
    var admobInterstitial = ""
    var facebookPlacementIdInterstitial = ""
    var fbPlacementIdBanner = ""
    var chartBoostAppID = ""
    var chartBoostAppSig = ""
    var nativeAdmob: String = ""
    var unityGameID = ""
    var startAppId = ""
    var nativeFacebook = ""
    var admobBannerId = ""
    var adLocation1Provider = ""
    const val adLocation2topPermanent = "Location2TopPermanent"
    var location2TopPermanentProvider="none"
    //Ad Locations
    var location2TopProvider = "none"
    var location2BottomProvider = "none"
    var locationAfter = "none"
    var nativeAdProviderName="none"
    var locationBeforeProvider="none"
    var isInitAdmobSdk = false
    var isInitFacebookSdk = false
    var isUnitySdkInit = false
    var isChartboostSdkInit = false
    var isStartAppSdkInit = false
    var middleAdProvider = "none"
}