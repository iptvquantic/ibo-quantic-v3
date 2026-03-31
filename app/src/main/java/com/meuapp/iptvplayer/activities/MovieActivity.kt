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
import com.meuapp.iptvplayer.adapter.MovieAdapter
import com.meuapp.iptvplayer.apps.Constants
import com.meuapp.iptvplayer.databinding.ActivityMovieBinding
import com.meuapp.iptvplayer.helper.IptvRepository
import com.meuapp.iptvplayer.helper.Result
import com.meuapp.iptvplayer.models.CategoryModel
import com.meuapp.iptvplayer.models.MovieModel
import kotlinx.coroutines.launch

class MovieActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieBinding
    private lateinit var repo: IptvRepository
    private lateinit var catAdapter: CategoryAdapter
    private lateinit var movieAdapter: MovieAdapter
    private val allMovies = mutableListOf<MovieModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repo = IptvRepository(this)
        binding.ivBack.setOnClickListener { finish() }
        setupRecyclers()
        loadCategories()
        binding.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) { filterMovies(s.toString()) }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun setupRecyclers() {
        catAdapter = CategoryAdapter { cat ->
            if (cat.categoryId == "all") loadAllMovies() else loadMovies(cat.categoryId)
        }
        binding.rvCategories.layoutManager = LinearLayoutManager(this)
        binding.rvCategories.adapter = catAdapter

        movieAdapter = MovieAdapter { movie ->
            Intent(this, MovieInfoActivity::class.java).apply {
                putExtra(Constants.EXTRA_VOD_ID, movie.streamId)
                putExtra(Constants.EXTRA_STREAM_NAME, movie.name)
                putExtra(Constants.EXTRA_STREAM_ICON, movie.streamIcon)
                startActivity(this)
            }
        }
        binding.rvMovies.layoutManager = GridLayoutManager(this, 4)
        binding.rvMovies.adapter = movieAdapter
    }

    private fun loadCategories() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            when (val r = repo.getVodCategories()) {
                is Result.Success -> {
                    val list = mutableListOf(CategoryModel("all","🎬 Todos",0))
                    list.addAll(r.data)
                    catAdapter.submitList(list)
                    loadAllMovies()
                }
                is Result.Error -> { binding.progressBar.visibility = View.GONE; Toast.makeText(this@MovieActivity, r.message, Toast.LENGTH_LONG).show() }
            }
        }
    }

    private fun loadAllMovies() {
        lifecycleScope.launch {
            when (val r = repo.getMovies()) {
                is Result.Success -> { allMovies.clear(); allMovies.addAll(r.data); movieAdapter.submitList(allMovies.toList()); binding.progressBar.visibility = View.GONE }
                is Result.Error -> { binding.progressBar.visibility = View.GONE; Toast.makeText(this@MovieActivity, r.message, Toast.LENGTH_LONG).show() }
            }
        }
    }

    private fun loadMovies(catId: String) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            when (val r = repo.getMovies(catId)) {
                is Result.Success -> { allMovies.clear(); allMovies.addAll(r.data); movieAdapter.submitList(allMovies.toList()); binding.progressBar.visibility = View.GONE }
                is Result.Error -> { binding.progressBar.visibility = View.GONE }
            }
        }
    }

    private fun filterMovies(q: String) {
        movieAdapter.submitList(if (q.isEmpty()) allMovies else allMovies.filter { it.name.contains(q, true) })
    }
}
