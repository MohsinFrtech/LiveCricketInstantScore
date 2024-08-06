package com.ruthal.live.cricket.app.ui.adapters

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.admanager.AdManager
import com.ruthal.live.cricket.app.appinterfaces.ChannelClick
import com.ruthal.live.cricket.app.constants.ApplicationConstants
import com.ruthal.live.cricket.app.constants.ApplicationConstants.channel_url
import com.ruthal.live.cricket.app.constants.UnchangedConstants
import com.ruthal.live.cricket.app.constants.UnchangedConstants.sepUrl
import com.ruthal.live.cricket.app.databinding.ItemLayoutChannelsBinding
import com.ruthal.live.cricket.app.databinding.NativeAdLayoutBinding
import com.ruthal.live.cricket.app.models.Channel
import com.ruthal.live.cricket.app.utils.StreamingUtils
import se.simbio.encryption.Encryption
import java.text.SimpleDateFormat
import java.util.*


class ChannelsList(
    val context: Context, private val navigateData: ChannelClick, val list: List<Channel?>,
    private val adType: String, private val adManager: AdManager, private val localVal: String,
    private val destination: String
) : ListAdapter<Channel, RecyclerView.ViewHolder>(
    ChannelAdapterDiffUtilCallback
) {


    private var channelAdapterBinding: ItemLayoutChannelsBinding? = null
    private val nativeAdsLayout = 1
    private val simpleMenuLayout = 0
    private var binding2: NativeAdLayoutBinding? = null

    object ChannelAdapterDiffUtilCallback : DiffUtil.ItemCallback<Channel>() {
        override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {

            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            nativeAdsLayout -> {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.native_ad_layout, parent, false)
                binding2 = DataBindingUtil.bind(view)
                NativeChannelViewHolder(view)
            }

            else -> {

                val view = LayoutInflater.from(context)
                    .inflate(R.layout.item_layout_channels, parent, false)
                channelAdapterBinding = DataBindingUtil.bind(view)
                ChannelAdapterViewHolder(view)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (list[position] == null) {
            return nativeAdsLayout
        }
        return simpleMenuLayout
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        holder.setIsRecyclable(false)
        when (getItemViewType(position)) {
            nativeAdsLayout -> {
                ////For native ads if ads_provider provide native ads..
                val viewHolder: NativeChannelViewHolder = holder as NativeChannelViewHolder

                if (adType.equals(UnchangedConstants.facebook, true)) {

                    if (ApplicationConstants.currentNativeAdFacebook != null) {
                        binding2?.fbNativeAdContainer?.let {
                            adManager.inflateFbNativeAd(
                                ApplicationConstants.currentNativeAdFacebook!!, it
                            )
                        }
                    }
                }

            }

            else -> {
                ///To set remaining events
//                channelAdapterBinding?.dataChannel = currentList[position]
                val viewHolder: ChannelAdapterViewHolder = holder as ChannelAdapterViewHolder
                viewHolder.textChannel.text = currentList[position].name
                loadImage(viewHolder.imageViewChannel, currentList[position].image_url)

                if (!currentList[position].date.isNullOrEmpty()) {
                    dateAndTime(currentList[position].date, viewHolder)
                }

                holder.itemView.setOnClickListener {
                    try {

                        if (currentList[position]?.channel_type.equals(
                                UnchangedConstants.typeFlussonic, true
                            )
                        ) {

                            if (localVal.isNotEmpty()) {
                                ApplicationConstants.parsedString = getChannelType(localVal)
                            }
                            val token = currentList[position].url?.let { it1 ->
                                StreamingUtils.improveDeprecatedCode(it1)
                            }
                            val linkAppend = currentList[position].url + token
                            currentList[position].url?.let { it1 ->
                                navigateData.onClick(
                                    it1, linkAppend,
                                    UnchangedConstants.typeFlussonic
                                )
                            }
                        } else if (currentList[position]?.channel_type.equals(
                                UnchangedConstants.userCdn, true
                            )
                        ) {

                            if (currentList[position]?.url?.contains(sepUrl) == true
                                && ApplicationConstants.passwordVal.isNotEmpty()
                            ) {

                                val separatedPart =
                                    currentList[position]?.url?.split(sepUrl)

                                channel_url = separatedPart?.get(1).toString()

                                navigateData.onClick("base", "", UnchangedConstants.userCdn)
                            }
                        }


                    } catch (e: Exception) {
                        Log.d("Token", "exception" + e.message)
                    }

                }


            }

        }

    }

    fun getChannelType(strToDecrypt: String?): String {
        val iv = ByteArray(UnchangedConstants.mySecretSize)
        val encryption: Encryption = Encryption.getDefault(
            ApplicationConstants.myUserLock1,
            ApplicationConstants.myUserCheck1, iv
        )

        return encryption.decryptOrNull(strToDecrypt)
    }

    private fun dateAndTime(channelDate: String?, viewHolder: ChannelAdapterViewHolder) {
        val df = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH)
        df.timeZone = TimeZone.getTimeZone("UTC")
        val date = channelDate?.let { df.parse(it) }
        df.timeZone = TimeZone.getDefault()
        val formattedDate = date?.let { df.format(it) }
        val date2: Date? = formattedDate?.let { df.parse(it) }
        val cal = Calendar.getInstance()
        if (date2 != null) {
            cal.time = date2
        }
        var hours = cal[Calendar.HOUR_OF_DAY]
        val minutes = cal[Calendar.MINUTE]
        var timeInAmOrPm = ""

        if (hours > 0) {
            timeInAmOrPm = if (hours >= 12) {
                "PM"
            } else {
                "AM"
            }
        }


        if (hours > 0) {
            if (hours >= 12) {
                if (hours == 12) {
                    /////
                } else {
                    hours -= 12
                }
            }
        }

        if (hours == 0) {
            hours = 12
            timeInAmOrPm = "AM"
        }

        val dayOfTheWeek =
            DateFormat.format("EEEE", date) as String

        val day = DateFormat.format("dd", date) as String

        val monthString =
            DateFormat.format("MMM", date) as String
        val year = DateFormat.format("yyyy", date) as String

        var timeValue = ""
        if (minutes < 9) {
            timeValue =
                "$hours:0$minutes $timeInAmOrPm"


            viewHolder?.channelDateTime?.text =
                "$timeValue-$dayOfTheWeek,$day $monthString $year"
        } else {
            timeValue =
                "$hours:$minutes $timeInAmOrPm"

            viewHolder?.channelDateTime?.text =
                "$timeValue-$dayOfTheWeek,$day $monthString $year"
        }

        viewHolder?.channelDateTime?.visibility = View.VISIBLE
    }

    class ChannelAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewChannel = itemView.findViewById<ImageView>(R.id.channelImg)
        val textChannel = itemView.findViewById<TextView>(R.id.channelName)
        val channelDateTime = itemView.findViewById<TextView>(R.id.dateOfChannel)
    }

    ///View holder class
    class NativeChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }

}