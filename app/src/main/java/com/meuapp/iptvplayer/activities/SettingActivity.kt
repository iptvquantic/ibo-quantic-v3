package com.meuapp.iptvplayer.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.meuapp.iptvplayer.MainActivity
import com.meuapp.iptvplayer.databinding.ActivitySettingBinding
import com.meuapp.iptvplayer.helper.PreferenceHelper

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivBack.setOnClickListener { finish() }

        binding.tvServerUrl.text = PreferenceHelper.getServerUrl(this)
        binding.tvUsername.text = PreferenceHelper.getUsername(this)
        binding.tvPlaylistName.text = PreferenceHelper.getPlaylistName(this)

        binding.btnChangePlaylist.setOnClickListener {
            startActivity(Intent(this, ChangePlaylistActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Sair")
                .setMessage("Deseja remover a lista atual e voltar ao início?")
                .setPositiveButton("Sim") { _, _ ->
                    PreferenceHelper.clearAll(this)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}
