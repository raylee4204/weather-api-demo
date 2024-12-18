package com.weatherapi.demo.di

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.weatherapi.demo.BuildConfig
import com.weatherapi.demo.api.WeatherService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.weatherapi.com/v1/"

    @Singleton
    @Provides
    fun provideRetrofitBuilder(
        moshi: Moshi
    ) = Retrofit.Builder().addConverterFactory(MoshiConverterFactory.create(moshi))

    @Singleton
    @Provides
    fun provideMoshi() = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    @Singleton
    @Provides
    fun provideWeatherService(
        retrofitBuilder: Retrofit.Builder,
        okHttpClient: OkHttpClient
    ): WeatherService {
        return retrofitBuilder
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()
            .create(WeatherService::class.java)
    }


    @Singleton
    @Provides
    fun provideOkhttp() = OkHttpClient.Builder().addInterceptor { chain ->
        val request = chain.request().newBuilder()
        val originalHttpUrl = chain.request().url
        val url =
            originalHttpUrl.newBuilder().addQueryParameter("key", BuildConfig.WEATHER_API_KEY).build()
        request.url(url)
        Log.d("URL", "$url")
        val response = chain.proceed(request.build())
        return@addInterceptor response
    }.build()
}