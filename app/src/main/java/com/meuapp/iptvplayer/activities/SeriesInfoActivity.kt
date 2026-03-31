package com.meuapp.iptvplayer.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.meuapp.iptvplayer.adapter.SeasonAdapter
import com.meuapp.iptvplayer.apps.Constants
import com.meuapp.iptvplayer.databinding.ActivitySeriesInfoBinding
import com.meuapp.iptvplayer.helper.IptvRepository
import com.meuapp.iptvplayer.helper.PreferenceHelper
import com.meuapp.iptvplayer.helper.Result
import com.meuapp.iptvplayer.models.Episode
import com.meuapp.iptvplayer.models.SeriesInfoResponse
import kotlinx.coroutines.launch

class SeriesInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeriesInfoBinding
    private lateinit var repo: IptvRepository
    private var seriesId = 0
    private var allEpisodes: Map<String, List<Episode>> = emptyMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeriesInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repo = IptvRepository(this)
        seriesId = intent.getIntExtra(Constants.EXTRA_SERIES_ID, 0)
        val fallbackName = intent.getStringExtra(Constants.EXTRA_STREAM_NAME) ?: ""
        val fallbackIcon = intent.getStringExtra(Constants.EXTRA_STREAM_ICON) ?: ""
        binding.tvSeriesTitle.text = fallbackName
        if (fallbackIcon.isNotEmpty()) Glide.with(this).load(fallbackIcon).into(binding.ivCover)
        binding.ivBack.setOnClickListener { finish() }
        if (seriesId > 0) loadInfo()
    }

    private fun loadInfo() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            when (val r = repo.getSeriesInfo(seriesId)) {
                is Result.Success -> displayInfo(r.data)
                is Result.Error -> { binding.progressBar.visibility = View.GONE; Toast.makeText(this@SeriesInfoActivity, r.message, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun displayInfo(info: SeriesInfoResponse) {
        binding.progressBar.visibility = View.GONE
        allEpisodes = info.episodes ?: emptyMap()
        info.info?.let { i ->
            binding.tvSeriesTitle.text = i.name ?: binding.tvSeriesTitle.text
            binding.tvDescription.text = i.plot ?: "Sem descrição"
            binding.tvCast.text = "Elenco: ${i.cast ?: "N/A"}"
            binding.tvGenre.text = "Gênero: ${i.genre ?: "N/A"}"
            binding.tvRating.text = "⭐ ${i.rating ?: "N/A"}"
            if (!i.cover.isNullOrEmpty()) Glide.with(this).load(i.cover).into(binding.ivCover)
        }
        val seasons = info.seasons ?: allEpisodes.keys.mapIndexed { i, k ->
            com.meuapp.iptvplayer.models.Season(seasonNumber = k.toIntOrNull() ?: (i + 1), name = "Temporada ${k.toIntOrNull() ?: (i + 1)}")
        }
        val seasonAdapter = SeasonAdapter(seasons) { season ->
            val eps = allEpisodes[season.seasonNumber.toString()] ?: emptyList()
            openEpisodeList(eps, season.seasonNumber)
        }
        binding.rvSeasons.layoutManager = LinearLayoutManager(this)
        binding.rvSeasons.adapter = seasonAdapter
    }

    private fun openEpisodeList(episodes: List<Episode>, season: Int) {
        Intent(this, SeasonActivity::class.java).apply {
            putExtra(Constants.EXTRA_SEASON_NUM, season)
            putExtra(Constants.EXTRA_SERIES_ID, seriesId)
            putParcelableArrayListExtra("episodes", ArrayList(episodes))
            startActivity(this)
        }
    }
}
