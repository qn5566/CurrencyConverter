package com.weichieh.currencyconverter

import androidx.annotation.VisibleForTesting
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.weichieh.currencyconverter.data.local.dao.CurrencyDao
import com.weichieh.currencyconverter.data.model.Currency
import com.weichieh.currencyconverter.data.remote.ApiService
import com.weichieh.currencyconverter.data.repo.RepositoryHelper
import com.weichieh.currencyconverter.data.repo.RepositoryImpl
import com.weichieh.currencyconverter.ui.MainViewModel
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MainViewModeTest {

    private val testDispatcher = TestCoroutineDispatcher()
    lateinit var mainViewModel: MainViewModel
    lateinit var mainRepository: RepositoryHelper

    @Mock
    lateinit var apiService: ApiService

    @Mock
    lateinit var currencyDao: CurrencyDao

    var c1 = Currency(0, "usd", 1.0)
    var c2 = Currency(0, "twd", 30.432499)
    var c3 = Currency(0, "jpy", 131.987)

    var currencyList = listOf(c1, c2, c3)

    @get:Rule
    val instantTaskExecutionRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
        mainRepository = RepositoryImpl(apiService, currencyDao)
        mainViewModel = MainViewModel(mainRepository)
    }

    /**
     * Unit test for [MainViewModel.getAllCurrencies] function.
     * It verifies that the ViewModel properly retrieves a list of currencies from the repository.
     */
    @Test
    fun getAllCurrencies() {
        runBlocking {
            // Mock the behavior of the repository to return a pre-defined list of currencies
            Mockito.`when`(mainRepository.getAllCurrenciesLocally())
                .thenReturn(currencyList)
            // Call the function to retrieve currencies from the ViewModel
            mainViewModel.getRates()
            // Retrieve the result and compare it with the expected value
            val result = mainViewModel.localRespone.getOrAwaitValue()
            assertEquals(
                listOf(
                    Currency(0, "usd", 1.0),
                    Currency(0, "twd", 30.432499), Currency(0, "jpy", 131.987)
                ), result
            )
        }
    }

    /**
     * Test case for when the currency list is empty. Verifies that the localRespone LiveData
     * is updated correctly after calling getRates() in the MainViewModel.
     *
     * @throws AssertionError if the expected and actual List<Currency> values do not match
     */
    @Test
    fun `empty currency list test`() {
        runBlocking {
            Mockito.`when`(mainRepository.getAllCurrenciesLocally())
                .thenReturn(emptyList())
            mainViewModel.getRates()
            val result = mainViewModel.localRespone.getOrAwaitValue()
            assertEquals(listOf<Currency>(), result)
        }
    }

    /**
     * A utility function for testing LiveData values. This function allows you to wait for a LiveData
     * value to be set, without blocking the main thread.
     *
     * @param time The amount of time to wait for the LiveData value to be set
     * @param timeUnit The unit of time for the waiting time
     * @param afterObserve A lambda function that is executed after the LiveData is observed. This can be used
     * for additional setup actions before waiting for the value
     * @return The value of the LiveData object
     * @throws TimeoutException if the LiveData value is never set within the specified time
     */
    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
        afterObserve: () -> Unit = {}
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data = o
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }
        this.observeForever(observer)
        try {
            afterObserve.invoke()
            if (!latch.await(time, timeUnit)) {
                throw TimeoutException("LiveData value was never set.")
            }
        } finally {
            this.removeObserver(observer)
        }
        @Suppress("UNCHECKED_CAST")
        return data as T
    }
}