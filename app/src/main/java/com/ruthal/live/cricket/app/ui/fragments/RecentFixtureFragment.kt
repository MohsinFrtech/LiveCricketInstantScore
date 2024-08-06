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
import com.ruthal.live.cricket.app.databinding.RecentMatchesFragmentBinding
import com.ruthal.live.cricket.app.fixtures.adapters.RecentlMatchesPagerAdapter
import com.ruthal.live.cricket.app.fixtures.viewmodels.RecentMatchesViewModel
import com.ruthal.live.cricket.app.network.GeneralApiResponseListener

class RecentFixtureFragment : Fragment(),GeneralApiResponseListener {

    private var recent: RecentMatchesFragmentBinding? = null
    private val recentMatchesViewModel by lazy {
        ViewModelProvider(requireActivity())[RecentMatchesViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layRecent = inflater.inflate(R.layout.recent_matches_fragment, container, false)
        recent = DataBindingUtil.bind(layRecent)
        recent?.lifecycleOwner=this
        recent?.viewModel = recentMatchesViewModel
        recentMatchesViewModel.apiResponseListener=this
        observeData()
        setUpViewPager()
        return layRecent
    }
    private fun observeData() {

        recentMatchesViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it)
            {
                recent?.progressBar?.visibility=View.VISIBLE
            }
            else
            {
                recent?.progressBar?.visibility=View.GONE
            }
        })
    }
    private fun setUpViewPager() {
        val recentStatus = resources.getStringArray(R.array.matches_formats)
        val fragmentAdapter = RecentlMatchesPagerAdapter(this, "2")
        recent?.viewPagerRecent?.isUserInputEnabled = true
        recent?.tabsRecent?.tabGravity = TabLayout.GRAVITY_FILL
        recent?.viewPagerRecent?.currentItem=0
        recent?.viewPagerRecent?.adapter = fragmentAdapter
        recent?.tabsRecent?.let {
            recent?.viewPagerRecent?.let { it1 ->
                TabLayoutMediator(it, it1
                ) { tab: TabLayout.Tab, position: Int ->

                    tab.text = recentStatus[position]
                }.attach()
            }
        }


        recent?.tabsRecent?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{

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