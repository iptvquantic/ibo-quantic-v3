package com.meuapp.iptvplayer.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.meuapp.iptvplayer.adapter.PlaylistAdapter
import com.meuapp.iptvplayer.apps.LTVApp
import com.meuapp.iptvplayer.databinding.ActivityChangePlaylistBinding
import com.meuapp.iptvplayer.helper.PreferenceHelper
import com.meuapp.iptvplayer.models.ServerModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangePlaylistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePlaylistBinding
    private lateinit var adapter: PlaylistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivBack.setOnClickListener { finish() }

        adapter = PlaylistAdapter(
            onSelect = { server -> activateServer(server) },
            onDelete = { server -> confirmDelete(server) }
        )
        binding.rvPlaylists.layoutManager = LinearLayoutManager(this)
        binding.rvPlaylists.adapter = adapter

        binding.btnAddNew.setOnClickListener { showAddDialog() }

        lifecycleScope.launch {
            LTVApp.database.serverDao().getAll().collectLatest { list ->
                adapter.submitList(list)
                binding.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showAddDialog() {
        val dialogBinding = layoutInflater.inflate(com.meuapp.iptvplayer.R.layout.dialog_add_playlist, null)
        AlertDialog.Builder(this)
            .setTitle("➕ Adicionar Playlist")
            .setView(dialogBinding)
            .setPositiveButton("Salvar") { _, _ ->
                val name = dialogBinding.findViewById<android.widget.EditText>(com.meuapp.iptvplayer.R.id.etName).text.toString().trim()
                val url = dialogBinding.findViewById<android.widget.EditText>(com.meuapp.iptvplayer.R.id.etUrl).text.toString().trim()
                val user = dialogBinding.findViewById<android.widget.EditText>(com.meuapp.iptvplayer.R.id.etUser).text.toString().trim()
                val pass = dialogBinding.findViewById<android.widget.EditText>(com.meuapp.iptvplayer.R.id.etPass).text.toString().trim()
                if (name.isEmpty() || url.isEmpty() || user.isEmpty()) {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val cleanUrl = if (url.startsWith("http")) url else "http://$url"
                lifecycleScope.launch(Dispatchers.IO) {
                    LTVApp.database.serverDao().insert(ServerModel(name = name, url = cleanUrl, username = user, password = pass))
                    withContext(Dispatchers.Main) { Toast.makeText(this@ChangePlaylistActivity, "Playlist adicionada!", Toast.LENGTH_SHORT).show() }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun activateServer(server: ServerModel) {
        lifecycleScope.launch(Dispatchers.IO) {
            LTVApp.database.serverDao().clearActive()
            LTVApp.database.serverDao().setActive(server.id)
            PreferenceHelper.setServerUrl(this@ChangePlaylistActivity, server.url)
            PreferenceHelper.setUsername(this@ChangePlaylistActivity, server.username)
            PreferenceHelper.setPassword(this@ChangePlaylistActivity, server.password)
            PreferenceHelper.setPlaylistName(this@ChangePlaylistActivity, server.name)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ChangePlaylistActivity, "✅ Ativado: ${server.name}", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun confirmDelete(server: ServerModel) {
        AlertDialog.Builder(this)
            .setTitle("Remover")
            .setMessage("Remover \"${server.name}\"?")
            .setPositiveButton("Remover") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) { LTVApp.database.serverDao().delete(server) }
            }
            .setNegativeButton("Cancelar", null).show()
    }
}
