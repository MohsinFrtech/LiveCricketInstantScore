package com.ruthal.live.cricket.app.appinterfaces

import androidx.navigation.NavDirections

////This interface is for controlling navigation between fragments
interface AppNavigation {
    fun navigation(viewId: NavDirections)
}