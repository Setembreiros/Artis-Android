package com.setembreiros.artis.common.di

import com.setembreiros.artis.BuildConfig
import com.setembreiros.artis.common.Constants
import com.setembreiros.artis.data.ApiClient
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providerHttpClientBuilder() : OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder().addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS).build()
    }

    @Provides
    fun providerApiRetrofit(httpClient: OkHttpClient) : Retrofit {

        val moshi = Moshi.Builder()
            //.add(CustomDateAdapter())
            .build()

        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BuildConfig.API_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    fun providerApiClient(retrofit: Retrofit) : ApiClient {
        return retrofit.create(ApiClient::class.java)
    }

}