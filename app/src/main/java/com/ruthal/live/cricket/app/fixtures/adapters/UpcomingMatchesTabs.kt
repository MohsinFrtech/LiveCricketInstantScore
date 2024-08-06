package com.ruthal.live.cricket.app.fixtures.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ruthal.live.cricket.app.fixtures.fragments.upcomingformatsfragments.OdiFormatFragment
import com.ruthal.live.cricket.app.fixtures.fragments.upcomingformatsfragments.T20FormatFragment
import com.ruthal.live.cricket.app.fixtures.fragments.upcomingformatsfragments.TestFormatFragment

class UpcomingMatchesTabs (fm: Fragment): FragmentStateAdapter(fm) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> T20FormatFragment()
        1 -> OdiFormatFragment()
        else -> TestFormatFragment()
    }
}