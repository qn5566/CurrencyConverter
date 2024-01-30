package com.weichieh.currencyconverter.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weichieh.currencyconverter.data.model.Currency
import com.weichieh.currencyconverter.data.repo.RepositoryHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(private val repositoryHelper: RepositoryHelper) :
    ViewModel() {

    private val _localResponse = MutableLiveData<List<Currency>>()

    val localRespone: LiveData<List<Currency>>
        get() = _localResponse

    init {
        getRates()
        repeatFun()
    }

    /**
     * This function is used to retrieve the currency rates from a local data source asynchronously using Coroutines.
     * The function gets all currencies from the local database and posts the result to the [_localResponse] LiveData object.
     */
    fun getRates() {
        viewModelScope.launch {
            var currencies = repositoryHelper.getAllCurrenciesLocally()
            _localResponse.postValue(currencies)

            currencies?.let {
                Log.d("MainViewModel", it.toString())
            }
        }
    }

    /**
     * The purpose of this function is to prevent frequent and unnecessary calls to the API,
     * which can be resource-intensive and slow down the app.
     */
    private fun getRatesHelper() = viewModelScope.launch {
        delay(8000)
        getRates()
    }


    /**
     * A helper function that creates a coroutine job that runs in the IO dispatcher
     * and repeats the [getRatesHelper] function every 29 minutes until the coroutine job is cancelled.
     *
     * @return A [Job] object representing the coroutine job created by this function
     */
    private fun repeatFun(): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                withContext(Dispatchers.IO + NonCancellable) {
                    getRatesHelper()
                    delay(1740000)
                }
            }
        }
    }

}