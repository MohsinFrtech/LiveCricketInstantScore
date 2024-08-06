package com.ruthal.live.cricket.app.fixtures.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.constants.ApplicationConstants
import com.ruthal.live.cricket.app.databinding.InnerRankingLayoutBinding
import com.ruthal.live.cricket.app.fixtures.adapters.TeamsRankAdapter
import com.ruthal.live.cricket.app.fixtures.models.RankingTeams
import com.ruthal.live.cricket.app.fixtures.viewmodels.TeamsRankingViewModel
import com.ruthal.live.cricket.app.network.GeneralApiResponseListener


class TeamsRankingScreen : Fragment(),GeneralApiResponseListener {

    private var innerRankingLayoutBinding: InnerRankingLayoutBinding? = null
    private val teamRankingInnerViewModel by lazy {
        ViewModelProvider(requireActivity())[TeamsRankingViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        status = requireArguments().getString("status", "2")
        val formatMatch = requireArguments().getString("Fragment_format", "2")
        val innerRankingLay = inflater.inflate(R.layout.inner_ranking_layout, container, false)
        innerRankingLayoutBinding = DataBindingUtil.bind(innerRankingLay)
        innerRankingLayoutBinding?.lifecycleOwner = this
        innerRankingLayoutBinding?.viewModel = teamRankingInnerViewModel
        teamRankingInnerViewModel?.apiResponseListener=this
        observeData(formatMatch)
        return innerRankingLayoutBinding?.root
    }

    private fun observeData(formatMatch: String) {
        if (formatMatch.equals("t20", true)) {
            teamRankingInnerViewModel.isLoadingT20.observe(viewLifecycleOwner, Observer {
                if (it) {
                    innerRankingLayoutBinding?.progressBar?.visibility = View.VISIBLE
                } else {
                    innerRankingLayoutBinding?.progressBar?.visibility = View.GONE

                }
            })
            teamRankingInnerViewModel.teamsT20List?.observe(viewLifecycleOwner, Observer {
                if (!it.isNullOrEmpty()) {
                    setRecyclerForRanking(it)
                }
            })
        } else if (formatMatch.equals("odi", true)) {
            teamRankingInnerViewModel.isLoadingOdi.observe(viewLifecycleOwner, Observer {
                if (it) {
                    innerRankingLayoutBinding?.progressBar?.visibility = View.VISIBLE
                } else {
                    innerRankingLayoutBinding?.progressBar?.visibility = View.GONE

                }
            })
            teamRankingInnerViewModel.teamsOdiList?.observe(viewLifecycleOwner, Observer {
                if (!it.isNullOrEmpty()) {
                    setRecyclerForRanking(it)
                }
            })

        } else {
            teamRankingInnerViewModel.isLoadingTest.observe(viewLifecycleOwner, Observer {
                if (it) {
                    innerRankingLayoutBinding?.progressBar?.visibility = View.VISIBLE
                } else {
                    innerRankingLayoutBinding?.progressBar?.visibility = View.GONE

                }
            })
            teamRankingInnerViewModel.teamsTestList?.observe(viewLifecycleOwner, Observer {
                if (!it.isNullOrEmpty()) {
                    setRecyclerForRanking(it)
                }
            })
        }

    }

    private fun setRecyclerForRanking(it: List<RankingTeams>) {
        val sortedList = it?.sortedWith(compareBy { it.rank })

        val listAdapter = TeamsRankAdapter {
            if (!it.name.isNullOrEmpty()) {
//                if (it.team_id != null) {
//                    AppConstants.selectedTeamId = it.team_id
//                }
//                AppConstants.selectedTeamName=it.name
//                val direction=MainTeamRankingFragmentDirections.actionTeamRankingFragmentToTeamMatchesFragment()
//                findNavController().navigate(direction)
//                this.findNavController().navigate(
//                    TeamsRankingFragmentDirections.actionRankingFragmentToTeamsMatchesFragment(
//                        it.team_id!!,
//                        it.name
//                    )
//                )
            }
        }
        innerRankingLayoutBinding?.recyclerViewTeams?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        innerRankingLayoutBinding?.recyclerViewTeams?.adapter = listAdapter
        listAdapter.submitList(sortedList)
    }

    companion object {
        fun newInstance(statusRanking: ApplicationConstants.MatchesFormats): TeamsRankingScreen {
            val matchesFragment = TeamsRankingScreen()
            val bundle = Bundle()
            bundle.putString("Fragment_format", statusRanking.toString())
            matchesFragment.arguments = bundle
            return matchesFragment
        }
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