package com.ruthal.live.cricket.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.databinding.UpcomingMatchesLayoutBinding
import com.ruthal.live.cricket.app.fixtures.adapters.RecentlMatchesPagerAdapter
import com.ruthal.live.cricket.app.fixtures.adapters.UpcomingMatchesTabs
import com.ruthal.live.cricket.app.fixtures.viewmodels.RecentMatchesViewModel
import com.ruthal.live.cricket.app.fixtures.viewmodels.UpcomingMatchesViewModel
import com.ruthal.live.cricket.app.network.GeneralApiResponseListener

class UpcomingMatchesFragment : Fragment(),GeneralApiResponseListener {
    private var upcoming: UpcomingMatchesLayoutBinding? = null
    private val upcomingMatchesViewModel by lazy {
        ViewModelProvider(requireActivity())[UpcomingMatchesViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layUpcoming = inflater.inflate(R.layout.upcoming_matches_layout, container, false)
        upcoming = DataBindingUtil.bind(layUpcoming)
        upcoming?.lifecycleOwner=this
        upcomingMatchesViewModel?.apiResponseListener=this
        setUpViewPager()
        observeData()
        return layUpcoming
    }

    private fun observeData() {

        upcomingMatchesViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it)
            {
                upcoming?.progressBar?.visibility=View.VISIBLE
            }
            else
            {
                upcoming?.progressBar?.visibility=View.GONE
            }
        })
    }
    private fun setUpViewPager() {
        val recentStatus = resources.getStringArray(R.array.matches_formats)
        val fragmentAdapter = UpcomingMatchesTabs(this)
        upcoming?.viewPagerRecent?.isUserInputEnabled = true
        upcoming?.tabsRecent?.tabGravity = TabLayout.GRAVITY_FILL
        upcoming?.viewPagerRecent?.currentItem=0
        upcoming?.viewPagerRecent?.adapter = fragmentAdapter
        upcoming?.tabsRecent?.let {
            upcoming?.viewPagerRecent?.let { it1 ->
                TabLayoutMediator(it, it1
                ) { tab: TabLayout.Tab, position: Int ->

                    tab.text = recentStatus[position]
                }.attach()
            }
        }


        upcoming?.tabsRecent?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{

            override fun onTabSelected(tab: TabLayout.Tab?) {
//

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {


            }
        })

//        binding?.tabsRecent?.setTabTextColors(resources.getColor(R.color.textColor), resources.getColor(R.color.white))

    }

    override fun onStarted() {

    }

    override fun onSuccess() {

    }

    override fun onFailure(message: String) {
        if (!message.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "" + message, Toast.LENGTH_LONG).show()
        }
    }
}