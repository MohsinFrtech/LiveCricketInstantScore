package com.ruthal.live.cricket.app.fixtures.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ruthal.live.cricket.app.constants.ApplicationConstants
import com.ruthal.live.cricket.app.fixtures.models.LiveToken
import com.ruthal.live.cricket.app.fixtures.models.ScoresModel
import com.ruthal.live.cricket.app.network.ApiController
import com.ruthal.live.cricket.app.network.GeneralApiResponseListener
import com.ruthal.live.cricket.app.utils.CodeUtils
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.await
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class RecentMatchesViewModel(app: Application) : AndroidViewModel(app) {
    var apiResponseListener: GeneralApiResponseListener? = null
    private val appContext: Application = app

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)
    private val _recentMatchesList = MutableLiveData<List<ScoresModel?>>()
    val recentMatchesList: LiveData<List<ScoresModel?>>
        get() = _recentMatchesList
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    var isTabSelect: MutableLiveData<String> = MutableLiveData<String>()

    init {
        _isLoading.value = true
        isTabSelect.value = "T20"
        viewModelScope.launch {
            getRecentMatches()
        }
    }

    fun againLoad() {

        viewModelScope.launch {
            getRecentMatches()
        }
    }

    fun getRecentMatches() {
        viewModelScope.launch {
            loadRecentMatches()
        }
    }

    private suspend fun loadRecentMatches() {
        if (CodeUtils.checkInternetIsAvailable(appContext)) {
            coroutineScope.launch {
                val addUser = LiveToken()
                addUser.token = ApplicationConstants.SCORETOKEN
                val body = Gson().toJson(addUser)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val getResponse = ApiController.retrofitServiceScore.getRecentMatches(body)

                try {

                    val responseResult = getResponse.await()
                    withContext(Dispatchers.Main) {
                        responseResult.let {
                            try {
                                if (it != null) {

                                    it.sortedByDescending { it1 ->
                                        it1.updated_at
                                    }
                                    _recentMatchesList.value = it
                                    _isLoading.value = false
                                    apiResponseListener?.onSuccess()
                                } else {

                                }
                            } catch (e: Exception) {
                                _isLoading.value = false
                                apiResponseListener?.onFailure("Something is wrong with response")
                            }


                        }

                    }

                } catch (e: Exception) {

                    withContext(Dispatchers.Main) {
                        _isLoading.value = false
                        apiResponseListener?.onFailure("Something went wrong, Please try again")
                    }

                    if (e is SocketTimeoutException || e is UnknownHostException) {
                        withContext(Dispatchers.Main) {
                            _isLoading.value = false
                            apiResponseListener?.onFailure("Server is taking too long to respond.")
                        }
                    }
                }

            }
        } else {
            _isLoading.value = false
            apiResponseListener?.onFailure("Internet connection lost! , please check your internet connection")
        }
    }


}