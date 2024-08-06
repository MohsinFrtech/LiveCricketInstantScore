package com.ruthal.live.cricket.app.appinterfaces

import androidx.navigation.NavDirections
import com.ruthal.live.cricket.app.models.Event

////This interface is for controlling navigation between fragments
interface ChannelClick {
    fun onClick(baseLink: String,linkAppend:String,
                channelType:String)
}