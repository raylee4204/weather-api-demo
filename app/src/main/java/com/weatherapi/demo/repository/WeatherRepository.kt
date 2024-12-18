package com.weatherapi.demo.repository

import android.util.Log
import com.weatherapi.demo.api.WeatherLocation
import com.weatherapi.demo.api.WeatherService
import com.weatherapi.demo.api.response.WeatherResponse
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherService: WeatherService
) {

    suspend fun getCurrentWeatherById(id: Int): WeatherResponse? {
        return getCurrentWeather("id:$id")
    }

    suspend fun getCurrentWeather(query: String): WeatherResponse? {
        try {
            return weatherService.getCurrentWeather(query)
        } catch (e: Exception) {
            Log.d("ERROR", e.message ?: "Unknown error")
        }
        return null
    }

    suspend fun searchCity(query: String): List<WeatherLocation> {
        try {
            val result = weatherService.searchCity(query)
            Log.d("SEARCH for $query", result.toString())
            return result
        } catch (e: Exception) {
            Log.d("ERROR", e.message ?: "Unknown error")
        }
        return emptyList()
    }
}