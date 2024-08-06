package com.ruthal.live.cricket.app.fixtures.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ruthal.live.cricket.app.constants.ApplicationConstants
import com.ruthal.live.cricket.app.fixtures.models.LiveToken
import com.ruthal.live.cricket.app.fixtures.models.PlayersRankingModel
import com.ruthal.live.cricket.app.network.ApiController
import com.ruthal.live.cricket.app.network.GeneralApiResponseListener
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.await
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class PlayerRankingViewModel : ViewModel() {
    var apiResponseListener: GeneralApiResponseListener? = null
    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)
    private val _teamsOdiListPlayer = MutableLiveData<List<PlayersRankingModel>?>()
    val teamsOdiListPlayer: MutableLiveData<List<PlayersRankingModel>?>
        get() = _teamsOdiListPlayer
    private val _teamsT20ListPlayer = MutableLiveData<List<PlayersRankingModel>?>()
    val teamsT20ListPlayer: MutableLiveData<List<PlayersRankingModel>?>
        get() = _teamsT20ListPlayer
    private val _teamsTestListPlayer = MutableLiveData<List<PlayersRankingModel>?>()
    val teamsTestListPlayer: MutableLiveData<List<PlayersRankingModel>?>
        get() = _teamsTestListPlayer

    private var _isLoadingT20Player = MutableLiveData<Boolean>()
    val isLoadingT20Player: LiveData<Boolean>
        get() = _isLoadingT20Player
    private var _isLoadingOdiPlayer = MutableLiveData<Boolean>()
    val isLoadingOdiPlayer: LiveData<Boolean>
        get() = _isLoadingOdiPlayer
    private var _isLoadingTestPlayer = MutableLiveData<Boolean>()
    val isLoadingTestPlayer: LiveData<Boolean>
        get() = _isLoadingTestPlayer
    var tabSelected = MutableLiveData<String>()


    init {
        _isLoadingT20Player.value = true
        _isLoadingOdiPlayer.value = true
        _isLoadingTestPlayer.value = true
        _teamsOdiListPlayer.value = null
        tabSelected.value = "t20"
    }


    fun getSpinnerData(): List<String> {
        val list = ArrayList<String>()
        list.add("T20")
        list.add("ODI")
        list.add("Test")

        return list

    }

    fun getOdiRanking() {
        _isLoadingOdiPlayer.value = true
        viewModelScope.launch {
            getODIPlayerRanking()
        }
    }

    private suspend fun getODIPlayerRanking() {
        try {
            coroutineScope.launch {
                val apiToken = LiveToken()
                apiToken.token = ApplicationConstants.SCORETOKEN
                val body = Gson().toJson(apiToken)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val getResponse = ApiController.retrofitServiceScore.getPlayerOdiRanking(body)
                try {
                    apiResponseListener?.onStarted()
                    val responseResult = getResponse.await()

                    withContext(Dispatchers.Main) {
                        responseResult.let {
                            if (it != null) {
                                _teamsOdiListPlayer.value = it
                            }
                        }

                        apiResponseListener?.onSuccess()
                        _isLoadingOdiPlayer.value = false
                    }
                } catch (e: Exception) {
                    when (e) {
                        is UnknownHostException -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("Kindly check you Internet Connection!")
                                _isLoadingOdiPlayer.value = false
                            }
                        }

                        is SocketTimeoutException -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("Kindly check you Internet Connection!")
                                _isLoadingOdiPlayer.value = false
                            }
                        }

                        else -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("An error occurred. Try again later!")
                                _isLoadingOdiPlayer.value = false
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            _isLoadingOdiPlayer.value = false
            e.printStackTrace()
        }
    }

    fun getT20Ranking() {
        _isLoadingT20Player.value = true
        viewModelScope.launch {
            getT20PlayerRanking()
        }
    }

    private suspend fun getT20PlayerRanking() {
        _isLoadingT20Player.value = true
        try {
            coroutineScope.launch {
                val apiToken = LiveToken()
                apiToken.token = ApplicationConstants.SCORETOKEN
                val body = Gson().toJson(apiToken)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val getResponse = ApiController.retrofitServiceScore.getPlayerT20Ranking(body)
                try {
                    apiResponseListener?.onStarted()
                    val responseResult = getResponse.await()

                    withContext(Dispatchers.Main) {
                        responseResult.let {
                            if (it != null) {
                                _teamsT20ListPlayer.value = it
                            }
                        }

                        apiResponseListener?.onSuccess()
                        _isLoadingT20Player.value = false
                    }
                } catch (e: Exception) {
                    when (e) {
                        is UnknownHostException -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("Kindly check you Internet Connection!")
                                _isLoadingT20Player.value = false
                            }
                        }

                        is SocketTimeoutException -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("Kindly check you Internet Connection!")
                                _isLoadingT20Player.value = false
                            }
                        }

                        else -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("An error occurred. Try again later!")
                                _isLoadingT20Player.value = false
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            _isLoadingT20Player.value = false
            e.printStackTrace()
        }
    }

    fun getTestRanking() {
        _isLoadingTestPlayer.value = true
        viewModelScope.launch {
            getTestPlayerRanking()
        }
    }

    private suspend fun getTestPlayerRanking() {
        _isLoadingTestPlayer.value = true
        try {
            coroutineScope.launch {
                val apiToken = LiveToken()
                apiToken.token = ApplicationConstants.SCORETOKEN
                val body = Gson().toJson(apiToken)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val getResponse = ApiController.retrofitServiceScore.getPlayerTestRanking(body)
                try {
                    apiResponseListener?.onStarted()
                    val responseResult = getResponse.await()

                    withContext(Dispatchers.Main) {
                        responseResult.let {
                            if (it != null) {
                                _teamsTestListPlayer.value = it
                            }
                        }

                        apiResponseListener?.onSuccess()
                        _isLoadingTestPlayer.value = false
                    }
                } catch (e: Exception) {
                    when (e) {
                        is UnknownHostException -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("Kindly check you Internet Connection!")
                                _isLoadingTestPlayer.value = false
                            }
                        }

                        is SocketTimeoutException -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("Kindly check you Internet Connection!")
                                _isLoadingTestPlayer.value = false
                            }
                        }

                        else -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("An error occurred. Try again later!")
                                _isLoadingTestPlayer.value = false
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            _isLoadingTestPlayer.value = false
            e.printStackTrace()
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.let {
            viewModelJob.cancel()
        }

    }
}
