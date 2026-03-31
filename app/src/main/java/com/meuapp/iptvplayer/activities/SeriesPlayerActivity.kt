package com.meuapp.iptvplayer.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.media3.common.PlaybackException
import com.meuapp.iptvplayer.apps.BaseActivity
import com.meuapp.iptvplayer.apps.Constants
import com.meuapp.iptvplayer.databinding.ActivitySeriesPlayerBinding
import com.meuapp.iptvplayer.models.Episode

class SeriesPlayerActivity : BaseActivity() {

    private lateinit var binding: ActivitySeriesPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullscreen(); keepScreenOn()
        binding = ActivitySeriesPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = intent.getStringExtra(Constants.EXTRA_STREAM_URL) ?: ""
        val name = intent.getStringExtra(Constants.EXTRA_STREAM_NAME) ?: ""

        binding.tvTitle.text = name
        player = buildPlayer()
        player!!.addListener(makePlayerListener { onPlayerError(it) })
        binding.playerView.player = player

        if (url.isEmpty()) { finish(); return }
        playUrl(url)

        binding.ivBack.setOnClickListener { finish() }
        binding.playerView.setOnClickListener {
            binding.llControls.visibility = if (binding.llControls.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        Toast.makeText(this, "Erro: ${error.message}", Toast.LENGTH_LONG).show()
    }
}
