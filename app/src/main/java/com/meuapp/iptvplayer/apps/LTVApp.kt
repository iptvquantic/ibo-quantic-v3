package com.meuapp.iptvplayer.apps

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.meuapp.iptvplayer.helper.AppDatabase

class LTVApp : Application() {
    companion object {
        lateinit var instance: LTVApp private set
        lateinit var database: AppDatabase private set
        fun getAppContext(): Context = instance.applicationContext
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "iboquantic_db")
            .fallbackToDestructiveMigration().build()
    }
}
