package com.arctouch.codechallenge

import android.app.Application
import com.arctouch.codechallenge.di.appModule
import com.arctouch.codechallenge.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MoviesApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val modulesList = listOf(networkModule, appModule)

        startKoin {
            //Koin logguer
            androidLogger(Level.NONE)
            //Declare context
            androidContext(this@MoviesApplication)

            //modules
            modules(modulesList)

        }
    }
}