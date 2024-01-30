package com.weichieh.currencyconverter.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.weichieh.currencyconverter.BuildConfig
import com.weichieh.currencyconverter.data.model.Currency
import com.weichieh.currencyconverter.data.model.Rate
import com.weichieh.currencyconverter.data.repo.RepositoryHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * A background worker to fetch currency exchange rates from API and save to local
 * database.
 *
 * @param appContext The application context
 * @param workerParams The worker parameters
 * @param repository The repository helper instance to access the data source
 */
@HiltWorker
class BackgroundWorker @AssistedInject constructor(
    @Assisted appContext: Context, @Assisted workerParams: WorkerParameters,
    private val repository: RepositoryHelper,
) : CoroutineWorker(appContext, workerParams) {

    /**
     * Fetches the currency exchange rates from API and saves to local database.
     *
     * @return The result of the work, either success or failure
     */
    override suspend fun doWork(): Result {

        return try {
            val data = repository.getLiveRates(BuildConfig.APIKEY)?.body()?.rates

            val currencies = data?.let { getCurrenciesFromRate(it) }

            if (currencies != null) {
                repository.deleteAllCurrencies()
                repository.insertAll(currencies)
                Log.d("BackgroundWorker", currencies.toString())
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("Worker_fail", e.toString())
            Result.failure()
        }
    }

    /**
     * Returns a list of Currency objects extracted from a Rate object.
     * The list includes all currencies in the Rate object except USD, and USD is added at the beginning of the list with a value of 1.0.
     *
     * @param rate The rate object to be converted
     * @return A list of currency objects
     */
    private fun getCurrenciesFromRate(rate: Rate): List<Currency> {
        var currencyList: MutableList<Currency> = ArrayList()

        // Extract currencies from the Rate object
        var obj = Gson().toJsonTree(rate).asJsonObject
        for ((key, value) in obj.entrySet()) {
            if (!key.equals("USD", ignoreCase = true)) {
                var currency = Currency(0, key, value.asDouble)
                currencyList.add(currency)
            }
        }
        // Add USD to the beginning of the list with a value of 1.0
        var currency = Currency(0, "USD", 1.0)
        currencyList.add(0, currency)

        return currencyList
    }
}