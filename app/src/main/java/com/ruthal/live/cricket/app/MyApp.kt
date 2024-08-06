package com.ruthal.live.cricket.app

import android.app.Application
import com.facebook.ads.AudienceNetworkAds


class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AudienceNetworkAds.initialize(this)

    }


}