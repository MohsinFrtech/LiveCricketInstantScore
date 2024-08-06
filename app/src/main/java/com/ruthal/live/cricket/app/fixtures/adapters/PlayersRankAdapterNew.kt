package com.ruthal.live.cricket.app.fixtures.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.databinding.ItemPlayerRankBinding
import com.ruthal.live.cricket.app.fixtures.models.PlayersRankingModel

class PlayersRankAdapterNew(private val onClickListener: OnClickListener) :
    ListAdapter<PlayersRankingModel, PlayersRankAdapterNew.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemPlayerRankBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PlayersRankingModel) {
            binding.apply {
                model = item
                tvRankId.text = item.rank.toString()
                if (item.avg != null) {
                    tvPoints.text = item.avg.toString()
                } else {
                    tvPoints.text = ""
                }
                tvRatings.text = item.rating.toString()
                ivTeamLogo.setImageResource(R.drawable.event_placeholder)

                executePendingBindings()
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<PlayersRankingModel>() {
        override fun areItemsTheSame(
            oldItem: PlayersRankingModel,
            newItem: PlayersRankingModel
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: PlayersRankingModel,
            newItem: PlayersRankingModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlayerRankBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        holder.itemView.setOnClickListener {
            onClickListener.onClick(item)
        }
    }

    class OnClickListener(val clickListener: (item: PlayersRankingModel) -> Unit) {
        fun onClick(item: PlayersRankingModel) = clickListener(item)
    }
}
