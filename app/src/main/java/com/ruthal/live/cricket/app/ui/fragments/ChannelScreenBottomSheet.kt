package com.ruthal.live.cricket.app.ui.fragments

import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.admanager.AdManager
import com.ruthal.live.cricket.app.appinterfaces.AdManagerListener
import com.ruthal.live.cricket.app.appinterfaces.ChannelClick
import com.ruthal.live.cricket.app.constants.ApplicationConstants
import com.ruthal.live.cricket.app.constants.ApplicationConstants.adLocation1Provider
import com.ruthal.live.cricket.app.constants.ApplicationConstants.location2BottomProvider
import com.ruthal.live.cricket.app.constants.ApplicationConstants.location2TopPermanentProvider
import com.ruthal.live.cricket.app.constants.ApplicationConstants.location2TopProvider
import com.ruthal.live.cricket.app.constants.ApplicationConstants.locationAfter
import com.ruthal.live.cricket.app.constants.ApplicationConstants.locationBeforeProvider
import com.ruthal.live.cricket.app.constants.ApplicationConstants.nativeAdProviderName
import com.ruthal.live.cricket.app.constants.UnchangedConstants
import com.ruthal.live.cricket.app.databinding.ChannelScreenLayoutBinding
import com.ruthal.live.cricket.app.models.Channel
import com.ruthal.live.cricket.app.ui.activities.StreamingScreen
import com.ruthal.live.cricket.app.ui.adapters.ChannelsList
import com.ruthal.live.cricket.app.utils.CodeUtils.visibility
import com.ruthal.live.cricket.app.viewmodels.StreamingViewModel
import java.util.ArrayList


class ChannelScreenBottomSheet : BottomSheetDialogFragment(), AdManagerListener, ChannelClick {
    private var bindingChannel: ChannelScreenLayoutBinding? = null
    private val viewModelChannel by lazy {
        activity?.let { ViewModelProvider(it)[StreamingViewModel::class.java] }
    }
    private var adManagerClass: AdManager? = null
    private var adStatus = false
    private var base_Link = ""
    private var append_Link = ""
    private var channel_type = ""

    private var listWithAd: List<Channel?> =
        ArrayList<Channel?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBackgroundDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.channel_screen_layout, container, false)
        bindingChannel = DataBindingUtil.bind(layout)
        bindingChannel?.lifecycleOwner = this
        adManagerClass = AdManager(requireContext(), requireActivity(), this)

        if (ApplicationConstants.selectedEvent != null) {
            if (!ApplicationConstants.selectedEvent!!.channels.isNullOrEmpty()) {
                bindingChannel?.eventNameChannels?.text =
                    ApplicationConstants.selectedEvent!!.name.toString()

                setChannelAdapter(ApplicationConstants.selectedEvent!!.channels)
            }
        }

        return layout
    }

    override fun onResume() {
        super.onResume()
        viewModelChannel?.dataModelList?.observe(viewLifecycleOwner, Observer {
            if (!it.app_ads.isNullOrEmpty()) {
                locationBeforeProvider =
                    adManagerClass?.checkProvider(it.app_ads!!, UnchangedConstants.adBefore)
                        .toString()
                locationAfter =
                    adManagerClass?.checkProvider(it.app_ads!!, UnchangedConstants.adAfter)
                        .toString()
                location2TopProvider =
                    adManagerClass?.checkProvider(it.app_ads!!, UnchangedConstants.adLocation2top).toString()

                adLocation1Provider =
                    adManagerClass?.checkProvider(it.app_ads!!, UnchangedConstants.adLocation1)
                        .toString()
                location2BottomProvider =
                    adManagerClass?.checkProvider(
                        it.app_ads!!,
                        UnchangedConstants.adLocation2bottom
                    ).toString()
                location2TopPermanentProvider =
                    adManagerClass?.checkProvider(
                        it.app_ads!!,
                        ApplicationConstants.adLocation2topPermanent
                    ).toString()
                if (locationBeforeProvider.equals(UnchangedConstants.startApp, true)) {
                    if (ApplicationConstants.videoFinish) {
                        ApplicationConstants.videoFinish = false
                        adManagerClass?.loadAdProvider(
                            locationBeforeProvider,
                            UnchangedConstants.adBefore, null, null, null, null
                        )

                    }
                } else {
                    adManagerClass?.loadAdProvider(
                        locationBeforeProvider,
                        UnchangedConstants.adBefore, null, null, null, null
                    )

                }
            }
        })
    }

    private fun setChannelAdapter(channels: List<Channel>?) {

        viewModelChannel?.dataModelList?.observe(viewLifecycleOwner, Observer {
            var nativeFieldVal = ""
            if (!it.extra_3.isNullOrEmpty()) {
                nativeFieldVal = it.extra_3!!
            }


            val liveChannels: MutableList<Channel> =
                ArrayList<Channel>()
            for (channel in channels!!) {
                if (channel.live == true) {
                    liveChannels.add(channel)
                }

            }

            if (liveChannels.isNotEmpty()) {
                bindingChannel?.noStreamingChannel?.visibility(false)
                liveChannels.sortBy { it1 ->
                    it1.priority
                }

                listWithAd = if (nativeAdProviderName.equals(UnchangedConstants.admob, true)) {
                    checkNativeAd(liveChannels)
                } else if (nativeAdProviderName.equals(UnchangedConstants.facebook, true)) {
                    checkNativeAd(liveChannels)
                } else {
                    liveChannels
                }
                val adapter = context?.let {
                    adManagerClass?.let { it1 ->
                        ChannelsList(
                            it, this, listWithAd, nativeAdProviderName,
                            it1, nativeFieldVal, ""
                        )
                    }
                }
                bindingChannel?.channelRecycler?.layoutManager = LinearLayoutManager(context)
                bindingChannel?.channelRecycler?.adapter = adapter
                adapter?.submitList(listWithAd)
            } else {
                hideChannels()
            }
        })
    }

    private fun hideChannels() {
        bindingChannel?.noStreamingChannel?.visibility(true)
        bindingChannel?.channelRecycler?.visibility(false)
    }

    ////Function to return list of channels with empty positions.....
    private fun checkNativeAd(list: List<Channel>): List<Channel?> {
        val tempChannels: MutableList<Channel?> =
            ArrayList()
        for (i in list.indices) {
            if (list[i].live!!) {
                val diff = i % 5
                if (diff == 2) {

                    tempChannels.add(null)
                }
                tempChannels.add(list[i])
                if (list.size == 2) {
                    if (i == 1) {
                        tempChannels.add(null)

                    }
                }
            }
        }
        return tempChannels
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet: FrameLayout =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
        val percent = 80.toFloat() / 100
        val dm = Resources.getSystem().displayMetrics
        val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentWidth = rect.width() * percent
        val percentheight = rect.height() * percent
        // Height of the view
        bottomSheet.layoutParams.height = percentheight.toInt()

        // Behavior of the bottom sheet
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.apply {
            peekHeight = resources.displayMetrics.heightPixels // Pop-up height
            state = BottomSheetBehavior.STATE_EXPANDED // Expanded state

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
    }

    override fun onAdLoad(value: String) {
        adStatus = value.equals("success", true)

    }

    override fun onAdFinish() {
        navigateToNextScreen(base_Link, append_Link, channel_type)
    }

    override fun onClick(baseLink: String, linkAppend: String, channelType: String) {
        base_Link = baseLink
        append_Link = linkAppend
        channel_type = channelType
        if (adStatus) {
            if (!locationBeforeProvider.equals("none", true)) {
                adManagerClass?.showAds(locationBeforeProvider)
            } else {
                navigateToNextScreen(baseLink, linkAppend, channelType)
            }
        } else {
            navigateToNextScreen(baseLink, linkAppend, channelType)
        }

    }

    private fun navigateToNextScreen(baseLink: String, linkAppend: String, channelType: String) {
        val intent = Intent(requireContext(), StreamingScreen::class.java)
        intent.putExtra("base", baseLink)
        intent.putExtra("append", linkAppend)
        intent.putExtra("type", channelType)
        requireActivity().startActivity(intent)
    }


}