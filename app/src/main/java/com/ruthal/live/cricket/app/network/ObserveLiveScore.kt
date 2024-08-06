package com.ruthal.live.cricket.app.network

import com.ruthal.live.cricket.app.fixtures.models.ScoresModel

interface ObserveLiveScore {
    fun scoresData(liveModel: List<ScoresModel>)
}