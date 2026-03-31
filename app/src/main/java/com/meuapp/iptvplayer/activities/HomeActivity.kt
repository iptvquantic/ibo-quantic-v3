package com.meuapp.iptvplayer.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.meuapp.iptvplayer.databinding.ActivityHomeBinding
import com.meuapp.iptvplayer.helper.PreferenceHelper

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvPlaylistName.text = PreferenceHelper.getPlaylistName(this)

        binding.cardLive.setOnClickListener { start(LiveActivity::class.java) }
        binding.cardMovies.setOnClickListener { start(MovieActivity::class.java) }
        binding.cardSeries.setOnClickListener { start(SeriesActivity::class.java) }
        binding.cardSearch.setOnClickListener { start(SearchActivity::class.java) }
        binding.cardSettings.setOnClickListener { start(SettingActivity::class.java) }
        binding.cardChangelist.setOnClickListener { start(ChangePlaylistActivity::class.java) }
    }

    private fun start(cls: Class<*>) = startActivity(Intent(this, cls))
}
