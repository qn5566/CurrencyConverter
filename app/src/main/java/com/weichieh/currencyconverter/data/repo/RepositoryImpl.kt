package com.weichieh.currencyconverter.data.repo

import com.weichieh.currencyconverter.data.local.dao.CurrencyDao
import com.weichieh.currencyconverter.data.model.APIResponse
import com.weichieh.currencyconverter.data.model.Currency
import com.weichieh.currencyconverter.data.remote.ApiService
import retrofit2.Response
import javax.inject.Inject

/**
 * Implementation of [RepositoryHelper] that provides an implementation of all the functions in the interface.
 * It uses [ApiService] for making network calls and [CurrencyDao] for performing database operations.
 *
 * @param apiService The service that provides network calls
 * @param currencyDao The DAO that provides access to the database operations
 */
class RepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val currencyDao: CurrencyDao
) : RepositoryHelper {

    /**
     * Retrieves live exchange rates from the API.
     *
     *  @param apiKey The API key required for making the network call
     *  @return A response object containing the API response
     */
    override suspend fun getLiveRates(apiKey: String): Response<APIResponse> =
        apiService.getLiveRates(apiKey)

    /**
     * Deletes all currency data from the local database.
     */
    override suspend fun deleteAllCurrencies() {
        currencyDao.deleteAllCurrencies()
    }

    /**
     * Inserts a list of currency data to the local database.
     *
     * @param currencyList The list of currency data to be inserted
     */
    override suspend fun insertAll(currencyList: List<Currency>) {
        currencyDao.insertAll(currencyList)
    }

    /**
     * Inserts a single currency data to the local database.
     *
     * @param currency The currency data to be inserted
     */
    override suspend fun insert(currency: Currency) {
        currencyDao.insert(currency)
    }

    /**
     * Retrieves all currency data from the local database.
     *
     * @return A list of currency data objects
     */
    override suspend fun getAllCurrenciesLocally(): List<Currency> {
        return currencyDao.getAllCurrenciesLocally()
    }
}