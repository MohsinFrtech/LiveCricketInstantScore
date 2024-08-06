package com.ruthal.live.cricket.app.fixtures.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.ruthal.live.cricket.app.constants.ApplicationConstants
import com.ruthal.live.cricket.app.fixtures.models.LiveToken
import com.ruthal.live.cricket.app.fixtures.models.RankingTeams
import com.ruthal.live.cricket.app.network.ApiController
import com.ruthal.live.cricket.app.network.GeneralApiResponseListener
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.await
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class TeamsRankingViewModel : ViewModel() {
    var apiResponseListener: GeneralApiResponseListener? = null


    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    private val _teamsList = MutableLiveData<List<RankingTeams>?>()
    val teamsList: MutableLiveData<List<RankingTeams>?>
        get() = _teamsList

    private val _teamsOdiList = MutableLiveData<List<RankingTeams>>()
    val teamsOdiList: LiveData<List<RankingTeams>>?
        get() = _teamsOdiList
    private val _teamsT20List = MutableLiveData<List<RankingTeams>>()
    val teamsT20List: LiveData<List<RankingTeams>>?
        get() = _teamsT20List
    private val _teamsTestList = MutableLiveData<List<RankingTeams>>()
    val teamsTestList: LiveData<List<RankingTeams>>?
        get() = _teamsTestList

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading
    private var _isLoadingT20 = MutableLiveData<Boolean>()
    val isLoadingT20: LiveData<Boolean>
        get() = _isLoadingT20
    private var _isLoadingOdi = MutableLiveData<Boolean>()
    val isLoadingOdi: LiveData<Boolean>
        get() = _isLoadingOdi
    private var _isLoadingTest = MutableLiveData<Boolean>()
    val isLoadingTest: LiveData<Boolean>
        get() = _isLoadingTest
    init {
        _isLoadingT20.value = true
        _isLoadingOdi.value = true
        _isLoadingTest.value = true
        _isLoading.value = false
    }

    fun getSpinnerData(): List<String> {
        val list = ArrayList<String>()

        list.add("ODI")
        list.add("T20")
        list.add("TEST")

        return list
    }

    fun getTeamOdiRanking() {
        viewModelScope.launch {
            getODIRanking()
        }
    }

    private suspend fun getODIRanking() {
        _isLoadingOdi.value = true
        try {
            coroutineScope.launch {
                val addUser = LiveToken()
                addUser.token = ApplicationConstants.SCORETOKEN
                val body = Gson().toJson(addUser)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val getResponse = ApiController.retrofitServiceScore.getFixtureOdiRanking(body)
                try {
                    apiResponseListener?.onStarted()
                    val responseResult = getResponse.await()

                    withContext(Dispatchers.Main) {
                        responseResult.let {
                            if (it != null) {
                                _teamsOdiList.value = it
                            }
                        }

                        apiResponseListener?.onSuccess()
                        _isLoadingOdi.value = false
                    }
                } catch (e: Exception) {
                    when (e) {
                        is UnknownHostException -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("Kindly check you Internet Connection!")
                                _isLoadingOdi.value = false
                            }
                        }

                        is SocketTimeoutException -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("Kindly check you Internet Connection!")
                                _isLoadingOdi.value = false
                            }
                        }

                        else -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("An error occurred. Try again later!")
                                _isLoadingOdi.value = false
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getTeamT20Ranking() {
        viewModelScope.launch {
            getT20Ranking()
        }
    }

    private suspend fun getT20Ranking() {
        _isLoadingT20.value = true
        try {
            coroutineScope.launch {
                val addUser = LiveToken()
                addUser.token = ApplicationConstants.SCORETOKEN
                val body = Gson().toJson(addUser)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val getResponse = ApiController.retrofitServiceScore.getFixtureT20Ranking(body)
                try {
                    apiResponseListener?.onStarted()
                    val responseResult = getResponse.await()

                    withContext(Dispatchers.Main) {
                        responseResult.let {
                            if (it != null) {
                                _teamsT20List.value = it
                            }
                        }

                        apiResponseListener?.onSuccess()
                        _isLoadingT20.value = false
                    }
                } catch (e: Exception) {
                    when (e) {
                        is UnknownHostException -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("Kindly check you Internet Connection!")
                                _isLoadingT20.value = false
                            }
                        }

                        is SocketTimeoutException -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("Kindly check you Internet Connection!")
                                _isLoadingT20.value = false
                            }
                        }

                        else -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("An error occurred. Try again later!")
                                _isLoadingT20.value = false
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getTeamTestRanking() {
        viewModelScope.launch {
            getTestRanking()
        }
    }

    private suspend fun getTestRanking() {
        _isLoadingTest.value = true
        try {
            coroutineScope.launch {
                val addUser = LiveToken()
                addUser.token = ApplicationConstants.SCORETOKEN
                val body = Gson().toJson(addUser)
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

                val getResponse = ApiController.retrofitServiceScore.getFixtureTestRank(body)
                try {
                    apiResponseListener?.onStarted()
                    val responseResult = getResponse.await()

                    withContext(Dispatchers.Main) {
                        responseResult.let {
                            if (it != null) {

                                _teamsTestList.value = it
                            }
                        }

                        apiResponseListener?.onSuccess()
                        _isLoadingTest.value = false
                    }
                } catch (e: Exception) {
                    when (e) {
                        is UnknownHostException -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("Kindly check you Internet Connection!")
                                _isLoadingTest.value = false
                            }
                        }

                        is SocketTimeoutException -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("Kindly check you Internet Connection!")
                                _isLoadingTest.value = false
                            }
                        }

                        else -> {
                            withContext(Dispatchers.Main) {
                                apiResponseListener?.onFailure("An error occurred. Try again later!")
                                _isLoadingTest.value = false
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun formatTeamsSelected(name: Any) {
        _teamsList.value = null
        when (name) {
            0 -> {
                _teamsList.value = _teamsOdiList.value

            }

            1 -> {
                _teamsList.value = _teamsT20List.value

            }

            2 -> {
                _teamsList.value = _teamsTestList.value

            }
        }

    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.let {
            viewModelJob.cancel()
        }

    }
}
