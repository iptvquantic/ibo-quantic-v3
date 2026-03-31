package com.meuapp.iptvplayer.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.meuapp.iptvplayer.apps.Constants
import com.meuapp.iptvplayer.databinding.ActivityMovieInfoBinding
import com.meuapp.iptvplayer.helper.IptvRepository
import com.meuapp.iptvplayer.helper.PreferenceHelper
import com.meuapp.iptvplayer.helper.Result
import com.meuapp.iptvplayer.models.MovieInfoResponse
import kotlinx.coroutines.launch

class MovieInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMovieInfoBinding
    private lateinit var repo: IptvRepository
    private var vodId = 0
    private var containerExt = "mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repo = IptvRepository(this)
        vodId = intent.getIntExtra(Constants.EXTRA_VOD_ID, 0)
        val fallbackName = intent.getStringExtra(Constants.EXTRA_STREAM_NAME) ?: ""
        val fallbackIcon = intent.getStringExtra(Constants.EXTRA_STREAM_ICON) ?: ""

        binding.tvMovieTitle.text = fallbackName
        if (fallbackIcon.isNotEmpty()) {
            Glide.with(this).load(fallbackIcon).into(binding.ivPoster)
        }

        binding.ivBack.setOnClickListener { finish() }
        binding.btnPlay.setOnClickListener { startPlayer() }

        if (vodId > 0) loadInfo()
    }

    private fun loadInfo() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            when (val r = repo.getMovieInfo(vodId)) {
                is Result.Success -> displayInfo(r.data)
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@MovieInfoActivity, r.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayInfo(info: MovieInfoResponse) {
        binding.progressBar.visibility = View.GONE
        val i = info.info
        if (i != null) {
            binding.tvMovieTitle.text = i.name ?: binding.tvMovieTitle.text
            binding.tvDescription.text = i.plot ?: i.description ?: "Sem descrição"
            binding.tvDirector.text = "Diretor: ${i.director ?: "N/A"}"
            binding.tvCast.text = "Elenco: ${i.cast ?: i.actors ?: "N/A"}"
            binding.tvGenre.text = "Gênero: ${i.genre ?: "N/A"}"
            binding.tvYear.text = "Ano: ${i.releaseDate ?: "N/A"}"
            binding.tvDuration.text = "Duração: ${i.duration ?: "N/A"}"
            binding.tvRating.text = "⭐ ${i.rating ?: "N/A"}"
            val posterUrl = when {
                !i.coverBig.isNullOrEmpty() -> i.coverBig
                !i.movieImage.isNullOrEmpty() -> i.movieImage
                else -> null
            }
            if (posterUrl != null) {
                Glide.with(this).load(posterUrl).into(binding.ivPoster)
            }
        }
        containerExt = info.movieData?.containerExtension ?: "mp4"
    }

    private fun startPlayer() {
        val url = PreferenceHelper.buildMovieUrl(this, vodId, containerExt)
        Intent(this, MoviePlayerActivity::class.java).apply {
            putExtra(Constants.EXTRA_MOVIE_URL, url)
            putExtra(Constants.EXTRA_STREAM_NAME, binding.tvMovieTitle.text.toString())
            startActivity(this)
        }
    }
}
