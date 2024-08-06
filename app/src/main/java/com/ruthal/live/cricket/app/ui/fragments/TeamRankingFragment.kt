package com.ruthal.live.cricket.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.databinding.BrowseScreenLayoutBinding
import com.ruthal.live.cricket.app.fixtures.adapters.TeamRankingsTabsAdapter
import com.ruthal.live.cricket.app.fixtures.viewmodels.TeamsRankingViewModel

class TeamRankingFragment : Fragment() {

    private var ranking: BrowseScreenLayoutBinding? = null
    private val teamRankingViewModel by lazy {
        ViewModelProvider(requireActivity())[TeamsRankingViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layRecent = inflater.inflate(R.layout.browse_screen_layout, container, false)
        ranking = DataBindingUtil.bind(layRecent)
        ranking?.lifecycleOwner=this
        ranking?.rankViewmodel=teamRankingViewModel
        teamRankingViewModel?.getTeamT20Ranking()
        teamRankingViewModel?.getTeamOdiRanking()
        teamRankingViewModel?.getTeamTestRanking()
        setTeamRankingTabs()
        return layRecent
    }

    private fun setTeamRankingTabs() {
        val recentStatus = resources.getStringArray(R.array.matches_formats)
        val fragmentAdapter = TeamRankingsTabsAdapter(this)
        ranking?.teamRankingPager?.isUserInputEnabled = true
        ranking?.teamTabs?.tabGravity = TabLayout.GRAVITY_FILL
        ranking?.teamRankingPager?.adapter = fragmentAdapter
        ranking?.teamTabs?.let {
            ranking?.teamRankingPager?.let { it1 ->
                TabLayoutMediator(
                    it, it1,
                    TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                        tab.text = recentStatus[position]
                    }).attach()
            }
        }

        ranking?.teamTabs?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

    }

}