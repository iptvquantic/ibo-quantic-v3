package com.meuapp.iptvplayer.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.meuapp.iptvplayer.adapter.CategoryAdapter
import com.meuapp.iptvplayer.adapter.LiveChannelAdapter
import com.meuapp.iptvplayer.apps.Constants
import com.meuapp.iptvplayer.databinding.ActivityLiveBinding
import com.meuapp.iptvplayer.helper.IptvRepository
import com.meuapp.iptvplayer.helper.PreferenceHelper
import com.meuapp.iptvplayer.helper.Result
import com.meuapp.iptvplayer.models.CategoryModel
import com.meuapp.iptvplayer.models.LiveChannelModel
import kotlinx.coroutines.launch

class LiveActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiveBinding
    private lateinit var repo: IptvRepository
    private lateinit var catAdapter: CategoryAdapter
    private lateinit var channelAdapter: LiveChannelAdapter
    private val categories = mutableListOf<CategoryModel>()
    private val channels = mutableListOf<LiveChannelModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repo = IptvRepository(this)
        setupRecyclers()
        loadCategories()
        binding.ivBack.setOnClickListener { finish() }
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) { filterChannels(s.toString()) }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun setupRecyclers() {
        catAdapter = CategoryAdapter { cat ->
            if (cat.categoryId == "all") loadAllChannels() else loadChannels(cat.categoryId)
        }
        binding.rvCategories.layoutManager = LinearLayoutManager(this)
        binding.rvCategories.adapter = catAdapter

        channelAdapter = LiveChannelAdapter { channel -> openPlayer(channel) }
        binding.rvChannels.layoutManager = LinearLayoutManager(this)
        binding.rvChannels.adapter = channelAdapter
    }

    private fun loadCategories() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            when (val r = repo.getLiveCategories()) {
                is Result.Success -> {
                    categories.clear()
                    categories.add(CategoryModel("all", "📺 Todos", 0))
                    categories.addAll(r.data)
                    catAdapter.submitList(categories.toList())
                    loadAllChannels()
                }
                is Result.Error -> { binding.progressBar.visibility = View.GONE; Toast.makeText(this@LiveActivity, r.message, Toast.LENGTH_LONG).show() }
            }
        }
    }

    private fun loadAllChannels() {
        lifecycleScope.launch {
            when (val r = repo.getLiveStreams()) {
                is Result.Success -> { channels.clear(); channels.addAll(r.data); channelAdapter.submitList(channels.toList()); binding.progressBar.visibility = View.GONE }
                is Result.Error -> { binding.progressBar.visibility = View.GONE; Toast.makeText(this@LiveActivity, r.message, Toast.LENGTH_LONG).show() }
            }
        }
    }

    private fun loadChannels(catId: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            when (val r = repo.getLiveStreams(catId)) {
                is Result.Success -> { channels.clear(); channels.addAll(r.data); channelAdapter.submitList(channels.toList()); binding.progressBar.visibility = View.GONE }
                is Result.Error -> { binding.progressBar.visibility = View.GONE; Toast.makeText(this@LiveActivity, r.message, Toast.LENGTH_LONG).show() }
            }
        }
    }

    private fun filterChannels(query: String) {
        val filtered = if (query.isEmpty()) channels else channels.filter { it.name.contains(query, ignoreCase = true) }
        channelAdapter.submitList(filtered)
    }

    private fun openPlayer(ch: LiveChannelModel) {
        val url = PreferenceHelper.buildStreamUrl(this, ch.streamId)
        Intent(this, LiveChannelActivity::class.java).apply {
            putExtra(Constants.EXTRA_STREAM_URL, url)
            putExtra(Constants.EXTRA_STREAM_NAME, ch.name)
            putExtra(Constants.EXTRA_STREAM_ICON, ch.streamIcon)
            putExtra(Constants.EXTRA_STREAM_ID, ch.streamId)
            startActivity(this)
        }
    }
}
