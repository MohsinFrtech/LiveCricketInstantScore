package com.ruthal.live.cricket.app.fixtures.adapters

import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.appinterfaces.AppNavigation
import com.ruthal.live.cricket.app.appinterfaces.MatchItemClick
import com.ruthal.live.cricket.app.constants.ApplicationConstants
import com.ruthal.live.cricket.app.constants.UnchangedConstants
import com.ruthal.live.cricket.app.databinding.MatchItemBinding
import com.ruthal.live.cricket.app.fixtures.models.ScoresModel
import com.ruthal.live.cricket.app.utils.CodeUtils
import com.ruthal.live.cricket.app.utils.CodeUtils.convertLongToTime
import java.text.SimpleDateFormat
import java.util.*


class FixtureItemAdapter(
    val context: Context,
    private val navigateData: MatchItemClick,
    val source: String
) :
    ListAdapter<ScoresModel, FixtureItemAdapter.LiveSliderAdapterViewHolder>(
        LiveSliderAdapterDiffUtilCallback
    ) {

    class LiveSliderAdapterViewHolder(
        private var binding: MatchItemBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindScoreData(liveScoresModel: ScoresModel) {

            binding.model = liveScoresModel
            val data = liveScoresModel
            binding.executePendingBindings()
            if (data.status != null) {
                binding?.startTime?.text = data.status
            }
            if (data.state.equals("Upcoming", true)) {
                binding?.startTime?.text = context.resources.getString(
                    R.string.venue, data.venue_info?.ground, data.venue_info?.city
                )

            }
            when (data.team_1_id) {
                4 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.australia)
                }

                2 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.india)
                }

                6 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.bangladesh)
                }

                5 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.srilanka)
                }

                13 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.newzealand)
                }

                9 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.england)
                }

                3 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.pakistan)
                }

                10 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.westindies)
                }

                96 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.afghanistan)
                }

                11 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.southafrica)
                }

                27 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.ireland)
                }

                23 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.scotland)
                }

                12 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.zimbabwe)
                }

                24 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.netherland)
                }

                72 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.nepal)
                }

                304 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.oman)
                }

                161 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.namibia)
                }

                15 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.us)
                }

                7 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.uae)
                }

                287 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.papuanew)
                }

                63 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.kolkta_knight_rider)
                }

                59 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.royal_challenger_bangolore)
                }

                64 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.rajistan_royals)
                }

                65 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.punjabkings)
                }

                61 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.delhi_capitals)
                }

                971 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.gujrat_titans)
                }

                58 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.chennai_super_kings)
                }

                26 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.canada)
                }

                255 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.sunrises_hyderabad)
                }

                62 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.mumbai_indians)
                }

                966 -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.lucknow_super_gaints)
                }

                else -> {
                    binding?.ivFirstTeam?.setImageResource(R.drawable.event_placeholder)
                }
            }
            when (data.team_2_id) {
                4 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.australia)
                }

                2 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.india)
                }

                6 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.bangladesh)
                }

                5 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.srilanka)
                }

                13 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.newzealand)
                }

                9 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.england)
                }

                3 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.pakistan)
                }

                10 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.westindies)
                }

                96 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.afghanistan)
                }

                11 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.southafrica)
                }

                27 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.ireland)
                }

                23 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.scotland)
                }

                12 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.zimbabwe)
                }

                24 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.netherland)
                }

                72 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.nepal)
                }

                304 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.oman)
                }

                161 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.namibia)
                }

                15 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.us)
                }

                7 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.uae)
                }

                287 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.papuanew)
                }

                63 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.kolkta_knight_rider)
                }

                59 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.royal_challenger_bangolore)
                }

                64 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.rajistan_royals)
                }

                65 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.punjabkings)
                }

                61 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.delhi_capitals)
                }

                971 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.gujrat_titans)
                }

                58 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.chennai_super_kings)
                }

                26 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.canada)
                }

                255 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.sunrises_hyderabad)
                }

                62 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.mumbai_indians)
                }

                966 -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.lucknow_super_gaints)
                }

                else -> {
                    binding?.ivSecondTeam?.setImageResource(R.drawable.event_placeholder)
                }
            }
            //////////////////////////////////////////////////////////////////////////////////////

            var wicketsTeam1In1 = ""
            var wicketsTeam1In2 = ""
            var wicketsTeam2In1 = ""
            var wicketsTeam2In2 = ""

            wicketsTeam1In1 = if (data.score_card?.team1Score?.inngs1?.wickets != null) {
                data.score_card?.team1Score?.inngs1?.wickets.toString()
            } else {
                "0"
            }
            wicketsTeam1In2 = if (data.score_card?.team1Score?.inngs2?.wickets != null) {
                data.score_card?.team1Score?.inngs2?.wickets.toString()
            } else {
                "0"
            }
            wicketsTeam2In1 = if (data.score_card?.team2Score?.inngs1?.wickets != null) {
                data.score_card?.team2Score?.inngs1?.wickets.toString()
            } else {
                "0"
            }
            wicketsTeam2In2 = if (data.score_card?.team2Score?.inngs2?.wickets != null) {
                data.score_card?.team2Score?.inngs2?.wickets.toString()
            } else {
                "0"
            }

            if (data.match_format.equals(UnchangedConstants.match_format_test, true)) {
                var scoresTeam1 = ""
                var scoresTeam2 = ""
                var oversTeam1 = ""

                var oversTeam2 = ""


                if (data.score_card?.team1Score?.inngs1 != null) {
                    if (data.score_card?.team1Score?.inngs2 != null) {

                        scoresTeam1 = data.score_card?.team1Score?.inngs1?.runs.toString() + "/" +
                                wicketsTeam1In1 + " & " +
                                data.score_card?.team1Score?.inngs2?.runs.toString() + "/" +
                                wicketsTeam1In2

                        oversTeam1 = ""
                    } else {

                        scoresTeam1 = data.score_card?.team1Score?.inngs1?.runs.toString() + "/" +
                                wicketsTeam1In1
                        oversTeam1 =
                            "(" + data.score_card?.team1Score?.inngs1?.overs.toString() + " ov" + ")"
                    }
                    binding?.tvTeam1Score?.text = scoresTeam1
                    binding?.tvTeam1Over?.text = oversTeam1
                }
                if (data.score_card?.team2Score?.inngs1 != null) {
                    if (data.score_card?.team2Score?.inngs2 != null) {

                        scoresTeam2 = data.score_card?.team2Score?.inngs1?.runs.toString() + "/" +
                                wicketsTeam2In1 + " & " +
                                data.score_card?.team2Score?.inngs2?.runs.toString() + "/" +
                                wicketsTeam2In2

                        oversTeam2 = ""
                    } else {

                        scoresTeam2 = data.score_card?.team2Score?.inngs1?.runs.toString() + "/" +
                                wicketsTeam2In1
                        oversTeam2 =
                            "(" + data.score_card?.team2Score?.inngs1?.overs.toString() + " ov" + ")"
                    }
                    binding?.tvTeam2Score?.text = scoresTeam2
                    binding?.tvTeam2Over?.text = oversTeam2
                }
            } else {
                if (data.score_card?.team1Score?.inngs1 != null) {
                    binding?.tvTeam1Score?.text =
                        data.score_card?.team1Score?.inngs1?.runs.toString() + "/" +
                                wicketsTeam1In1
                    binding?.tvTeam1Over?.text = "(" +
                            data.score_card?.team1Score?.inngs1?.overs.toString() + " ov" + ")"
                }

                if (data.score_card?.team2Score?.inngs1 != null) {
                    binding?.tvTeam2Score?.text =
                        data.score_card?.team2Score?.inngs1?.runs.toString() + "/" +
                                wicketsTeam2In1
                    binding?.tvTeam2Over?.text = "(" +
                            data.score_card?.team2Score?.inngs1?.overs.toString() + " ov" + ")"
                }
            }

            //////////////////////////////////////////////////////////////////////////////////////

            if (data.start_time != null && data.end_time != null) {

                val valueShow = CodeUtils.convertDateAndTime(data.start_time!!)
                dateAndTime(valueShow, data.start_time!!, data)
            }
        }

        private fun dateAndTime(channelDate: String?, startTime: Long, dataValue: ScoresModel) {
            val df = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH)
            val date = channelDate.let { it?.let { it1 -> df.parse(it1) } }
            val cal = Calendar.getInstance()
            if (date != null) {
                cal.time = date
            }

            val hours = cal[Calendar.HOUR_OF_DAY]
            val hourOfMatch = hours
            val currentDate = Date()
            val day = DateFormat.format("dd", date) as String
            val currentDay = DateFormat.format("dd", currentDate) as String
            val currentFormatDate = df.format(currentDate)
            val currentParseDate = currentFormatDate.let { df.parse(it) }

            if (day.equals(currentDay, true)) {
                ////means same day

                if (currentParseDate != null) {
                    cal.time = currentParseDate
                }

                val currentParseHour = cal[Calendar.HOUR_OF_DAY]
                val difference: Long = date!!.time - currentParseDate.time
                val days = (difference / (1000 * 60 * 60 * 24)).toInt()
                var hours = ((difference - 1000 * 60 * 60 * 24 * days) / (1000 * 60 * 60)).toInt()
                val min =
                    (difference - 1000 * 60 * 60 * 24 * days - 1000 * 60 * 60 * hours).toInt() / (1000 * 60)
                hours = if (hours < 0) -hours else hours

                if (hours >= 0) {
                    if (min >= 0) {
                        if (hourOfMatch > currentParseHour) {

                            ///means match will start later...
                            binding?.startTime?.text =
                                context.getString(R.string.startTime, hours, min)
                        } else {
                            binding?.startTime?.text =
                                context.getString(R.string.startedTime, hours, min)

                        }


                    } else {
                        val x = Math.abs(min)
                        if (hourOfMatch > currentParseHour) {
                            ///means match will start later...
                            binding?.startTime?.text =
                                context.getString(R.string.startTime, hours, x)
                        } else {
                            binding?.startTime?.text =
                                context.getString(R.string.startedTime, hours, x)

                        }
                    }


                }

            } else {
                ///means live match is not supposed to be the same day....
                binding?.startTime?.text = context.resources.getString(
                    R.string.venue, dataValue.venue_info?.ground, dataValue.venue_info?.city
                )
            }

            val monthString =
                DateFormat.format("MMM", date) as String
            binding?.tvTime?.text = "$day $monthString . " + convertLongToTime(startTime)


        }

    }


    object LiveSliderAdapterDiffUtilCallback : DiffUtil.ItemCallback<ScoresModel>() {
        override fun areItemsTheSame(oldItem: ScoresModel, newItem: ScoresModel): Boolean {
            return oldItem.match_id == newItem.match_id
        }

        override fun areContentsTheSame(
            oldItem: ScoresModel,
            newItem: ScoresModel
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveSliderAdapterViewHolder {
        val binding: MatchItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.match_item, parent, false
        )
        return LiveSliderAdapterViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: LiveSliderAdapterViewHolder, position: Int) {
//        holder.setIsRecyclable(false)
        holder.bindScoreData(currentList[position])
        val data = currentList[position]
        holder.itemView.setOnClickListener {
            if (!data.status.isNullOrEmpty()) {
                if (source.equals("main", true)) {
                  navigateData.onMatchClick(currentList[position])
                } else if (source.equals("recent", true)) {
//                    val itemDirection2 =
//                        RecentFragmentDirections.actionRecentFragmentToLiveDetails(data)
//                    navigateData.navigation(itemDirection2)
                } else if (source.equals("seriesMatch", true)) {
                    if (!data.status.equals("Upcoming", true)) {

//                        val itemDirection2 =
//                            SeriesMatchFragmentDirections.actionSeriesMatchFragmentToLiveDetails(
//                                data
//                            )
//                        navigateData.navigation(itemDirection2)
                    }


                }

            }
        }
    }


}