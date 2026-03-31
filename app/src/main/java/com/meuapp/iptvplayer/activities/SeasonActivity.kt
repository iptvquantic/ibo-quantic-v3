package com.meuapp.iptvplayer.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.meuapp.iptvplayer.adapter.EpisodeAdapter
import com.meuapp.iptvplayer.apps.Constants
import com.meuapp.iptvplayer.databinding.ActivitySeasonBinding
import com.meuapp.iptvplayer.helper.PreferenceHelper
import com.meuapp.iptvplayer.models.Episode

class SeasonActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeasonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeasonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val season = intent.getIntExtra(Constants.EXTRA_SEASON_NUM, 1)
        val episodes = intent.getParcelableArrayListExtra<Episode>("episodes") ?: arrayListOf()

        binding.tvTitle.text = "Temporada $season"
        binding.ivBack.setOnClickListener { finish() }

        val adapter = EpisodeAdapter(episodes) { ep ->
            val url = PreferenceHelper.buildEpisodeUrl(this, ep.id, ep.containerExtension)
            Intent(this, SeriesPlayerActivity::class.java).apply {
                putExtra(Constants.EXTRA_STREAM_URL, url)
                putExtra(Constants.EXTRA_STREAM_NAME, ep.title)
                putExtra(Constants.EXTRA_EPISODE, ep)
                startActivity(this)
            }
        }
        binding.rvEpisodes.layoutManager = LinearLayoutManager(this)
        binding.rvEpisodes.adapter = adapter
    }
}
