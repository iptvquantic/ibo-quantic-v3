package com.meuapp.iptvplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.meuapp.iptvplayer.R
import com.meuapp.iptvplayer.databinding.ItemVodBinding
import com.meuapp.iptvplayer.models.MovieModel

class MovieAdapter(
    private val onClick: (MovieModel) -> Unit
) : ListAdapter<MovieModel, MovieAdapter.VH>(DIFF) {

    inner class VH(val b: ItemVodBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemVodBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(h: VH, position: Int) {
        val m = getItem(position)
        h.b.tvName.text = m.name
        h.b.tvRating.text = if (m.rating5based > 0) "⭐ ${"%.1f".format(m.rating5based)}" else ""
        Glide.with(h.b.ivPoster.context)
            .load(m.streamIcon)
            .placeholder(R.drawable.ic_movie_placeholder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(h.b.ivPoster)
        h.b.root.setOnClickListener { onClick(m) }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<MovieModel>() {
            override fun areItemsTheSame(a: MovieModel, b: MovieModel) = a.streamId == b.streamId
            override fun areContentsTheSame(a: MovieModel, b: MovieModel) = a == b
        }
    }
}
