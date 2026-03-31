package com.meuapp.iptvplayer.helper

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

data class AuthResult(
    val status: String = "error",
    val message: String = "",
    val mac: String = "",
    val clientName: String? = null,
    val dns: String? = null,
    val username: String? = null,
    val password: String? = null,
    val expiresAt: String? = null
)

object MacAuthHelper {

    // URL do seu painel — já configurado!
    private const val API_URL = "https://ibo-quantic-painel-gsg5.vercel.app/api/mac/check"

    suspend fun checkMac(mac: String): AuthResult = withContext(Dispatchers.IO) {
        try {
            val cleanMac = mac.replace(":", "")
            val url = "$API_URL?mac=$cleanMac"
            val response = URL(url).readText(Charsets.UTF_8)
            val map = Gson().fromJson(response, Map::class.java)
            AuthResult(
                status = map["status"]?.toString() ?: "error",
                message = map["message"]?.toString() ?: "",
                mac = map["mac"]?.toString() ?: mac,
                clientName = map["client_name"]?.toString(),
                dns = map["dns"]?.toString(),
                username = map["username"]?.toString(),
                password = map["password"]?.toString(),
                expiresAt = map["expires_at"]?.toString()
            )
        } catch (e: Exception) {
            AuthResult(status = "error", message = "Sem conexão com o servidor: ${e.message}")
        }
    }
}
