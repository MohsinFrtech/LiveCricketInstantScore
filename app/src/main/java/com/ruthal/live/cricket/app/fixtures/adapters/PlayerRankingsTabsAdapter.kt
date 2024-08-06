package com.ruthal.live.cricket.app.fixtures.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ruthal.live.cricket.app.constants.UnchangedConstants
import com.ruthal.live.cricket.app.fixtures.fragments.PlayerRankingScreen

class PlayerRankingsTabsAdapter(fm: Fragment) :
    FragmentStateAdapter(fm) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> PlayerRankingScreen.newInstance(UnchangedConstants.player_batsmen)
        1 -> PlayerRankingScreen.newInstance(UnchangedConstants.player_allRounders)
        2 -> PlayerRankingScreen.newInstance(UnchangedConstants.player_bowler)
        else -> {
            PlayerRankingScreen()
        }
    }
}