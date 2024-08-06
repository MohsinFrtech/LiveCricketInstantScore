package com.ruthal.live.cricket.app.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ruthal.live.cricket.app.constants.ApplicationConstants.NODEAPI_BASE
import com.ruthal.live.cricket.app.constants.ApplicationConstants.Streaming_API_BASE_URL
import com.ruthal.live.cricket.app.constants.ApplicationConstants.appId
import com.ruthal.live.cricket.app.constants.ApplicationConstants.appTaken
import com.ruthal.live.cricket.app.constants.ApplicationConstants.appVersionCode
import com.ruthal.live.cricket.app.constants.ApplicationConstants.channel_url
import com.ruthal.live.cricket.app.constants.ApplicationConstants.networkIp
import com.ruthal.live.cricket.app.constants.ApplicationConstants.passValue
import com.ruthal.live.cricket.app.constants.ApplicationConstants.passwordVal
import com.ruthal.live.cricket.app.models.DataModel
import com.ruthal.live.cricket.app.models.NodeFile
import com.ruthal.live.cricket.app.models.StreamingApiParams
import com.ruthal.live.cricket.app.network.ApiController
import com.ruthal.live.cricket.app.network.GeneralApiResponseListener
import com.ruthal.live.cricket.app.utils.CodeUtils
import com.ruthal.live.cricket.app.utils.StreamingResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.await
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class StreamingViewModel(app: Application) : AndroidViewModel(app) {
    private var viewModelJob = Job()
    private val appContext: Application = app
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    private val streamingResponse = StreamingResponse()
    var apiResponseListener: GeneralApiResponseListener? = null


    private val _dataModelList = MutableLiveData<DataModel>()
    val dataModelList: LiveData<DataModel>
        get() = _dataModelList

    private var _userLink = MutableLiveData<String>()
    val userLink: LiveData<String>
        get() = _userLink

    init {
        _isLoading.value = true
    }

    fun onRefreshEvents() {
        _isLoading.value = true
        viewModelScope.launch {
            getLiveEventsFromRemote()
        }

    }

    fun getLiveEvents() {
        viewModelScope.launch {
            getLiveEventsFromRemote()
        }
    }

    private suspend fun getLiveEventsFromRemote() {
        _isLoading.value = true
        if (CodeUtils.checkInternetIsAvailable(appContext)) {
            if (Streaming_API_BASE_URL != "") {
                coroutineScope.launch {
                    val params = StreamingApiParams()
                    params.id = appId
                    params.auth_token = appTaken
                    params.build_no = appVersionCode.toString()
//                    params.build_no = "0"
                    val body = Gson().toJson(params)
                        .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                    val getResponse = ApiController.apiServiceStreaming.getStreamingEvents(
                        body
                    )
                    try {

                        val responseResult = getResponse.await()
                        withContext(Dispatchers.Main) {
                            responseResult.let {
                                try {
                                    val model = streamingResponse.parseResponse(it?.data)
                                    streamingResponse.getExtraValuesFromResponse(model)
                                    _dataModelList.value = model
                                    if (networkIp.equals("userIp", true)) {
                                        getIP()
                                    } else {

                                    }

                                } catch (e: Exception) {

                                    _isLoading.value = false
                                    apiResponseListener?.onFailure("Something is wrong with response")
                                    //e.printStackTrace();
                                }


                            }
                            _isLoading.value = false

                        }

                    } catch (e: Exception) {
                        Log.d("Exception", "" + "coming34......" + e.localizedMessage)

                        withContext(Dispatchers.Main) {
                            _isLoading.value = false
                            apiResponseListener?.onFailure("Something went wrong, Please try again")
                        }
                        if (e is SocketTimeoutException) {
                            withContext(Dispatchers.Main) {
                                _isLoading.value = false
                                apiResponseListener?.onFailure("Server is taking too long to respond.")
                            }
                        }
                        if (e is UnknownHostException) {
                            withContext(Dispatchers.Main) {
                                _isLoading.value = false
                                apiResponseListener?.onFailure("Server is taking too long to respond.")
                            }
                        }
                    }
                }

            } else {
                _isLoading.value = false
                apiResponseListener?.onFailure("Server is taking too long to respond.")
            }

        } else {
            _isLoading.value = false
            apiResponseListener?.onFailure("Internet connection lost! , please check your internet connection")
        }

    }

    private fun getIP() {
        if (CodeUtils.checkInternetIsAvailable(appContext)) {
            coroutineScope.launch {
                val getResponse = ApiController.apiServiceIPApi.getIP()
                try {
                    val responseResult = getResponse?.await()
                    withContext(Dispatchers.Main) {
                        if (responseResult != null) {
                            networkIp = responseResult.toString()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }

    }



    fun connectionWithCdnApi(){
        _isLoading.value = true
        viewModelScope.launch {
            getLinkFromServer()
        }
    }

    private suspend fun getLinkFromServer() {
        if (NODEAPI_BASE != "") {
            if (CodeUtils.checkInternetIsAvailable(appContext)) {
                val addUser = NodeFile()
                addUser.passphrase = passwordVal
                addUser.channel_url = channel_url
                val body = Gson().toJson(addUser)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())


                coroutineScope.launch {
                    val getResponse = ApiController.retrofitServiceNodeApi.getCdnUrl(
                        body
                    )

                    try {
                        val responseResult = getResponse.await()
                        withContext(Dispatchers.Main) {
                            responseResult.let {

                                if (!it?.url.isNullOrEmpty()) {

                                    _userLink.value = it?.url.toString()

                                }

                            }
                            _isLoading.value = false
                        }

                    } catch (e: Exception) {

                        withContext(Dispatchers.Main) {

                            _isLoading.value = false
                            _userLink.value=""
                        }

                    }

                }

            } else {
                _isLoading.value = false
                _userLink.value=""
                apiResponseListener?.onFailure("Internet connection lost! , please check your internet connection")
            }
        } else {
            _userLink.value=""
            apiResponseListener?.onFailure("Server is taking too long to respond.")
        }
    }


    // On ViewModel Cleared
    override fun onCleared() {
        super.onCleared()
        viewModelJob.let {
            viewModelJob.cancel()
        }

    }
}