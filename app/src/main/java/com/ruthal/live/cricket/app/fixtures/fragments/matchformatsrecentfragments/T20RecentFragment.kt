package com.ruthal.live.cricket.app.fixtures.fragments.matchformatsrecentfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.admanager.AdManager
import com.ruthal.live.cricket.app.appinterfaces.AdManagerListener
import com.ruthal.live.cricket.app.appinterfaces.AppNavigation
import com.ruthal.live.cricket.app.appinterfaces.MatchItemClick
import com.ruthal.live.cricket.app.constants.ApplicationConstants
import com.ruthal.live.cricket.app.constants.UnchangedConstants
import com.ruthal.live.cricket.app.databinding.RecentMatchesFormatFragmentBinding
import com.ruthal.live.cricket.app.fixtures.adapters.RecentMatchesAdapter
import com.ruthal.live.cricket.app.fixtures.fragments.MatchDetailBottomSheet
import com.ruthal.live.cricket.app.fixtures.models.ScoresModel
import com.ruthal.live.cricket.app.fixtures.viewmodels.RecentMatchesViewModel
import com.ruthal.live.cricket.app.utils.CodeUtils
import com.ruthal.live.cricket.app.utils.CodeUtils.visibility

class T20RecentFragment : Fragment(), MatchItemClick , AdManagerListener {
    private val recentT20MatchesViewModel by lazy {
        ViewModelProvider(requireActivity())[RecentMatchesViewModel::class.java]
    }
    var binding: RecentMatchesFormatFragmentBinding? = null
    private var adManagerClass: AdManager? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.recent_matches_format_fragment, container, false)
        binding = DataBindingUtil.bind(view)
        binding?.lifecycleOwner = this
        binding?.recentMatchesModel = recentT20MatchesViewModel
        adManagerClass = AdManager(requireContext(), requireActivity(), this)
        setT20DataForRecentMatches()
        return view
    }

    private fun setT20DataForRecentMatches() {
        recentT20MatchesViewModel.recentMatchesList.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                binding?.noResult?.visibility(false)
                setRecyclerForT20(it)
            } else {
                binding?.noResult?.text = resources.getString(R.string.noRecentT20)
                binding?.noResult?.visibility(true)

            }
        })
    }

    private fun setRecyclerForT20(liveScoresModels: List<ScoresModel?>) {
        val t20FormatList: MutableList<ScoresModel> =
            ArrayList<ScoresModel>()
        liveScoresModels.forEach {
            if (it?.match_format?.equals(UnchangedConstants.match_format_t20, true) == true) {
                t20FormatList.add(it)
            }
        }
        val listWithAd: List<ScoresModel?> =
            if (ApplicationConstants.nativeAdProviderName != "none") {
                CodeUtils.checkNativeAd(t20FormatList)
            } else {
                t20FormatList
            }
        val listAdapter = adManagerClass?.let {
            RecentMatchesAdapter(requireContext(),this,"recent",listWithAd, ApplicationConstants.nativeAdProviderName,
                it
            )
        }
        binding?.recentMatchesRecycler?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding?.recentMatchesRecycler?.adapter = listAdapter
        listAdapter?.submitList(listWithAd)
    }


    override fun onAdLoad(value: String) {

    }

    override fun onAdFinish() {

    }

    override fun onMatchClick(scoresModel: ScoresModel) {
        ApplicationConstants.selectedMatch = scoresModel
        val fullscreenModal = MatchDetailBottomSheet()
        requireActivity().supportFragmentManager.let {
            fullscreenModal.show(
                it,
                "FullscreenModalBottomSheetDialog"
            )
        }
    }
}