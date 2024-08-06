package com.ruthal.live.cricket.app.ui.adapters

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.ruthal.live.cricket.app.R


@BindingAdapter("loadImage")
fun loadImage(imgView: ImageView, imgUrl: String?){
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
                .load(imgUri)
                .placeholder(R.drawable.channel_placeholder)
                .into(imgView)
    }
}

@BindingAdapter("loadEventImage")
fun loadEventImage(imgView: ImageView, imgUrl: String?){
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .placeholder(R.drawable.event_placeholder)
            .into(imgView)
    }
}