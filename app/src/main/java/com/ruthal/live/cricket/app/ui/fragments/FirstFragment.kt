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
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.appinterfaces.AdManagerListener
import com.ruthal.live.cricket.app.appinterfaces.AppNavigation
import com.ruthal.live.cricket.app.appinterfaces.EventClick
import com.ruthal.live.cricket.app.appinterfaces.MatchItemClick
import com.ruthal.live.cricket.app.constants.ApplicationConstants
import com.ruthal.live.cricket.app.databinding.FirstScreenFragmentBinding
import com.ruthal.live.cricket.app.fixtures.adapters.FixtureItemAdapter
import com.ruthal.live.cricket.app.fixtures.fragments.MatchDetailBottomSheet
import com.ruthal.live.cricket.app.fixtures.models.ScoresModel
import com.ruthal.live.cricket.app.fixtures.viewmodels.MatchesViewModel
import com.ruthal.live.cricket.app.models.Event
import com.ruthal.live.cricket.app.network.GeneralApiResponseListener
import com.ruthal.live.cricket.app.ui.adapters.MainEventAdapter
import com.ruthal.live.cricket.app.utils.CodeUtils.visibility
import com.ruthal.live.cricket.app.viewmodels.StreamingViewModel

class FirstFragment : Fragment(), MatchItemClick, GeneralApiResponseListener, EventClick,AdManagerListener {

    private var bindingMain: FirstScreenFragmentBinding? = null
    private val scoreViewModel by lazy {
        ViewModelProvider(requireActivity())[MatchesViewModel::class.java]
    }
    private val streamingViewModel by lazy {
        activity?.let { ViewModelProvider(it)[StreamingViewModel::class.java] }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.first_screen_fragment, container, false)
        bindingMain = DataBindingUtil.bind(layout)
        bindingMain?.lifecycleOwner = this
        bindingMain?.viewModel = scoreViewModel
        bindingMain?.model = streamingViewModel
        streamingViewModel?.apiResponseListener = this

        getLiveMatchesDataList()
        getLiveEvents()
        bindingMain?.menuIcon?.setOnClickListener {
            findNavController().navigate(R.id.menu)
        }
        return layout
    }

    private fun getLiveEvents() {
        streamingViewModel?.isLoading?.observe(viewLifecycleOwner, Observer {
            if (it) {
                bindingMain?.progressBar?.visibility = View.VISIBLE
            } else {
                bindingMain?.progressBar?.visibility = View.GONE
            }
        })
        streamingViewModel?.dataModelList?.observe(viewLifecycleOwner, Observer {
            if (it.live == true) {
                showStreaming()

                if (!it.events.isNullOrEmpty()) {
                    val liveEvents: MutableList<Event> =
                        ArrayList<Event>()
                    var liveChannelCount = 0
                    showStreaming()
                    it.events!!.forEach { event ->
                        if (event.live == true) {
                            if (!event.channels.isNullOrEmpty()) {
                                liveChannelCount = 0
                                event.channels!!.forEach { channel ->
                                    if (channel.live == true) {
                                        liveChannelCount++
                                    }
                                }

                                if (liveChannelCount > 0) {
                                    liveEvents.add(event)
                                }
                            }
                        }
                    }


                    if (!liveEvents.isNullOrEmpty()) {

                        showStreaming()
                        setRecyclerForLiveEvents(liveEvents)
                    } else {
                        hideStreaming()
                    }
                } else {
                    hideStreaming()
                }
            } else {
                hideStreaming()
            }
        })
    }



    private fun setRecyclerForLiveEvents(liveEvents: MutableList<Event>) {
        bindingMain?.eventRecycler?.visibility(true)
        val listAdapter = context?.let {
            MainEventAdapter(requireContext(), this)
        }
        bindingMain?.eventRecycler?.layoutManager =
            GridLayoutManager(requireContext(), 2)
        bindingMain?.eventRecycler?.isNestedScrollingEnabled = false
        bindingMain?.eventRecycler?.adapter = listAdapter
        listAdapter?.submitList(liveEvents)
    }

    private fun hideStreaming() {
        bindingMain?.noStreaming?.visibility(true)
        bindingMain?.tvLive2?.visibility(false)
        bindingMain?.eventRecycler?.visibility(false)
    }

    private fun showStreaming() {
        bindingMain?.noStreaming?.visibility(false)
        bindingMain?.tvLive2?.visibility(true)
    }

    private fun getLiveMatchesDataList() {
        scoreViewModel?.isLoading?.observe(viewLifecycleOwner, Observer {
            if (it) {
                bindingMain?.progressBar?.visibility = View.VISIBLE
            } else {
                bindingMain?.progressBar?.visibility = View.GONE
            }
        })

        scoreViewModel?.liveMatchList?.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                setAdapter2(it)
            } else {

            }
        })
    }



    private fun setAdapter2(liveScores: List<ScoresModel?>) {
        try {
//            binding?.progressBar?.visibility=View.GONE
            val listAdapter = FixtureItemAdapter(requireContext(), this, "main")
            bindingMain?.recyclerviewLiveSlider?.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            bindingMain?.recyclerviewLiveSlider?.adapter = listAdapter
            listAdapter.submitList(liveScores)

            ////////////////////////////////////////////////
            bindingMain?.viewDots?.attachToRecyclerView(bindingMain!!.recyclerviewLiveSlider)
            if (bindingMain?.recyclerviewLiveSlider?.onFlingListener == null)
                LinearSnapHelper().attachToRecyclerView(bindingMain?.recyclerviewLiveSlider)
            val animator: DefaultItemAnimator = object : DefaultItemAnimator() {
                override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
                    return true
                }
            }
            bindingMain?.recyclerviewLiveSlider?.itemAnimator = animator
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onStarted() {

    }

    override fun onSuccess() {

    }

    override fun onFailure(message: String) {
        if (!message.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "" + message, Toast.LENGTH_LONG).show()
            hideStreaming()
        }
    }

    override fun onClick(event: Event) {
        ApplicationConstants.selectedEvent = event
        val fullscreenModal = ChannelScreenBottomSheet()
        requireActivity().supportFragmentManager.let {
            fullscreenModal.show(
                it,
                "FullscreenModalBottomSheetDialog"
            )
        }
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