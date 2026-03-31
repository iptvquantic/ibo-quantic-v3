package com.meuapp.iptvplayer.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.meuapp.iptvplayer.R
import com.meuapp.iptvplayer.adapter.SearchResultAdapter
import com.meuapp.iptvplayer.apps.Constants
import com.meuapp.iptvplayer.databinding.ActivitySearchBinding
import com.meuapp.iptvplayer.helper.IptvRepository
import com.meuapp.iptvplayer.helper.PreferenceHelper
import com.meuapp.iptvplayer.helper.Result
import com.meuapp.iptvplayer.models.LiveChannelModel
import com.meuapp.iptvplayer.models.MovieModel
import com.meuapp.iptvplayer.models.SeriesModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SearchItem(
    val id: Int, val name: String, val icon: String,
    val type: String, val ext: String = "mp4", val streamUrl: String = ""
)

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var repo: IptvRepository
    private lateinit var adapter: SearchResultAdapter
    private var searchJob: Job? = null
    private var filterType = "all"

    private var allMovies = listOf<MovieModel>()
    private var allSeries = listOf<SeriesModel>()
    private var allChannels = listOf<LiveChannelModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repo = IptvRepository(this)
        binding.ivBack.setOnClickListener { finish() }

        adapter = SearchResultAdapter { item -> openItem(item) }
        binding.rvResults.layoutManager = GridLayoutManager(this, 3)
        binding.rvResults.adapter = adapter

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch { delay(300); search(s.toString()) }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        preloadData()
    }

    private fun preloadData() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvResultCount.text = "Carregando..."
        lifecycleScope.launch {
            val movies = repo.getMovies()
            val series = repo.getSeries()
            val channels = repo.getLiveStreams()
            if (movies is Result.Success) allMovies = movies.data
            if (series is Result.Success) allSeries = series.data
            if (channels is Result.Success) allChannels = channels.data
            binding.progressBar.visibility = View.GONE
            val total = allMovies.size + allSeries.size + allChannels.size
            binding.tvResultCount.text = "$total itens — digite para pesquisar"
        }
    }

    private fun search(query: String) {
        if (query.length < 2) {
            adapter.submitList(emptyList())
            binding.rvResults.visibility = View.GONE
            binding.llEmpty.visibility = View.VISIBLE
            return
        }
        val results = mutableListOf<SearchItem>()
        allMovies.filter { it.name.contains(query, true) }
            .forEach { results.add(SearchItem(it.streamId, it.name, it.streamIcon, Constants.TYPE_VOD, it.containerExtension)) }
        allSeries.filter { it.name.contains(query, true) }
            .forEach { results.add(SearchItem(it.seriesId, it.name, it.cover, Constants.TYPE_SERIES)) }
        allChannels.filter { it.name.contains(query, true) }
            .forEach {
                val url = PreferenceHelper.buildStreamUrl(this, it.streamId)
                results.add(SearchItem(it.streamId, it.name, it.streamIcon, Constants.TYPE_LIVE, "ts", url))
            }

        adapter.submitList(results)
        binding.rvResults.visibility = if (results.isNotEmpty()) View.VISIBLE else View.GONE
        binding.llEmpty.visibility = if (results.isEmpty()) View.VISIBLE else View.GONE
        binding.tvResultCount.text = if (results.isEmpty()) "Nenhum resultado para \"$query\"" else "${results.size} resultados"
    }

    private fun openItem(item: SearchItem) {
        when (item.type) {
            Constants.TYPE_VOD -> startActivity(Intent(this, MovieInfoActivity::class.java).apply {
                putExtra(Constants.EXTRA_VOD_ID, item.id)
                putExtra(Constants.EXTRA_STREAM_NAME, item.name)
                putExtra(Constants.EXTRA_STREAM_ICON, item.icon)
            })
            Constants.TYPE_SERIES -> startActivity(Intent(this, SeriesInfoActivity::class.java).apply {
                putExtra(Constants.EXTRA_SERIES_ID, item.id)
                putExtra(Constants.EXTRA_STREAM_NAME, item.name)
                putExtra(Constants.EXTRA_STREAM_ICON, item.icon)
            })
            Constants.TYPE_LIVE -> startActivity(Intent(this, LiveChannelActivity::class.java).apply {
                putExtra(Constants.EXTRA_STREAM_URL, item.streamUrl)
                putExtra(Constants.EXTRA_STREAM_NAME, item.name)
                putExtra(Constants.EXTRA_STREAM_ICON, item.icon)
                putExtra(Constants.EXTRA_STREAM_ID, item.id)
            })
        }
    }
}
