package com.meuapp.iptvplayer.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.PlaybackException
import com.meuapp.iptvplayer.apps.BaseActivity
import com.meuapp.iptvplayer.apps.Constants
import com.meuapp.iptvplayer.apps.LTVApp
import com.meuapp.iptvplayer.databinding.ActivityMoviePlayerBinding
import com.meuapp.iptvplayer.models.ResumeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoviePlayerActivity : BaseActivity() {

    private lateinit var binding: ActivityMoviePlayerBinding
    private var movieUrl = ""
    private var movieName = ""
    private var streamId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullscreen(); keepScreenOn()
        binding = ActivityMoviePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        movieUrl = intent.getStringExtra(Constants.EXTRA_MOVIE_URL) ?: ""
        movieName = intent.getStringExtra(Constants.EXTRA_STREAM_NAME) ?: ""
        streamId = intent.getIntExtra(Constants.EXTRA_STREAM_ID, 0)

        binding.tvTitle.text = movieName
        player = buildPlayer()
        player!!.addListener(makePlayerListener { onPlayerError(it) })
        binding.playerView.player = player

        if (movieUrl.isEmpty()) { finish(); return }

        checkResumeAndPlay()

        binding.ivBack.setOnClickListener { saveProgress(); finish() }
        binding.playerView.setOnClickListener {
            binding.llControls.visibility = if (binding.llControls.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    private fun checkResumeAndPlay() {
        if (streamId > 0) {
            lifecycleScope.launch {
                val resume = withContext(Dispatchers.IO) { LTVApp.database.resumeDao().get(streamId) }
                playUrl(movieUrl)
                if (resume != null && resume.position > 0) {
                    player?.seekTo(resume.position)
                    Toast.makeText(this@MoviePlayerActivity, "Continuando de ${formatTime(resume.position)}", Toast.LENGTH_SHORT).show()
                }
            }
        } else playUrl(movieUrl)
    }

    private fun saveProgress() {
        if (streamId > 0) {
            val pos = player?.currentPosition ?: 0
            val dur = player?.duration ?: 0
            lifecycleScope.launch(Dispatchers.IO) {
                LTVApp.database.resumeDao().insert(ResumeModel(streamId, movieName, "", pos, dur, System.currentTimeMillis()))
            }
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        Toast.makeText(this, "Erro de reprodução: ${error.message}", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() { saveProgress(); super.onDestroy() }

    private fun formatTime(ms: Long): String {
        val s = ms / 1000; return "%02d:%02d:%02d".format(s / 3600, (s % 3600) / 60, s % 60)
    }
}
