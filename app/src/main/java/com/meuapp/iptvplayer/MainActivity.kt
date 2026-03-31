package com.meuapp.iptvplayer

import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.meuapp.iptvplayer.activities.HomeActivity
import com.meuapp.iptvplayer.databinding.ActivityMainBinding
import com.meuapp.iptvplayer.helper.MacAuthHelper
import com.meuapp.iptvplayer.helper.PreferenceHelper
import kotlinx.coroutines.launch
import java.net.NetworkInterface

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var macAddress: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        macAddress = getMacAddress()
        binding.tvMacAddress.text = macAddress
        binding.tvDeviceKey.text = getDeviceKey()

        // If already authenticated, go home
        if (PreferenceHelper.isAuthenticated(this)) {
            goHome()
            return
        }

        binding.btnActivate.setOnClickListener {
            checkMac()
        }
    }

    private fun checkMac() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnActivate.isEnabled = false
        binding.tvStatus.text = "Verificando dispositivo..."
        binding.tvStatus.visibility = View.VISIBLE

        lifecycleScope.launch {
            val result = MacAuthHelper.checkMac(macAddress)
            binding.progressBar.visibility = View.GONE
            binding.btnActivate.isEnabled = true

            when (result.status) {
                "active" -> {
                    PreferenceHelper.saveAuthData(
                        context = this@MainActivity,
                        mac = macAddress,
                        dns = result.dns ?: "",
                        username = result.username ?: "",
                        password = result.password ?: "",
                        clientName = result.clientName ?: ""
                    )
                    goHome()
                }
                "not_found" -> {
                    binding.tvStatus.text = "❌ Dispositivo não cadastrado.\nEntre em contato com seu revendedor e informe o MAC acima."
                    binding.tvStatus.setTextColor(getColor(android.R.color.holo_red_light))
                }
                "blocked" -> {
                    binding.tvStatus.text = "🚫 Dispositivo bloqueado.\nEntre em contato com seu revendedor."
                    binding.tvStatus.setTextColor(getColor(android.R.color.holo_red_light))
                }
                "expired" -> {
                    binding.tvStatus.text = "⏰ ${result.message}"
                    binding.tvStatus.setTextColor(getColor(android.R.color.holo_orange_light))
                }
                else -> {
                    binding.tvStatus.text = "⚠️ Erro de conexão. Verifique sua internet."
                    binding.tvStatus.setTextColor(getColor(android.R.color.holo_orange_light))
                }
            }
        }
    }

    private fun getMacAddress(): String {
        try {
            // Try WiFi MAC
            val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            val mac = wifiInfo?.macAddress
            if (!mac.isNullOrEmpty() && mac != "02:00:00:00:00:00") {
                return mac.uppercase()
            }
        } catch (e: Exception) {}

        try {
            // Try network interface
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val ni = interfaces.nextElement()
                if (ni.name.contains("wlan") || ni.name.contains("eth")) {
                    val mac = ni.hardwareAddress
                    if (mac != null && mac.size >= 6) {
                        return mac.joinToString(":") { "%02X".format(it) }
                    }
                }
            }
        } catch (e: Exception) {}

        // Fallback: use Android ID as fake MAC
        val androidId = android.provider.Settings.Secure.getString(
            contentResolver, android.provider.Settings.Secure.ANDROID_ID
        ) ?: "000000000000"
        val id = androidId.padEnd(12, '0').take(12).uppercase()
        return id.chunked(2).joinToString(":")
    }

    private fun getDeviceKey(): String {
        val androidId = android.provider.Settings.Secure.getString(
            contentResolver, android.provider.Settings.Secure.ANDROID_ID
        ) ?: "000000"
        return androidId.take(6).uppercase()
    }

    private fun goHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
