package com.meuapp.iptvplayer.helper

import android.content.Context
import android.content.SharedPreferences
import com.meuapp.iptvplayer.apps.Constants

object PreferenceHelper {
    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE)

    // MAC Auth methods
    fun saveAuthData(ctx: Context, mac: String, dns: String, username: String, password: String, clientName: String) {
        prefs(ctx).edit().apply {
            putString("mac_address", mac)
            putString(Constants.KEY_SERVER_URL, dns)
            putString(Constants.KEY_USERNAME, username)
            putString(Constants.KEY_PASSWORD, password)
            putString(Constants.KEY_PLAYLIST_NAME, clientName)
            putBoolean("is_authenticated", true)
            apply()
        }
    }

    fun isAuthenticated(ctx: Context): Boolean = prefs(ctx).getBoolean("is_authenticated", false)
    fun getMacAddress(ctx: Context): String = prefs(ctx).getString("mac_address", "") ?: ""

    // IPTV data
    fun setServerUrl(ctx: Context, url: String) = prefs(ctx).edit().putString(Constants.KEY_SERVER_URL, url).apply()
    fun getServerUrl(ctx: Context): String = prefs(ctx).getString(Constants.KEY_SERVER_URL, "") ?: ""
    fun setUsername(ctx: Context, v: String) = prefs(ctx).edit().putString(Constants.KEY_USERNAME, v).apply()
    fun getUsername(ctx: Context): String = prefs(ctx).getString(Constants.KEY_USERNAME, "") ?: ""
    fun setPassword(ctx: Context, v: String) = prefs(ctx).edit().putString(Constants.KEY_PASSWORD, v).apply()
    fun getPassword(ctx: Context): String = prefs(ctx).getString(Constants.KEY_PASSWORD, "") ?: ""
    fun setPlaylistName(ctx: Context, v: String) = prefs(ctx).edit().putString(Constants.KEY_PLAYLIST_NAME, v).apply()
    fun getPlaylistName(ctx: Context): String = prefs(ctx).getString(Constants.KEY_PLAYLIST_NAME, "IBO QUANTIC") ?: "IBO QUANTIC"

    fun isLoggedIn(ctx: Context): Boolean = isAuthenticated(ctx) && getServerUrl(ctx).isNotEmpty()

    fun clearAll(ctx: Context) = prefs(ctx).edit().clear().apply()

    fun buildPlayerApiUrl(ctx: Context): String {
        val base = getServerUrl(ctx).trimEnd('/')
        return "$base/player_api.php"
    }
    fun buildStreamUrl(ctx: Context, streamId: Int, ext: String = "ts"): String {
        val base = getServerUrl(ctx).trimEnd('/')
        val u = getUsername(ctx); val p = getPassword(ctx)
        return "$base/live/$u/$p/$streamId.$ext"
    }
    fun buildMovieUrl(ctx: Context, streamId: Int, ext: String = "mp4"): String {
        val base = getServerUrl(ctx).trimEnd('/')
        val u = getUsername(ctx); val p = getPassword(ctx)
        return "$base/movie/$u/$p/$streamId.$ext"
    }
    fun buildEpisodeUrl(ctx: Context, streamId: String, ext: String = "mp4"): String {
        val base = getServerUrl(ctx).trimEnd('/')
        val u = getUsername(ctx); val p = getPassword(ctx)
        return "$base/series/$u/$p/$streamId.$ext"
    }
}
