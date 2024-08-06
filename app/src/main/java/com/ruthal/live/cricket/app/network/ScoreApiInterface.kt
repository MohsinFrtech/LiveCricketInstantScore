package com.ruthal.live.cricket.app.network

import com.ruthal.live.cricket.app.constants.UnchangedConstants.network_Api_Base_Url
import com.ruthal.live.cricket.app.constants.UnchangedConstants.streamingApiEndPoint
import com.ruthal.live.cricket.app.fixtures.models.PlayersRankingModel
import com.ruthal.live.cricket.app.fixtures.models.RankingTeams
import com.ruthal.live.cricket.app.fixtures.models.ScoresModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface ScoreApiInterface {
    //Score api interface
    @POST("matches/live")
    fun getLiveMatches(
        @Body body: RequestBody
    ): Call<List<ScoresModel>>


    @POST("matches/recent")
    fun getRecentMatches(
        @Body body: RequestBody
    ): Call<List<ScoresModel>>


    @POST("matches/upcoming")
    fun getUpcomingMatches(
        @Body body: RequestBody
    ): Call<List<ScoresModel>>

    @POST("team_rankings/odi")
    fun getFixtureOdiRanking(
        @Body body: RequestBody
    ): Call<List<RankingTeams>>

    @POST("team_rankings/t20")
    fun getFixtureT20Ranking(
        @Body body: RequestBody
    ): Call<List<RankingTeams>>

    @POST("team_rankings/test")
    fun getFixtureTestRank(
        @Body body: RequestBody
    ): Call<List<RankingTeams>>

    @POST("player_rankings/odi")
    fun getPlayerOdiRanking(
        @Body body: RequestBody
    ): Call<List<PlayersRankingModel>>

    @POST("player_rankings/t20")
    fun getPlayerT20Ranking(
        @Body body: RequestBody
    ): Call<List<PlayersRankingModel>>

    @POST("player_rankings/test")
    fun getPlayerTestRanking(
        @Body body: RequestBody
    ): Call<List<PlayersRankingModel>>
}