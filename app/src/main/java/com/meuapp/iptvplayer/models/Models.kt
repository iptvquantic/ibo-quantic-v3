package com.meuapp.iptvplayer.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

// ─── SERVER / PLAYLIST ───────────────────────────────────────────────────────

@Entity(tableName = "servers")
data class ServerModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val url: String = "",
    val username: String = "",
    val password: String = "",
    val isActive: Boolean = false
)

// ─── LOGIN ────────────────────────────────────────────────────────────────────

data class LoginModel(
    @SerializedName("user_info") val userInfo: UserInfo? = null,
    @SerializedName("server_info") val serverInfo: ServerInfo? = null
)

data class UserInfo(
    @SerializedName("username") val username: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("exp_date") val expDate: String? = null,
    @SerializedName("max_connections") val maxConnections: String? = null,
    @SerializedName("active_cons") val activeCons: String? = null
)

data class ServerInfo(
    @SerializedName("url") val url: String? = null,
    @SerializedName("port") val port: String? = null,
    @SerializedName("https_port") val httpsPort: String? = null,
    @SerializedName("server_protocol") val serverProtocol: String? = null,
    @SerializedName("rtmp_port") val rtmpPort: String? = null,
    @SerializedName("timezone") val timezone: String? = null,
    @SerializedName("timestamp_now") val timestampNow: Long? = null
)

data class LoginResponse(
    val userInfo: UserInfo? = null,
    val serverInfo: ServerInfo? = null
)

// ─── CATEGORY ─────────────────────────────────────────────────────────────────

@Parcelize
data class CategoryModel(
    @SerializedName("category_id") val categoryId: String = "",
    @SerializedName("category_name") val categoryName: String = "",
    @SerializedName("parent_id") val parentId: Int = 0
) : Parcelable

// ─── LIVE CHANNELS ────────────────────────────────────────────────────────────

@Parcelize
data class LiveChannelModel(
    @SerializedName("num") val num: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("stream_type") val streamType: String = "",
    @SerializedName("stream_id") val streamId: Int = 0,
    @SerializedName("stream_icon") val streamIcon: String = "",
    @SerializedName("epg_channel_id") val epgChannelId: String? = null,
    @SerializedName("added") val added: String? = null,
    @SerializedName("category_id") val categoryId: String = "",
    @SerializedName("custom_sid") val customSid: String? = null,
    @SerializedName("tv_archive") val tvArchive: Int = 0,
    @SerializedName("direct_source") val directSource: String? = null,
    @SerializedName("tv_archive_duration") val tvArchiveDuration: Int = 0
) : Parcelable

// ─── VOD / MOVIES ─────────────────────────────────────────────────────────────

@Parcelize
data class MovieModel(
    @SerializedName("num") val num: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("stream_type") val streamType: String = "",
    @SerializedName("stream_id") val streamId: Int = 0,
    @SerializedName("stream_icon") val streamIcon: String = "",
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("rating_5based") val rating5based: Float = 0f,
    @SerializedName("added") val added: String? = null,
    @SerializedName("category_id") val categoryId: String = "",
    @SerializedName("container_extension") val containerExtension: String = "",
    @SerializedName("custom_sid") val customSid: String? = null,
    @SerializedName("direct_source") val directSource: String? = null
) : Parcelable

data class MovieInfoResponse(
    @SerializedName("info") val info: MovieInfo? = null,
    @SerializedName("movie_data") val movieData: MovieData? = null
)

data class MovieInfo(
    @SerializedName("kinopoisk_url") val kinopoiskUrl: String? = null,
    @SerializedName("tmdb_id") val tmdbId: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("o_name") val oName: String? = null,
    @SerializedName("cover_big") val coverBig: String? = null,
    @SerializedName("movie_image") val movieImage: String? = null,
    @SerializedName("releasedate") val releaseDate: String? = null,
    @SerializedName("episode_run_time") val episodeRunTime: String? = null,
    @SerializedName("youtube_trailer") val youtubeTrailer: String? = null,
    @SerializedName("director") val director: String? = null,
    @SerializedName("actors") val actors: String? = null,
    @SerializedName("cast") val cast: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("plot") val plot: String? = null,
    @SerializedName("age") val age: String? = null,
    @SerializedName("country") val country: String? = null,
    @SerializedName("genre") val genre: String? = null,
    @SerializedName("backdrop_path") val backdropPath: List<String>? = null,
    @SerializedName("duration_secs") val durationSecs: Int? = null,
    @SerializedName("duration") val duration: String? = null,
    @SerializedName("video") val video: Map<String, String>? = null,
    @SerializedName("audio") val audio: Map<String, String>? = null,
    @SerializedName("bitrate") val bitrate: Int? = null,
    @SerializedName("rating") val rating: String? = null
)

data class MovieData(
    @SerializedName("stream_id") val streamId: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("added") val added: String = "",
    @SerializedName("category_id") val categoryId: String = "",
    @SerializedName("container_extension") val containerExtension: String = "",
    @SerializedName("custom_sid") val customSid: String = "",
    @SerializedName("direct_source") val directSource: String = ""
)

// ─── SERIES ───────────────────────────────────────────────────────────────────

@Parcelize
data class SeriesModel(
    @SerializedName("num") val num: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("series_id") val seriesId: Int = 0,
    @SerializedName("cover") val cover: String = "",
    @SerializedName("plot") val plot: String? = null,
    @SerializedName("cast") val cast: String? = null,
    @SerializedName("director") val director: String? = null,
    @SerializedName("genre") val genre: String? = null,
    @SerializedName("releaseDate") val releaseDate: String? = null,
    @SerializedName("last_modified") val lastModified: String? = null,
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("rating_5based") val rating5based: Float = 0f,
    @SerializedName("backdrop_path") val backdropPath: List<String>? = null,
    @SerializedName("youtube_trailer") val youtubeTrailer: String? = null,
    @SerializedName("episode_run_time") val episodeRunTime: String? = null,
    @SerializedName("category_id") val categoryId: String = ""
) : Parcelable

data class SeriesInfoResponse(
    @SerializedName("info") val info: InfoSerie? = null,
    @SerializedName("episodes") val episodes: Map<String, List<Episode>>? = null,
    @SerializedName("seasons") val seasons: List<Season>? = null
)

data class InfoSerie(
    @SerializedName("name") val name: String? = null,
    @SerializedName("cover") val cover: String? = null,
    @SerializedName("plot") val plot: String? = null,
    @SerializedName("cast") val cast: String? = null,
    @SerializedName("director") val director: String? = null,
    @SerializedName("genre") val genre: String? = null,
    @SerializedName("releaseDate") val releaseDate: String? = null,
    @SerializedName("rating") val rating: String? = null,
    @SerializedName("backdrop_path") val backdropPath: List<String>? = null,
    @SerializedName("youtube_trailer") val youtubeTrailer: String? = null,
    @SerializedName("episode_run_time") val episodeRunTime: String? = null
)

@Parcelize
data class Season(
    @SerializedName("air_date") val airDate: String? = null,
    @SerializedName("episode_count") val episodeCount: Int = 0,
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String? = null,
    @SerializedName("overview") val overview: String? = null,
    @SerializedName("season_number") val seasonNumber: Int = 0,
    @SerializedName("cover") val cover: String? = null,
    @SerializedName("cover_big") val coverBig: String? = null
) : Parcelable

@Parcelize
data class Episode(
    @SerializedName("id") val id: String = "",
    @SerializedName("episode_num") val episodeNum: Int = 0,
    @SerializedName("title") val title: String = "",
    @SerializedName("container_extension") val containerExtension: String = "",
    @SerializedName("info") val info: EpisodeInfoModel? = null,
    @SerializedName("custom_sid") val customSid: String? = null,
    @SerializedName("added") val added: String? = null,
    @SerializedName("season") val season: Int = 0,
    @SerializedName("direct_source") val directSource: String? = null
) : Parcelable

@Parcelize
data class EpisodeInfoModel(
    @SerializedName("tmdb_id") val tmdbId: String? = null,
    @SerializedName("releasedate") val releaseDate: String? = null,
    @SerializedName("plot") val plot: String? = null,
    @SerializedName("duration_secs") val durationSecs: Int? = null,
    @SerializedName("duration") val duration: String? = null,
    @SerializedName("movie_image") val movieImage: String? = null,
    @SerializedName("bitrate") val bitrate: Int? = null,
    @SerializedName("rating") val rating: String? = null
) : Parcelable

// ─── EPG ──────────────────────────────────────────────────────────────────────

data class EPGChannel(
    @SerializedName("channel_id") val channelId: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("start") val start: String? = null,
    @SerializedName("end") val end: String? = null,
    @SerializedName("start_timestamp") val startTimestamp: Long = 0L,
    @SerializedName("stop_timestamp") val stopTimestamp: Long = 0L,
    @SerializedName("description") val description: String? = null,
    @SerializedName("lang") val lang: String? = null
)

data class CatchUpEpgResponse(
    @SerializedName("epg_listings") val epgListings: List<CatchUpEpg>? = null
)

data class CatchUpEpg(
    @SerializedName("id") val id: String? = null,
    @SerializedName("epg_id") val epgId: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("lang") val lang: String? = null,
    @SerializedName("start") val start: String? = null,
    @SerializedName("end") val end: String? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("channel_id") val channelId: String? = null,
    @SerializedName("start_timestamp") val startTimestamp: String? = null,
    @SerializedName("stop_timestamp") val stopTimestamp: String? = null,
    @SerializedName("has_archive") val hasArchive: Int = 0
)

// ─── RESUME ───────────────────────────────────────────────────────────────────

@Entity(tableName = "resume_movies")
data class ResumeModel(
    @PrimaryKey val streamId: Int,
    val name: String = "",
    val icon: String = "",
    val position: Long = 0L,
    val duration: Long = 0L,
    val timestamp: Long = 0L
)

@Entity(tableName = "resume_series")
data class ResumeSeriesModel(
    @PrimaryKey val episodeId: String,
    val seriesId: Int = 0,
    val seriesName: String = "",
    val episodeName: String = "",
    val icon: String = "",
    val season: Int = 0,
    val episode: Int = 0,
    val position: Long = 0L,
    val duration: Long = 0L,
    val timestamp: Long = 0L
)
