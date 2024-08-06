package com.ruthal.live.cricket.app.fixtures.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ruthal.live.cricket.app.constants.ApplicationConstants
import com.ruthal.live.cricket.app.fixtures.fragments.TeamsRankingScreen

class TeamRankingsTabsAdapter(fm: Fragment) :
    FragmentStateAdapter(fm) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> TeamsRankingScreen.newInstance(ApplicationConstants.MatchesFormats.T20)
        1 -> TeamsRankingScreen.newInstance(ApplicationConstants.MatchesFormats.ODI)
        else -> {
            TeamsRankingScreen.newInstance(ApplicationConstants.MatchesFormats.TEST)
        }
    }
}