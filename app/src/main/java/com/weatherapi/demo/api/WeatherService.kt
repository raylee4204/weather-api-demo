package com.weatherapi.demo.api

import com.weatherapi.demo.api.response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherService {

    @GET("search.json")
    suspend fun searchCity(@Query("q") query: String): List<WeatherLocation>

    @GET("current.json")
    suspend fun getCurrentWeather(@Query("q") query: String): WeatherResponse

}