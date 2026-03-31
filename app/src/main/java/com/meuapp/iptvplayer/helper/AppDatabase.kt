package com.meuapp.iptvplayer.helper

import androidx.room.*
import com.meuapp.iptvplayer.models.ResumeModel
import com.meuapp.iptvplayer.models.ResumeSeriesModel
import com.meuapp.iptvplayer.models.ServerModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {
    @Query("SELECT * FROM servers") fun getAll(): Flow<List<ServerModel>>
    @Query("SELECT * FROM servers WHERE isActive = 1 LIMIT 1") suspend fun getActive(): ServerModel?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(s: ServerModel)
    @Delete suspend fun delete(s: ServerModel)
    @Query("UPDATE servers SET isActive = 0") suspend fun clearActive()
    @Query("UPDATE servers SET isActive = 1 WHERE id = :id") suspend fun setActive(id: Int)
}

@Dao
interface ResumeDao {
    @Query("SELECT * FROM resume_movies ORDER BY timestamp DESC") fun getAll(): Flow<List<ResumeModel>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(r: ResumeModel)
    @Query("SELECT * FROM resume_movies WHERE streamId = :id LIMIT 1") suspend fun get(id: Int): ResumeModel?
    @Query("DELETE FROM resume_movies") suspend fun clearAll()
}

@Dao
interface ResumeSeriesDao {
    @Query("SELECT * FROM resume_series ORDER BY timestamp DESC") fun getAll(): Flow<List<ResumeSeriesModel>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(r: ResumeSeriesModel)
    @Query("SELECT * FROM resume_series WHERE episodeId = :id LIMIT 1") suspend fun get(id: String): ResumeSeriesModel?
    @Query("DELETE FROM resume_series") suspend fun clearAll()
}

@Database(
    entities = [ServerModel::class, ResumeModel::class, ResumeSeriesModel::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serverDao(): ServerDao
    abstract fun resumeDao(): ResumeDao
    abstract fun resumeSeriesDao(): ResumeSeriesDao
}
