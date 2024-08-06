package com.ruthal.live.cricket.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.databinding.BrowseScreenLayoutBinding
import com.ruthal.live.cricket.app.databinding.PlayerScreenBinding
import com.ruthal.live.cricket.app.databinding.RecentMatchesFragmentBinding
import com.ruthal.live.cricket.app.fixtures.adapters.PlayerRankingsTabsAdapter
import com.ruthal.live.cricket.app.fixtures.viewmodels.PlayerRankingViewModel

class PlayerScreenFragment : Fragment() {

    private var player:PlayerScreenBinding?=null
    private val playerRankingViewModel by lazy {
        ViewModelProvider(requireActivity())[PlayerRankingViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layRanking = inflater.inflate(R.layout.player_screen, container, false)
        player = DataBindingUtil.bind(layRanking)
        player?.lifecycleOwner=this
        player?.rankViewmodel=playerRankingViewModel
        playerRankingViewModel?.getT20Ranking()
        playerRankingViewModel?.getOdiRanking()
        playerRankingViewModel?.getTestRanking()
        setPlayerRankingTabs()
        return layRanking
    }

    private fun setPlayerRankingTabs() {
        val recentStatus = resources.getStringArray(R.array.matches_formats_players)
        val fragmentAdapter = PlayerRankingsTabsAdapter(this)
        player?.playerRankingPager?.isUserInputEnabled = true
        player?.playerTabs?.tabGravity = TabLayout.GRAVITY_FILL
        player?.playerRankingPager?.adapter = fragmentAdapter
        player?.playerTabs?.let {
            player?.playerRankingPager?.let { it1 ->
                TabLayoutMediator(
                    it, it1,
                    TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                        tab.text = recentStatus[position]
                    }).attach()
            }
        }

        player?.playerTabs?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

    }

}