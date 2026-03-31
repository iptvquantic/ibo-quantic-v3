package com.meuapp.iptvplayer.apps

import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.datasource.okhttp.OkHttpDataSource
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

abstract class BaseActivity : AppCompatActivity() {

    protected var player: ExoPlayer? = null

    protected fun buildPlayer(): ExoPlayer {
        val trustAll = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(c: Array<X509Certificate>, a: String) {}
            override fun checkServerTrusted(c: Array<X509Certificate>, a: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
        val sslCtx = SSLContext.getInstance("SSL").apply { init(null, trustAll, SecureRandom()) }
        val okHttp = OkHttpClient.Builder()
            .sslSocketFactory(sslCtx.socketFactory, trustAll[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()
        val dsFactory = OkHttpDataSource.Factory(okHttp)
        return ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dsFactory))
            .build()
    }

    protected fun playUrl(url: String) {
        player?.let {
            it.setMediaItem(MediaItem.fromUri(url))
            it.prepare()
            it.playWhenReady = true
        }
    }

    protected open fun onPlayerError(error: PlaybackException) {}

    protected fun makePlayerListener(onError: (PlaybackException) -> Unit): Player.Listener =
        object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) = onError(error)
        }

    override fun onResume() { super.onResume(); player?.play() }
    override fun onPause() { super.onPause(); player?.pause() }

    override fun onDestroy() {
        player?.release()
        player = null
        super.onDestroy()
    }

    protected fun keepScreenOn() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    protected fun fullscreen() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}
