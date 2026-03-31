package com.meuapp.iptvplayer.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.meuapp.iptvplayer.adapter.EpgAdapter
import com.meuapp.iptvplayer.apps.Constants
import com.meuapp.iptvplayer.databinding.ActivityCatchUpBinding
import com.meuapp.iptvplayer.helper.IptvRepository
import com.meuapp.iptvplayer.helper.PreferenceHelper
import com.meuapp.iptvplayer.helper.Result
import com.meuapp.iptvplayer.models.CatchUpEpg
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CatchUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatchUpBinding
    private lateinit var repo: IptvRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCatchUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repo = IptvRepository(this)

        val streamId = intent.getIntExtra(Constants.EXTRA_STREAM_ID, 0)
        val channelName = intent.getStringExtra(Constants.EXTRA_STREAM_NAME) ?: ""
        val channelIcon = intent.getStringExtra(Constants.EXTRA_STREAM_ICON) ?: ""

        binding.tvChannelName.text = channelName
        if (channelIcon.isNotEmpty()) Glide.with(this).load(channelIcon).into(binding.ivIcon)
        binding.ivBack.setOnClickListener { finish() }

        if (streamId > 0) loadEpg(streamId)
    }

    private fun loadEpg(streamId: Int) {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            when (val r = repo.getCatchUpData(streamId)) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val epgList = r.data.epgListings ?: emptyList()
                    if (epgList.isEmpty()) {
                        binding.tvEmpty.visibility = View.VISIBLE
                        return@launch
                    }
                    val adapter = EpgAdapter(epgList) { epg -> openCatchUpPlayer(epg, streamId) }
                    binding.rvEpg.layoutManager = LinearLayoutManager(this@CatchUpActivity)
                    binding.rvEpg.adapter = adapter
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                    Toast.makeText(this@CatchUpActivity, "Catch-up não disponível para este canal", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openCatchUpPlayer(epg: CatchUpEpg, streamId: Int) {
        val base = PreferenceHelper.getServerUrl(this).trimEnd('/')
        val u = PreferenceHelper.getUsername(this); val p = PreferenceHelper.getPassword(this)
        val start = epg.startTimestamp ?: "0"
        val end = epg.stopTimestamp ?: "0"
        val url = "$base/timeshift/$u/$p/${computeDuration(start, end)}/$start/$streamId.ts"
        Intent(this, CatchUpPlayerActivity::class.java).apply {
            putExtra(Constants.EXTRA_STREAM_URL, url)
            putExtra(Constants.EXTRA_STREAM_NAME, epg.title ?: "CatchUp")
            startActivity(this)
        }
    }

    private fun computeDuration(start: String, end: String): Int {
        return try {
            val s = start.toLong(); val e = end.toLong()
            ((e - s) / 60).toInt().coerceAtLeast(1)
        } catch (ex: Exception) { 120 }
    }
}
