package com.ruthal.live.cricket.app.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.appinterfaces.AppNavigation
import com.ruthal.live.cricket.app.appinterfaces.EventClick
import com.ruthal.live.cricket.app.databinding.ItemLayoutEventsBinding
import com.ruthal.live.cricket.app.models.Event


class MainEventAdapter(
    val context: Context, private val navigateData: EventClick
) : ListAdapter<Event, MainEventAdapter.EventAdapterViewHolder>(EventAdapterDiffUtilCallback) {


    private var bindingEvent: ItemLayoutEventsBinding? = null
    private var liveChannelCount = 0

    object EventAdapterDiffUtilCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }

    }


    class EventAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    }


    override fun onBindViewHolder(holder: EventAdapterViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        bindingEvent?.data = currentList[position]
        if (!currentList[position].channels.isNullOrEmpty()) {

            liveChannelCount = 0
            for (i in currentList[position].channels!!) {
                if (i.live == true) {
                    liveChannelCount++
                }
            }
            bindingEvent?.eventChannels?.text = "Channels . " + liveChannelCount
        }
        bindingEvent?.root?.setOnClickListener {
            navigateData.onClick(currentList[position])
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapterViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_layout_events, parent, false)
        bindingEvent = DataBindingUtil.bind(view)
        return EventAdapterViewHolder(view)
    }
}