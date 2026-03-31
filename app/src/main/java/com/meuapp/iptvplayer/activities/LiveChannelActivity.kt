package com.meuapp.iptvplayer.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.media3.common.PlaybackException
import androidx.media3.ui.PlayerView
import com.bumptech.glide.Glide
import com.meuapp.iptvplayer.apps.BaseActivity
import com.meuapp.iptvplayer.apps.Constants
import com.meuapp.iptvplayer.databinding.ActivityLiveChannelBinding

class LiveChannelActivity : BaseActivity() {

    private lateinit var binding: ActivityLiveChannelBinding
    private var streamUrl = ""
    private var streamName = ""
    private var streamIcon = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullscreen(); keepScreenOn()
        binding = ActivityLiveChannelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        streamUrl = intent.getStringExtra(Constants.EXTRA_STREAM_URL) ?: ""
        streamName = intent.getStringExtra(Constants.EXTRA_STREAM_NAME) ?: ""
        streamIcon = intent.getStringExtra(Constants.EXTRA_STREAM_ICON) ?: ""

        binding.tvChannelName.text = streamName
        if (streamIcon.isNotEmpty()) Glide.with(this).load(streamIcon).into(binding.ivChannelLogo)

        player = buildPlayer()
        player!!.addListener(makePlayerListener { onPlayerError(it) })
        binding.playerView.player = player

        if (streamUrl.isEmpty()) { Toast.makeText(this, "URL inválida", Toast.LENGTH_SHORT).show(); finish(); return }
        playUrl(streamUrl)

        binding.ivBack.setOnClickListener { finish() }
        binding.playerView.setOnClickListener {
            val vis = binding.llOverlay.visibility
            binding.llOverlay.visibility = if (vis == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        // Try alternative extension
        if (streamUrl.endsWith(".ts")) {
            val altUrl = streamUrl.replace(".ts", ".m3u8")
            Toast.makeText(this, "Tentando formato alternativo...", Toast.LENGTH_SHORT).show()
            playUrl(altUrl)
        } else {
            Toast.makeText(this, "Erro ao reproduzir: ${error.message}", Toast.LENGTH_LONG).show()
        }
    }
}
