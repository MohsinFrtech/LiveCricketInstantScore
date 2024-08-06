package com.ruthal.live.cricket.app.network

interface GeneralApiResponseListener {
    fun onStarted()
    fun onSuccess()
    fun onFailure(message: String)
}