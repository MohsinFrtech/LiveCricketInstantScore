package com.ruthal.live.cricket.app.network


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.ruthal.live.cricket.app.BuildConfig
import com.ruthal.live.cricket.app.constants.ApplicationConstants.Ip_Api_Base_Url
import com.ruthal.live.cricket.app.constants.ApplicationConstants.NODEAPI_BASE
import com.ruthal.live.cricket.app.constants.ApplicationConstants.SCORE_API_BASEURL
import com.ruthal.live.cricket.app.constants.ApplicationConstants.Streaming_API_BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiController {
    private val moshiConverter = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    ////Gson converter....
    private val gsonConverter: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val okHttpClient: OkHttpClient by lazy {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }

        OkHttpClient.Builder()
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .connectTimeout(90, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }


    private val retrofitInstanceStreaming: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(Streaming_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshiConverter))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())

    }
    private val retrofitInstanceIpApi: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(Ip_Api_Base_Url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gsonConverter))
    }

    private val retrofitNodeApi: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(NODEAPI_BASE)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gsonConverter))
    }

    val apiServiceStreaming: StreamingApiInterface by lazy {
        retrofitInstanceStreaming
            .build()
            .create(StreamingApiInterface::class.java)
    }

    val apiServiceIPApi: StreamingApiInterface by lazy {
        retrofitInstanceIpApi
            .build()
            .create(StreamingApiInterface::class.java)
    }


    private val retrofit: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(SCORE_API_BASEURL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshiConverter))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())

    }


    val retrofitServiceScore: ScoreApiInterface by lazy {
        retrofit
            .build()
            .create(ScoreApiInterface::class.java)
    }
    val retrofitServiceNodeApi: StreamingApiInterface by lazy {
        retrofitNodeApi
            .build()
            .create(StreamingApiInterface::class.java)
    }
}