package com.ruthal.live.cricket.app.fixtures.fragments.upcomingformatsfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.admanager.AdManager
import com.ruthal.live.cricket.app.appinterfaces.AdManagerListener
import com.ruthal.live.cricket.app.appinterfaces.AppNavigation
import com.ruthal.live.cricket.app.appinterfaces.MatchItemClick
import com.ruthal.live.cricket.app.constants.ApplicationConstants
import com.ruthal.live.cricket.app.constants.UnchangedConstants
import com.ruthal.live.cricket.app.databinding.UpcomingMatchesFormatFragmentBinding
import com.ruthal.live.cricket.app.databinding.UpcomingTestFragmentBinding
import com.ruthal.live.cricket.app.fixtures.adapters.RecentMatchesAdapter
import com.ruthal.live.cricket.app.fixtures.models.ScoresModel
import com.ruthal.live.cricket.app.fixtures.viewmodels.UpcomingMatchesViewModel
import com.ruthal.live.cricket.app.utils.CodeUtils
import com.ruthal.live.cricket.app.utils.CodeUtils.visibility

class TestFormatFragment:Fragment(), MatchItemClick, AdManagerListener {
    private val upcomingTestMatchesViewModel by lazy {
        ViewModelProvider(requireActivity())[UpcomingMatchesViewModel::class.java]
    }
    var binding: UpcomingTestFragmentBinding?=null
    private var adManagerClass: AdManager? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.upcoming_test_fragment,container,false)
        binding= DataBindingUtil.bind(view)
        binding?.lifecycleOwner=this
        binding?.matchesModel=upcomingTestMatchesViewModel
        adManagerClass = AdManager(requireContext(), requireActivity(), this)

        setTestDataForUpcomingMatches()
        return view
    }
    private fun setTestDataForUpcomingMatches() {
        upcomingTestMatchesViewModel.upcomingMatchesList.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                binding?.noResult?.visibility(false)
                setRecyclerForTest(it)
            }
            else
            {
                binding?.noResult?.text = resources.getString(R.string.noUpcomingtest)
                binding?.noResult?.visibility(true)
            }
        })
    }

    private fun setRecyclerForTest(liveScoresModels: List<ScoresModel?>) {
        val testFormatList: MutableList<ScoresModel> =
            ArrayList<ScoresModel>()
        liveScoresModels.forEach {
            if (it?.match_format?.equals(UnchangedConstants.match_format_test,true) == true)
            {
                testFormatList.add(it)
            }
        }

        if (!testFormatList.isNullOrEmpty())
        {
            binding?.noResult?.visibility(false)
            val listWithAd: List<ScoresModel?> =
                if (ApplicationConstants.nativeAdProviderName != "none") {
                    CodeUtils.checkNativeAd(testFormatList)
                } else {
                    testFormatList
                }
            val listAdapter = adManagerClass?.let {
                RecentMatchesAdapter(requireContext(),this,"",listWithAd, ApplicationConstants.nativeAdProviderName,
                    it
                )
            }

            binding?.matchesRecycler?.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding?.matchesRecycler?.adapter = listAdapter
            listAdapter?.submitList(listWithAd)
        }
        else
        {
            binding?.noResult?.text = resources.getString(R.string.noUpcomingtest)
            binding?.noResult?.visibility=View.VISIBLE
        }

    }

    override fun onAdLoad(value: String) {

    }

    override fun onAdFinish() {

    }

    override fun onMatchClick(scoresModel: ScoresModel) {

    }
}