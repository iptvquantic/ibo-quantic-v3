package com.meuapp.iptvplayer.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.meuapp.iptvplayer.adapter.CategoryAdapter
import com.meuapp.iptvplayer.adapter.SeriesAdapter
import com.meuapp.iptvplayer.apps.Constants
import com.meuapp.iptvplayer.databinding.ActivitySeriesBinding
import com.meuapp.iptvplayer.helper.IptvRepository
import com.meuapp.iptvplayer.helper.Result
import com.meuapp.iptvplayer.models.CategoryModel
import com.meuapp.iptvplayer.models.SeriesModel
import kotlinx.coroutines.launch

class SeriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeriesBinding
    private lateinit var repo: IptvRepository
    private lateinit var catAdapter: CategoryAdapter
    private lateinit var seriesAdapter: SeriesAdapter
    private val allSeries = mutableListOf<SeriesModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repo = IptvRepository(this)
        binding.ivBack.setOnClickListener { finish() }
        setupRecyclers()
        loadCategories()
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) { filter(s.toString()) }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun setupRecyclers() {
        catAdapter = CategoryAdapter { cat ->
            if (cat.categoryId == "all") loadAll() else loadByCat(cat.categoryId)
        }
        binding.rvCategories.layoutManager = LinearLayoutManager(this)
        binding.rvCategories.adapter = catAdapter

        seriesAdapter = SeriesAdapter { series ->
            Intent(this, SeriesInfoActivity::class.java).apply {
                putExtra(Constants.EXTRA_SERIES_ID, series.seriesId)
                putExtra(Constants.EXTRA_STREAM_NAME, series.name)
                putExtra(Constants.EXTRA_STREAM_ICON, series.cover)
                startActivity(this)
            }
        }
        binding.rvSeries.layoutManager = GridLayoutManager(this, 4)
        binding.rvSeries.adapter = seriesAdapter
    }

    private fun loadCategories() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            when (val r = repo.getSeriesCategories()) {
                is Result.Success -> {
                    val list = mutableListOf(CategoryModel("all","📺 Todas",0)); list.addAll(r.data)
                    catAdapter.submitList(list); loadAll()
                }
                is Result.Error -> { binding.progressBar.visibility = View.GONE; Toast.makeText(this@SeriesActivity, r.message, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun loadAll() {
        lifecycleScope.launch {
            when (val r = repo.getSeries()) {
                is Result.Success -> { allSeries.clear(); allSeries.addAll(r.data); seriesAdapter.submitList(allSeries.toList()); binding.progressBar.visibility = View.GONE }
                is Result.Error -> binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun loadByCat(catId: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            when (val r = repo.getSeries(catId)) {
                is Result.Success -> { allSeries.clear(); allSeries.addAll(r.data); seriesAdapter.submitList(allSeries.toList()); binding.progressBar.visibility = View.GONE }
                is Result.Error -> binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun filter(q: String) {
        seriesAdapter.submitList(if (q.isEmpty()) allSeries else allSeries.filter { it.name.contains(q, true) })
    }
}
