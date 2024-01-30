package com.weichieh.currencyconverter.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.work.*
import com.weichieh.currencyconverter.R
import com.weichieh.currencyconverter.databinding.ActivityMainBinding
import com.weichieh.currencyconverter.worker.BackgroundWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

/**
 * The MainActivity is the main entry point of the application. It sets up the background worker
 * to periodically fetch exchange rate data from the API.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var workManager: WorkManager
    private lateinit var periodicRequest: PeriodicWorkRequest

    private val fragment by lazy {
        MainFragment.newInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setUpWorker(this)

        supportFragmentManager.beginTransaction().replace(R.id.frag_content, fragment).commit()
    }

    fun setUpWorker(context: Context) {
        // The setConstraints method is used to specify the constraints that must be met
        // before the work request can be executed. In this case, it is set to require a network connection.
        val constraints = Constraints.Builder().run {
            setRequiredNetworkType(NetworkType.CONNECTED)
            build()
        }

        // Creates a periodic work request that runs the BackgroundWorker every 28 minutes
        // with the specified constraints and tag.
        periodicRequest = PeriodicWorkRequestBuilder<BackgroundWorker>(28, TimeUnit.MINUTES)
            .addTag("WorkingBackgroud")
            .setConstraints(constraints)
            .build()

        workManager = WorkManager.getInstance(context)

        // Once the work request is defined, it is passed to the enqueueUniquePeriodicWork
        // method of the WorkManager class. The enqueueUniquePeriodicWork method is used to
        // schedule the work request to run periodically with a unique name.
        // The ExistingPeriodicWorkPolicy parameter is used to specify what should happen
        // if the same work request is already scheduled.
        workManager?.enqueueUniquePeriodicWork(
            "Periodic_Fetch_Request",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest!!
        )
    }
}