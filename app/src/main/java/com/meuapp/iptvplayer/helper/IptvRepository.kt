package com.meuapp.iptvplayer.helper

import android.content.Context
import com.meuapp.iptvplayer.models.*
import com.meuapp.iptvplayer.remote.RetroClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

class IptvRepository(private val context: Context) {
    private val prefs = PreferenceHelper
    private val apiUrl get() = prefs.buildPlayerApiUrl(context)
    private val user get() = prefs.getUsername(context)
    private val pass get() = prefs.getPassword(context)
    private val api get() = RetroClass.getApiService(prefs.getServerUrl(context))

    suspend fun login(): Result<LoginModel> = withContext(Dispatchers.IO) {
        try {
            val r = api.authenticate(apiUrl, user, pass)
            if (r.isSuccessful && r.body() != null) Result.Success(r.body()!!)
            else Result.Error("Erro de autenticação: ${r.code()}")
        } catch (e: Exception) { Result.Error(e.message ?: "Erro de rede") }
    }

    suspend fun getLiveCategories(): Result<List<CategoryModel>> = withContext(Dispatchers.IO) {
        try {
            val r = api.getLiveCategories(apiUrl, user, pass)
            if (r.isSuccessful) Result.Success(r.body() ?: emptyList())
            else Result.Error("Erro ${r.code()}")
        } catch (e: Exception) { Result.Error(e.message ?: "Erro") }
    }

    suspend fun getLiveStreams(categoryId: String? = null): Result<List<LiveChannelModel>> = withContext(Dispatchers.IO) {
        try {
            val r = if (categoryId != null) api.getLiveStreamsByCategory(apiUrl, user, pass, categoryId = categoryId)
                    else api.getLiveStreams(apiUrl, user, pass)
            if (r.isSuccessful) Result.Success(r.body() ?: emptyList())
            else Result.Error("Erro ${r.code()}")
        } catch (e: Exception) { Result.Error(e.message ?: "Erro") }
    }

    suspend fun getVodCategories(): Result<List<CategoryModel>> = withContext(Dispatchers.IO) {
        try {
            val r = api.getVodCategories(apiUrl, user, pass)
            if (r.isSuccessful) Result.Success(r.body() ?: emptyList())
            else Result.Error("Erro ${r.code()}")
        } catch (e: Exception) { Result.Error(e.message ?: "Erro") }
    }

    suspend fun getMovies(categoryId: String? = null): Result<List<MovieModel>> = withContext(Dispatchers.IO) {
        try {
            val r = if (categoryId != null) api.getVodStreamsByCategory(apiUrl, user, pass, categoryId = categoryId)
                    else api.getVodStreams(apiUrl, user, pass)
            if (r.isSuccessful) Result.Success(r.body() ?: emptyList())
            else Result.Error("Erro ${r.code()}")
        } catch (e: Exception) { Result.Error(e.message ?: "Erro") }
    }

    suspend fun getMovieInfo(vodId: Int): Result<MovieInfoResponse> = withContext(Dispatchers.IO) {
        try {
            val r = api.getVodInfo(apiUrl, user, pass, vodId = vodId)
            if (r.isSuccessful && r.body() != null) Result.Success(r.body()!!)
            else Result.Error("Erro ${r.code()}")
        } catch (e: Exception) { Result.Error(e.message ?: "Erro") }
    }

    suspend fun getSeriesCategories(): Result<List<CategoryModel>> = withContext(Dispatchers.IO) {
        try {
            val r = api.getSeriesCategories(apiUrl, user, pass)
            if (r.isSuccessful) Result.Success(r.body() ?: emptyList())
            else Result.Error("Erro ${r.code()}")
        } catch (e: Exception) { Result.Error(e.message ?: "Erro") }
    }

    suspend fun getSeries(categoryId: String? = null): Result<List<SeriesModel>> = withContext(Dispatchers.IO) {
        try {
            val r = if (categoryId != null) api.getSeriesByCategory(apiUrl, user, pass, categoryId = categoryId)
                    else api.getSeries(apiUrl, user, pass)
            if (r.isSuccessful) Result.Success(r.body() ?: emptyList())
            else Result.Error("Erro ${r.code()}")
        } catch (e: Exception) { Result.Error(e.message ?: "Erro") }
    }

    suspend fun getSeriesInfo(seriesId: Int): Result<SeriesInfoResponse> = withContext(Dispatchers.IO) {
        try {
            val r = api.getSeriesInfo(apiUrl, user, pass, seriesId = seriesId)
            if (r.isSuccessful && r.body() != null) Result.Success(r.body()!!)
            else Result.Error("Erro ${r.code()}")
        } catch (e: Exception) { Result.Error(e.message ?: "Erro") }
    }

    suspend fun getEpg(streamId: Int): Result<CatchUpEpgResponse> = withContext(Dispatchers.IO) {
        try {
            val r = api.getShortEpg(apiUrl, user, pass, streamId = streamId)
            if (r.isSuccessful && r.body() != null) Result.Success(r.body()!!)
            else Result.Error("Erro ${r.code()}")
        } catch (e: Exception) { Result.Error(e.message ?: "Erro") }
    }

    suspend fun getCatchUpData(streamId: Int): Result<CatchUpEpgResponse> = withContext(Dispatchers.IO) {
        try {
            val r = api.getSimpleDataTable(apiUrl, user, pass, streamId = streamId)
            if (r.isSuccessful && r.body() != null) Result.Success(r.body()!!)
            else Result.Error("Erro ${r.code()}")
        } catch (e: Exception) { Result.Error(e.message ?: "Erro") }
    }
}
