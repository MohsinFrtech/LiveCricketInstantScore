package com.ruthal.live.cricket.app.network

import com.ruthal.live.cricket.app.constants.UnchangedConstants.network_Api_Base_Url
import com.ruthal.live.cricket.app.constants.UnchangedConstants.streamingApiEndPoint
import com.ruthal.live.cricket.app.models.Response
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface StreamingApiInterface {
    //streaming api interface
    @POST(streamingApiEndPoint)
    @Headers("Content-Type: application/json")
    fun getStreamingEvents(
        @Body body: RequestBody
    ): Call<StreamingResponse>

    @GET(network_Api_Base_Url)
    fun getIP(): Call<String?>?

    @POST("get_url")
    @Headers("Content-Type: application/json")
    fun getCdnUrl(
        @Body body: RequestBody
    ): Call<Response?>
}