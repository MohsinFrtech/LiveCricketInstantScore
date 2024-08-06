package com.ruthal.live.cricket.app.fixtures.fragments

import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.admanager.AdManager
import com.ruthal.live.cricket.app.appinterfaces.AdManagerListener
import com.ruthal.live.cricket.app.appinterfaces.ChannelClick
import com.ruthal.live.cricket.app.constants.ApplicationConstants
import com.ruthal.live.cricket.app.constants.ApplicationConstants.adLocation1Provider
import com.ruthal.live.cricket.app.constants.UnchangedConstants
import com.ruthal.live.cricket.app.databinding.MatchDetailScreenBinding
import com.ruthal.live.cricket.app.fixtures.models.ScoresModel
import com.ruthal.live.cricket.app.models.Channel
import com.ruthal.live.cricket.app.utils.CodeUtils
import java.util.ArrayList


class MatchDetailBottomSheet : BottomSheetDialogFragment(), AdManagerListener, ChannelClick {
    private var bindingDetail: MatchDetailScreenBinding? = null

    private var adManagerClass: AdManager? = null

    private var listWithAd: List<Channel?> =
        ArrayList<Channel?>()
    private var match:ScoresModel?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.TransparentBackgroundDialog)
    }
    private var adManager: AdManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.match_detail_screen, container, false)
        bindingDetail = DataBindingUtil.bind(layout)
        adManagerClass = AdManager(requireContext(), requireActivity(), this)
        if (ApplicationConstants.selectedMatch!=null)
        {
            match = ApplicationConstants.selectedMatch
        }
        adManager = AdManager(requireContext(), requireActivity(), this)

        if (adLocation1Provider != "none") {
            bindingDetail?.fbAdView?.let { it2 ->
                bindingDetail?.startAppBanner?.let { it3 ->
                    adManager?.loadAdProvider(
                        adLocation1Provider, UnchangedConstants.adLocation1,
                        null, it2, bindingDetail?.unityBannerView, it3
                    )
                }
            }
        }

        setMatchDetailData()

        bindingDetail?.cancelIcon?.setOnClickListener {
            dismiss()
        }
        return layout
    }

    //set match detail data.....
    private fun setMatchDetailData() {
        bindingDetail?.model = match
        if (match?.start_time != null && match?.end_time != null) {
            bindingDetail?.tvDateTime?.text =
                CodeUtils.convertLongToTime(match?.start_time!!) + " - " +
                        CodeUtils.convertLongToTime(match?.end_time!!)
        }
        setScoresTextData()
        setTeamImages()
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

    private fun setTeamImages() {
        when (match?.team_1_id) {
            4 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.australia)
            }
            2 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.india)
            }
            6 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.bangladesh)
            }
            5 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.srilanka)
            }
            13 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.newzealand)
            }
            9 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.england)
            }
            3 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.pakistan)
            }
            10 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.westindies)
            }
            96 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.afghanistan)
            }
            11 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.southafrica)
            }
            27 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.ireland)
            }
            23 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.scotland)
            }
            12 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.zimbabwe)
            }
            24 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.netherland)
            }
            72 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.nepal)
            }
            304 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.oman)
            }
            161 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.namibia)
            }
            15 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.us)
            }
            7 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.uae)
            }
            287 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.papuanew)
            }
            63 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.kolkta_knight_rider)
            }
            59 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.royal_challenger_bangolore)
            }
            64 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.rajistan_royals)
            }
            65 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.punjabkings)
            }
            61 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.delhi_capitals)
            }
            971 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.gujrat_titans)
            }
            58 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.chennai_super_kings)
            }
            26 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.canada)
            }
            255 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.sunrises_hyderabad)
            }
            62 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.mumbai_indians)
            }
            966 -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.lucknow_super_gaints)
            }
            else -> {
                bindingDetail?.ivFirstTeam?.setImageResource(R.drawable.event_placeholder)
            }
        }

        when (match?.team_2_id) {
            4 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.australia)
            }
            2 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.india)
            }
            6 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.bangladesh)
            }
            5 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.srilanka)
            }
            13 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.newzealand)
            }
            9 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.england)
            }
            3 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.pakistan)
            }
            10 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.westindies)
            }
            96 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.afghanistan)
            }
            11 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.southafrica)
            }
            27 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.ireland)
            }
            23 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.scotland)
            }
            12 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.zimbabwe)
            }
            24 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.netherland)
            }
            72 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.nepal)
            }
            304 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.oman)
            }
            161 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.namibia)
            }
            15 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.us)
            }
            7 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.uae)
            }
            287 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.papuanew)
            }
            63 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.kolkta_knight_rider)
            }
            59 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.royal_challenger_bangolore)
            }
            64 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.rajistan_royals)
            }
            65 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.punjabkings)
            }
            61 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.delhi_capitals)
            }
            971 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.gujrat_titans)
            }
            58 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.chennai_super_kings)
            }
            26 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.canada)
            }
            255 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.sunrises_hyderabad)
            }
            62 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.mumbai_indians)
            }
            966 -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.lucknow_super_gaints)
            }
            else -> {
                bindingDetail?.ivSecondTeam?.setImageResource(R.drawable.event_placeholder)
            }
        }
    }

    private fun setScoresTextData() {
        //////////////////////////////////////////////////////////////////////////////////////

        var wicketsTeam1In1 = ""
        var wicketsTeam1In2 = ""
        var wicketsTeam2In1 = ""
        var wicketsTeam2In2 = ""

        wicketsTeam1In1 = if (match?.score_card?.team1Score?.inngs1?.wickets != null) {
            match?.score_card?.team1Score?.inngs1?.wickets.toString()
        } else {
            "0"
        }
        wicketsTeam1In2 = if (match?.score_card?.team1Score?.inngs2?.wickets != null) {
            match?.score_card?.team1Score?.inngs2?.wickets.toString()
        } else {
            "0"
        }
        wicketsTeam2In1 = if (match?.score_card?.team2Score?.inngs1?.wickets != null) {
            match?.score_card?.team2Score?.inngs1?.wickets.toString()
        } else {
            "0"
        }
        wicketsTeam2In2 = if (match?.score_card?.team2Score?.inngs2?.wickets != null) {
            match?.score_card?.team2Score?.inngs2?.wickets.toString()
        } else {
            "0"
        }


        if (match?.match_format.equals(UnchangedConstants.match_format_test, true)) {

            var scores_team1 = ""
            var scores_team2 = ""
            var overs_team1 = ""
            var overs_team2 = ""


            if (match?.score_card?.team1Score?.inngs1 != null) {
                if (match?.score_card?.team1Score?.inngs2 != null) {
                    scores_team1 =
                        match?.score_card?.team1Score?.inngs1?.runs.toString() + "/" +
                                wicketsTeam1In1 + " & " +
                                match?.score_card?.team1Score?.inngs2?.runs.toString() + "/" +
                                wicketsTeam1In2

                    overs_team1 = ""
                } else {
                    scores_team1 =
                        match?.score_card?.team1Score?.inngs1?.runs.toString() + "/" +
                                wicketsTeam1In1
                    overs_team1 = "(" +
                            match?.score_card?.team1Score?.inngs1?.overs.toString() + " ov)"
                }
                bindingDetail?.tvFirstScore?.text = scores_team1 + overs_team1
            }
            if (match?.score_card?.team2Score?.inngs1 != null) {
                if (match?.score_card?.team2Score?.inngs2 != null) {
                    scores_team2 =
                        match?.score_card?.team2Score?.inngs1?.runs.toString() + "/" +
                                wicketsTeam2In1 + " & " +
                                match?.score_card?.team2Score?.inngs2?.runs.toString() + "/" +
                                wicketsTeam2In2

                    overs_team2 = ""
                } else {
                    scores_team2 =
                        match?.score_card?.team2Score?.inngs1?.runs.toString() + "/" +
                                wicketsTeam2In1
                    overs_team2 = "(" +
                            match?.score_card?.team2Score?.inngs1?.overs.toString() + " ov)"
                }
                bindingDetail?.tvSecondScore?.text = scores_team2 + overs_team2
            }
        } else {
            if (match?.score_card?.team1Score?.inngs1 != null) {
                bindingDetail?.tvFirstScore?.text =
                    match?.score_card?.team1Score?.inngs1?.runs.toString() + "/" +
                            wicketsTeam1In1 + "(" +
                            match?.score_card?.team1Score?.inngs1?.overs.toString() + " ov)"
            }

            if (match?.score_card?.team2Score?.inngs1 != null) {
                bindingDetail?.tvSecondScore?.text =
                    match?.score_card?.team2Score?.inngs1?.runs.toString() + "/" +
                            wicketsTeam2In1 + "(" +
                            match?.score_card?.team2Score?.inngs1?.overs.toString() + " ov)"
            }
        }

        //////////////////////////////////////////////////////////////////////////////////////
    }

    override fun onAdLoad(value: String) {

    }

    override fun onAdFinish() {

    }

    override fun onClick(baseLink: String, linkAppend: String, channelType: String) {


    }




}