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
import com.ruthal.live.cricket.app.fixtures.adapters.RecentMatchesAdapter
import com.ruthal.live.cricket.app.fixtures.models.ScoresModel
import com.ruthal.live.cricket.app.fixtures.viewmodels.UpcomingMatchesViewModel
import com.ruthal.live.cricket.app.utils.CodeUtils
import com.ruthal.live.cricket.app.utils.CodeUtils.visibility

class T20FormatFragment:Fragment(), MatchItemClick, AdManagerListener {
    private val upcomingT20MatchesViewModel by lazy {
        ViewModelProvider(requireActivity())[UpcomingMatchesViewModel::class.java]
    }
    var binding: UpcomingMatchesFormatFragmentBinding?=null
    private var adManagerClass: AdManager? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.upcoming_matches_format_fragment,container,false)
        binding=DataBindingUtil.bind(view)
        binding?.lifecycleOwner=this
        binding?.matchesModel=upcomingT20MatchesViewModel
        adManagerClass = AdManager(requireContext(), requireActivity(), this)
        setT20DataForUpcomingMatches()
        return view
    }

    private fun setT20DataForUpcomingMatches() {
        upcomingT20MatchesViewModel.upcomingMatchesList.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                binding?.noResult?.visibility(false)
                setRecyclerForT20(it)
            }
            else
            {
                binding?.noResult?.text = resources.getString(R.string.noUpcomingT20)
                binding?.noResult?.visibility(true)
            }
        })
    }

    private fun setRecyclerForT20(liveScoresModels: List<ScoresModel?>) {
        val t20FormatList: MutableList<ScoresModel> =
            ArrayList<ScoresModel>()
        liveScoresModels.forEach {
            if (it?.match_format?.equals(UnchangedConstants.match_format_t20,true) == true)
            {
                t20FormatList.add(it)
            }
        }
        if (!t20FormatList.isNullOrEmpty())
        {
            binding?.noResult?.visibility(false)
            val listWithAd: List<ScoresModel?> =
                if (ApplicationConstants.nativeAdProviderName != "none") {
                    CodeUtils.checkNativeAd(t20FormatList)
                } else {
                    t20FormatList
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
            binding?.noResult?.text = resources.getString(R.string.noUpcomingT20)
            binding?.noResult?.visibility(true)
        }

    }



    override fun onAdLoad(value: String) {

    }

    override fun onAdFinish() {
    }

    override fun onMatchClick(scoresModel: ScoresModel) {

    }
}