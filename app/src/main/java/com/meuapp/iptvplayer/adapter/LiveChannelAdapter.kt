package com.meuapp.iptvplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.meuapp.iptvplayer.R
import com.meuapp.iptvplayer.databinding.ItemLiveChannelBinding
import com.meuapp.iptvplayer.models.LiveChannelModel

class LiveChannelAdapter(
    private val onClick: (LiveChannelModel) -> Unit
) : ListAdapter<LiveChannelModel, LiveChannelAdapter.VH>(DIFF) {

    inner class VH(val b: ItemLiveChannelBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemLiveChannelBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(h: VH, position: Int) {
        val ch = getItem(position)
        h.b.tvChannelName.text = ch.name
        h.b.tvChannelNum.text = "#${ch.num}"
        h.b.badgeLive.setBackgroundResource(R.drawable.badge_live)
        Glide.with(h.b.ivChannelIcon.context)
            .load(ch.streamIcon)
            .placeholder(R.drawable.ic_tv_placeholder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(h.b.ivChannelIcon)
        h.b.root.setOnClickListener { onClick(ch) }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<LiveChannelModel>() {
            override fun areItemsTheSame(a: LiveChannelModel, b: LiveChannelModel) = a.streamId == b.streamId
            override fun areContentsTheSame(a: LiveChannelModel, b: LiveChannelModel) = a == b
        }
    }
}
