package com.weichieh.currencyconverter.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.weichieh.currencyconverter.data.local.dao.CurrencyDao
import com.weichieh.currencyconverter.data.model.Currency

@Database(entities = [Currency::class], version = 1)
abstract class CurrencyDatabase: RoomDatabase() {
    abstract fun getCurrencyDao(): CurrencyDao
}