package com.weichieh.currencyconverter.data.repo

import com.weichieh.currencyconverter.data.model.APIResponse
import com.weichieh.currencyconverter.data.model.Currency
import retrofit2.Response

interface RepositoryHelper {
    /**
     * Retrieves the latest currency exchange rates from the API.
     *
     * @param apiKey The API key to use for the request
     * @return The response containing the currency exchange rates
     */
    suspend fun getLiveRates(apiKey: String): Response<APIResponse>

    /**
     * Deletes all currencies from the local database.
     */
    suspend fun deleteAllCurrencies()

    /**
     * Inserts a list of currencies into the local database.
     *
     * @param currencyList The list of currencies to insert
     */
    suspend fun insertAll(currencyList: List<Currency>)

    /**
     * Inserts a currency into the local database.
     *
     * @param currency The currency to insert
     */
    suspend fun insert(currency: Currency)

    /**
     * Retrieves all currencies stored in the local database.
     *
     * @return A list of all currencies
     */
    suspend fun getAllCurrenciesLocally(): List<Currency>
}