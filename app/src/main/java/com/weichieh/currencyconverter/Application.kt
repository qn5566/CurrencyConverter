package com.weichieh.currencyconverter

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * The main application class for the Currency Converter app.
 * This class extends the Android Application class and is annotated with @HiltAndroidApp to enable
 * Hilt dependency injection. It also implements Configuration.Provider to provide configuration
 * for WorkManager.
 *
 * @constructor Creates a new instance of the Application class
 */
@HiltAndroidApp
class Application : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    /**
     * Provides configuration for WorkManager.
     *
     * @return The configuration for WorkManager
     */
    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
}