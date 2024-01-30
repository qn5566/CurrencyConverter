package com.weichieh.currencyconverter.di.module

import android.content.Context
import androidx.room.Room
import com.weichieh.currencyconverter.BuildConfig
import com.weichieh.currencyconverter.data.local.dao.CurrencyDao
import com.weichieh.currencyconverter.data.local.db.CurrencyDatabase
import com.weichieh.currencyconverter.data.remote.ApiService
import com.weichieh.currencyconverter.data.repo.RepositoryHelper
import com.weichieh.currencyconverter.data.repo.RepositoryImpl
import com.weichieh.currencyconverter.utils.NetworkHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * AppModule is a Dagger module that provides dependencies for the Currency
 * Converter application.
 * The dependencies provided by this module include the CurrencyDatabase,
 * CurrencyDao, ApiService, RepositoryHelper, and NetworkHelper.
 * These dependencies are used throughout the application to access and manipulate
 * data related to currency conversion rates.
 *
 * @constructor Creates a new instance of AppModule
 */
@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): CurrencyDatabase {
        return Room.databaseBuilder(
            appContext,
            CurrencyDatabase::class.java,
            "RssReader"
        ).build()
    }

    @Provides
    @Singleton
    fun provideCurrencyDao(appDatabase: CurrencyDatabase): CurrencyDao {
        return appDatabase.getCurrencyDao()
    }

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Singleton
    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideAPIService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideRepository(api: ApiService, currencyDao: CurrencyDao): RepositoryHelper =
        RepositoryImpl(api, currencyDao)

    @Provides
    @Singleton
    fun provideNetworkHelper(@ApplicationContext appContext: Context): NetworkHelper =
        NetworkHelper(appContext)
}