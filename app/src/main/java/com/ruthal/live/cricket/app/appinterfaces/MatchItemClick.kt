package com.ruthal.live.cricket.app.appinterfaces

import androidx.navigation.NavDirections
import com.ruthal.live.cricket.app.fixtures.models.ScoresModel
import com.ruthal.live.cricket.app.models.Event

////This interface is for controlling navigation between fragments
interface MatchItemClick {
    fun onMatchClick(scoresModel: ScoresModel)
}