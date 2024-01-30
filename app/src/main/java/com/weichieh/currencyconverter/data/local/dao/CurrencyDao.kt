package com.weichieh.currencyconverter.data.local.dao

import com.weichieh.currencyconverter.data.model.Currency
import androidx.room.*

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currencies")
    suspend fun getAllCurrenciesLocally(): List<Currency>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(currency: Currency)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(currencyList: List<Currency>)

    @Query("DELETE FROM currencies")
    suspend fun deleteAllCurrencies()

}