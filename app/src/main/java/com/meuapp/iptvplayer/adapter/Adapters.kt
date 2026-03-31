package com.meuapp.iptvplayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.meuapp.iptvplayer.R
import com.meuapp.iptvplayer.databinding.*
import com.meuapp.iptvplayer.models.*
import com.meuapp.iptvplayer.activities.SearchItem

// ─── SERIES ───────────────────────────────────────────────────────────────────
class SeriesAdapter(
    private val onClick: (SeriesModel) -> Unit
) : ListAdapter<SeriesModel, SeriesAdapter.VH>(DIFF) {

    inner class VH(val b: ItemVodBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemVodBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(h: VH, position: Int) {
        val s = getItem(position)
        h.b.tvName.text = s.name
        h.b.tvRating.text = if (s.rating5based > 0) "⭐ ${"%.1f".format(s.rating5based)}" else ""
        Glide.with(h.b.ivPoster.context)
            .load(s.cover)
            .placeholder(R.drawable.ic_movie_placeholder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(h.b.ivPoster)
        h.b.root.setOnClickListener { onClick(s) }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<SeriesModel>() {
            override fun areItemsTheSame(a: SeriesModel, b: SeriesModel) = a.seriesId == b.seriesId
            override fun areContentsTheSame(a: SeriesModel, b: SeriesModel) = a == b
        }
    }
}

// ─── SEASON ───────────────────────────────────────────────────────────────────
class SeasonAdapter(
    private val seasons: List<Season>,
    private val onClick: (Season) -> Unit
) : RecyclerView.Adapter<SeasonAdapter.VH>() {

    inner class VH(val b: ItemSeasonBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemSeasonBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(h: VH, position: Int) {
        val s = seasons[position]
        h.b.tvSeasonName.text = s.name ?: "Temporada ${s.seasonNumber}"
        h.b.tvEpisodeCount.text = "${s.episodeCount} episódios"
        if (!s.cover.isNullOrEmpty())
            Glide.with(h.b.ivCover.context).load(s.cover).placeholder(R.drawable.ic_movie_placeholder).into(h.b.ivCover)
        h.b.root.setOnClickListener { onClick(s) }
    }

    override fun getItemCount() = seasons.size
}

// ─── EPISODE ──────────────────────────────────────────────────────────────────
class EpisodeAdapter(
    private val episodes: List<Episode>,
    private val onClick: (Episode) -> Unit
) : RecyclerView.Adapter<EpisodeAdapter.VH>() {

    inner class VH(val b: ItemEpisodeBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemEpisodeBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(h: VH, pos: Int) {
        val ep = episodes[pos]
        h.b.tvEpNum.text = "Ep. ${ep.episodeNum}"
        h.b.tvEpTitle.text = ep.title
        h.b.tvEpDuration.text = ep.info?.duration ?: ""
        h.b.tvEpPlot.text = ep.info?.plot ?: ""
        if (!ep.info?.movieImage.isNullOrEmpty())
            Glide.with(h.b.ivThumb.context).load(ep.info?.movieImage).placeholder(R.drawable.ic_movie_placeholder).into(h.b.ivThumb)
        h.b.btnPlay.setOnClickListener { onClick(ep) }
        h.b.root.setOnClickListener { onClick(ep) }
    }

    override fun getItemCount() = episodes.size
}

// ─── EPG ──────────────────────────────────────────────────────────────────────
class EpgAdapter(
    private val items: List<CatchUpEpg>,
    private val onClick: (CatchUpEpg) -> Unit
) : RecyclerView.Adapter<EpgAdapter.VH>() {

    inner class VH(val b: ItemEpgBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemEpgBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(h: VH, pos: Int) {
        val epg = items[pos]
        h.b.tvTitle.text = epg.title ?: "Sem título"
        h.b.tvTime.text = formatTime(epg.startTimestamp) + " – " + formatTime(epg.stopTimestamp)
        h.b.tvDescription.text = epg.description ?: ""
        h.b.root.setOnClickListener { onClick(epg) }
    }

    override fun getItemCount() = items.size

    private fun formatTime(ts: String?): String {
        return try {
            val ms = (ts?.toLong() ?: 0L) * 1000
            val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            sdf.format(java.util.Date(ms))
        } catch (e: Exception) { "--:--" }
    }
}

// ─── PLAYLIST ─────────────────────────────────────────────────────────────────
class PlaylistAdapter(
    private val onSelect: (ServerModel) -> Unit,
    private val onDelete: (ServerModel) -> Unit
) : ListAdapter<ServerModel, PlaylistAdapter.VH>(DIFF2) {

    inner class VH(val b: ItemPlaylistBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(h: VH, pos: Int) {
        val s = getItem(pos)
        h.b.tvName.text = s.name
        h.b.tvUrl.text = s.url
        h.b.tvUser.text = s.username
        h.b.ivActive.visibility = if (s.isActive) android.view.View.VISIBLE else android.view.View.GONE
        h.b.btnSelect.setOnClickListener { onSelect(s) }
        h.b.btnDelete.setOnClickListener { onDelete(s) }
    }

    companion object {
        val DIFF2 = object : DiffUtil.ItemCallback<ServerModel>() {
            override fun areItemsTheSame(a: ServerModel, b: ServerModel) = a.id == b.id
            override fun areContentsTheSame(a: ServerModel, b: ServerModel) = a == b
        }
    }
}

// ─── SEARCH RESULT ────────────────────────────────────────────────────────────
class SearchResultAdapter(
    private val onClick: (SearchItem) -> Unit
) : ListAdapter<SearchItem, SearchResultAdapter.VH>(DIFF3) {

    inner class VH(val b: ItemSearchResultBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(h: VH, pos: Int) {
        val item = getItem(pos)
        h.b.tvName.text = item.name
        h.b.tvType.text = when (item.type) {
            "movie" -> "🎬 Filme"
            "series" -> "📺 Série"
            else -> "📡 Canal"
        }
        Glide.with(h.b.ivPoster.context)
            .load(item.icon)
            .placeholder(R.drawable.ic_movie_placeholder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(h.b.ivPoster)
        h.b.root.setOnClickListener { onClick(item) }
    }

    companion object {
        val DIFF3 = object : DiffUtil.ItemCallback<SearchItem>() {
            override fun areItemsTheSame(a: SearchItem, b: SearchItem) = a.id == b.id && a.type == b.type
            override fun areContentsTheSame(a: SearchItem, b: SearchItem) = a == b
        }
    }
}
