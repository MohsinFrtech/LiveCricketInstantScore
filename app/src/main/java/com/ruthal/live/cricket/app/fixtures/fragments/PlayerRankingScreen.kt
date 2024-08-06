package com.ruthal.live.cricket.app.fixtures.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.constants.UnchangedConstants
import com.ruthal.live.cricket.app.databinding.InnerPlayerRankingLayoutBinding
import com.ruthal.live.cricket.app.fixtures.adapters.PlayersRankAdapterNew
import com.ruthal.live.cricket.app.fixtures.models.PlayersRankingModel
import com.ruthal.live.cricket.app.fixtures.viewmodels.PlayerRankingViewModel
import com.ruthal.live.cricket.app.network.GeneralApiResponseListener

class PlayerRankingScreen : Fragment(), AdapterView.OnItemSelectedListener ,GeneralApiResponseListener{

    private var innerPlayerRankingLayoutBinding: InnerPlayerRankingLayoutBinding? = null
    private val teamRankingInnerViewModel by lazy {
        ViewModelProvider(requireActivity())[PlayerRankingViewModel::class.java]
    }
    var formatMatch = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        status = requireArguments().getString("status", "2")
        formatMatch = requireArguments().getString("Fragment_format", "2")
        Log.d("formatSelected", "format" + formatMatch)

        val innerRankingLay =
            inflater.inflate(R.layout.inner_player_ranking_layout, container, false)
        innerPlayerRankingLayoutBinding = DataBindingUtil.bind(innerRankingLay)
        innerPlayerRankingLayoutBinding?.lifecycleOwner = this
        innerPlayerRankingLayoutBinding?.viewModel = teamRankingInnerViewModel
        teamRankingInnerViewModel?.apiResponseListener=this
        setSpinnerData()
        observeData(formatMatch)
        return innerPlayerRankingLayoutBinding?.root
    }

    private fun setSpinnerData() {
        // Create an ArrayAdapter using a simple spinner layout and languages array
        innerPlayerRankingLayoutBinding?.rankSpinner?.onItemSelectedListener = this

        val aa = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            teamRankingInnerViewModel.getSpinnerData()
        )
        // Set layout to use when the list of choices appear
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Set Adapter to Spinner
        innerPlayerRankingLayoutBinding?.rankSpinner?.adapter = aa

    }

    private fun observeData(formatMatch: String) {
        if (formatMatch.equals(UnchangedConstants.player_batsmen, true)) {
            teamRankingInnerViewModel.isLoadingT20Player.observe(viewLifecycleOwner, Observer {
                if (it) {
                    innerPlayerRankingLayoutBinding?.progressBar?.visibility = View.VISIBLE
                } else {
                    innerPlayerRankingLayoutBinding?.progressBar?.visibility = View.GONE

                }
            })
            teamRankingInnerViewModel.teamsT20ListPlayer.observe(viewLifecycleOwner, Observer {
                if (!it.isNullOrEmpty()) {
                    val listPlayerSpecific: MutableList<PlayersRankingModel> =
                        ArrayList<PlayersRankingModel>()
                    it.forEach {
                        if (it.format.equals(UnchangedConstants.match_format_t20, true)) {
                            if (it.category?.equals(UnchangedConstants.player_batsmen, true) == true) {
                                listPlayerSpecific.add(it)
                            }
                        }
                    }
                    setRecyclerForRanking(listPlayerSpecific)
                }
            })
        } else if (formatMatch.equals(UnchangedConstants.player_allRounders, true)) {
            teamRankingInnerViewModel.isLoadingOdiPlayer.observe(viewLifecycleOwner, Observer {
                if (it) {
                    innerPlayerRankingLayoutBinding?.progressBar?.visibility = View.VISIBLE
                } else {
                    innerPlayerRankingLayoutBinding?.progressBar?.visibility = View.GONE

                }
            })
            teamRankingInnerViewModel.teamsT20ListPlayer.observe(viewLifecycleOwner, Observer {
                if (!it.isNullOrEmpty()) {
                    val listPlayerSpecific: MutableList<PlayersRankingModel> =
                        ArrayList<PlayersRankingModel>()
                    it.forEach {
                        if (it.format.equals(UnchangedConstants.match_format_t20, true)) {
                            if (it.category?.equals("allrounders", true) == true) {
                                listPlayerSpecific.add(it)
                            }
                        }
                    }
                    setRecyclerForRanking(listPlayerSpecific)
                }
            })

        } else {
            teamRankingInnerViewModel.isLoadingTestPlayer.observe(viewLifecycleOwner, Observer {
                if (it) {
                    innerPlayerRankingLayoutBinding?.progressBar?.visibility = View.VISIBLE
                } else {
                    innerPlayerRankingLayoutBinding?.progressBar?.visibility = View.GONE

                }
            })
            teamRankingInnerViewModel.teamsT20ListPlayer.observe(viewLifecycleOwner, Observer {
                if (!it.isNullOrEmpty()) {
                    val listPlayerSpecific: MutableList<PlayersRankingModel> =
                        ArrayList<PlayersRankingModel>()
                    it.forEach {
                        if (it.format.equals(UnchangedConstants.match_format_t20, true)) {
                            if (it.category?.equals(UnchangedConstants.player_bowler, true) == true) {
                                listPlayerSpecific.add(it)
                            }
                        }
                    }
                    setRecyclerForRanking(listPlayerSpecific)
                }
            })
        }

    }

    private fun setRecyclerForRanking(it: List<PlayersRankingModel>) {
        val sortedList = it?.sortedWith(compareBy { it.rank })

        val listAdapter = PlayersRankAdapterNew(PlayersRankAdapterNew.OnClickListener {

        })
        innerPlayerRankingLayoutBinding?.recyclerViewTeams?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        innerPlayerRankingLayoutBinding?.recyclerViewTeams?.adapter = listAdapter
        listAdapter.submitList(sortedList)
    }

    companion object {
        fun newInstance(statusRanking: String): PlayerRankingScreen {
            val matchesFragment = PlayerRankingScreen()
            val bundle = Bundle()
            bundle.putString("Fragment_format", statusRanking.toString())
            matchesFragment.arguments = bundle
            return matchesFragment
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        sortListAgainstFormat(p2)
    }

    private fun sortListAgainstFormat(p2: Int) {
        when (p2) {
            0 -> {
               val format=sortSubList()
                teamRankingInnerViewModel.teamsT20ListPlayer.observe(viewLifecycleOwner, Observer {
                    if (!it.isNullOrEmpty()) {
                        val listPlayerSpecific: MutableList<PlayersRankingModel> =
                            ArrayList<PlayersRankingModel>()
                        it.forEach {
                            if (it.format.equals(UnchangedConstants.match_format_t20, true)) {
                                if (it.category?.equals(format, true) == true) {
                                    listPlayerSpecific.add(it)
                                }
                            }
                        }
                        setRecyclerForRanking(listPlayerSpecific)
                    }
                })
            }

            1 -> {
                val format=sortSubList()
                teamRankingInnerViewModel.teamsOdiListPlayer.observe(viewLifecycleOwner, Observer {
                    if (!it.isNullOrEmpty()) {
                        val listPlayerSpecific: MutableList<PlayersRankingModel> =
                            ArrayList<PlayersRankingModel>()
                        it.forEach {
                            if (it.format.equals(UnchangedConstants.match_format_odi, true)) {
                                if (it.category?.equals(format, true) == true) {
                                    listPlayerSpecific.add(it)
                                }
                            }
                        }
                        setRecyclerForRanking(listPlayerSpecific)
                    }
                })

            }

            2 -> {
                val format=sortSubList()
                teamRankingInnerViewModel.teamsTestListPlayer.observe(viewLifecycleOwner, Observer {
                    if (!it.isNullOrEmpty()) {
                        val listPlayerSpecific: MutableList<PlayersRankingModel> =
                            ArrayList<PlayersRankingModel>()
                        it.forEach {
                            if (it.format.equals(UnchangedConstants.match_format_test, true)) {
                                if (it.category?.equals(format, true) == true) {
                                    listPlayerSpecific.add(it)
                                }
                            }
                        }
                        setRecyclerForRanking(listPlayerSpecific)
                    }
                })

            }
        }
    }

    private fun sortSubList():String {

        if (formatMatch.equals(UnchangedConstants.player_batsmen,true))
        {
            return UnchangedConstants.player_batsmen
        }
        else if (formatMatch.equals(UnchangedConstants.player_allRounders,true))
        {
            return "allrounders"
        }
        else
        {
            return UnchangedConstants.player_bowler
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

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