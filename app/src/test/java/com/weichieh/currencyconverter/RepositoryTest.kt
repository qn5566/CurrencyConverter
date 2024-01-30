package com.weichieh.currencyconverter

import com.weichieh.currencyconverter.data.local.dao.CurrencyDao
import com.weichieh.currencyconverter.data.model.APIResponse
import com.weichieh.currencyconverter.data.model.Currency
import com.weichieh.currencyconverter.data.remote.ApiService
import com.weichieh.currencyconverter.data.repo.RepositoryHelper
import com.weichieh.currencyconverter.data.repo.RepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response


@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class RepositoryTest {

    @Mock
    lateinit var apiService: ApiService

    @Mock
    lateinit var currencyDao: CurrencyDao

    val apiKey = "key"

    lateinit var mainRepository: RepositoryHelper

    var c1 = Currency(0, "usd", 1.0)
    var c2 = Currency(0, "twd", 30.432499)
    var c3 = Currency(0, "jpy", 131.987)

    var currencyList = listOf(c1, c2, c3)

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        mainRepository = RepositoryImpl(apiService, currencyDao)
    }

    /**
     * Tests the function of getting currency response from API.
     */
    @Test
    fun `get currency response api test`() {
        runBlocking {
            // uses Mockito framework to mock ApiService and return a success response with a sample APIResponse object.
            Mockito.`when`(apiService.getLiveRates(apiKey))
                .thenReturn(Response.success(APIResponse("time", "usd", null)))

            // the test verifies that the response body matches the expected APIResponse object.
            val response = mainRepository.getLiveRates(apiKey)
            assertEquals(APIResponse("time", "usd", null), response.body())
        }
    }

    @Test
    fun `test for different currency api response`() {
        runBlocking {
            Mockito.`when`(apiService.getLiveRates(apiKey))
                .thenReturn(Response.success(APIResponse("time", "usd", null)))

            val response = mainRepository.getLiveRates(apiKey)
            Assert.assertNotEquals(APIResponse("time", "twd", null), response.body())
        }

    }

    @Test
    fun `get currency list from db test`() {
        runBlocking {
            Mockito.`when`(currencyDao.getAllCurrenciesLocally()).thenReturn(currencyList)

            val result = mainRepository.getAllCurrenciesLocally()
            assertEquals(
                listOf(
                    Currency(0, "usd", 1.0),
                    Currency(0, "twd", 30.432499), Currency(0, "jpy", 131.987)
                ), result
            )
        }
    }
}