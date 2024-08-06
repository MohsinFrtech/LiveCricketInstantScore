package com.ruthal.live.cricket.app.fixtures.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ruthal.live.cricket.app.fixtures.fragments.matchformatsrecentfragments.OdiRecentFragment
import com.ruthal.live.cricket.app.fixtures.fragments.matchformatsrecentfragments.T20RecentFragment
import com.ruthal.live.cricket.app.fixtures.fragments.matchformatsrecentfragments.TestRecentFragment

class RecentlMatchesPagerAdapter (fm: Fragment, status:String): FragmentStateAdapter(fm) {
    var status: String? = null;

    init {
        this.status = status
    }


    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> T20RecentFragment()
        1 -> OdiRecentFragment()
        else -> TestRecentFragment()
    }
}