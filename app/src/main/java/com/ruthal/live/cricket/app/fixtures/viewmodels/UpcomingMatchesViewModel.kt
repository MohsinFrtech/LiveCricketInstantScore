package com.ruthal.live.cricket.app.fixtures.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import java.util.Date

class UpcomingMatchesViewModel(application: Application) : AndroidViewModel(application) {
    var apiResponseListener: GeneralApiResponseListener? = null
    private var appContext = application
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)
    private val _upcomingMatchesList = MutableLiveData<List<ScoresModel?>>()
    val upcomingMatchesList: LiveData<List<ScoresModel?>>
        get() = _upcomingMatchesList
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    var isTabSelect: MutableLiveData<String> = MutableLiveData<String>()

    init {
        _isLoading.value = true
        isTabSelect.value = "T20"
        loadUpcoming()
    }

    fun againLoad() {
        _isLoading.value = true
        loadUpcoming()
    }

    fun loadUpcoming() {
        viewModelScope.launch {
            loadUpcomingMatches()
        }
    }


    private suspend fun loadUpcomingMatches() {
        if (CodeUtils.checkInternetIsAvailable(appContext)) {
            coroutineScope.launch {
                val addUser = LiveToken()
                addUser.token = ApplicationConstants.SCORETOKEN
                val body = Gson().toJson(addUser)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val getResponse = ApiController.retrofitServiceScore.getUpcomingMatches(body)

                try {

                    val responseResult = getResponse.await()
                    withContext(Dispatchers.Main) {
                        responseResult.let {
                            try {
                                if (it != null) {

                                    it.sortedByDescending { it1 ->
                                        it1.updated_at
                                    }
                                    _upcomingMatchesList.value = it
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