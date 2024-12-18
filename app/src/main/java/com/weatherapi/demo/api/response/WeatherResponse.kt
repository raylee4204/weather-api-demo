package com.weatherapi.demo.api.response

import com.squareup.moshi.Json
import com.weatherapi.demo.api.WeatherLocation


data class WeatherResponse(val location: WeatherLocation, val current: CurrentWeather) {
    data class CurrentWeather(
        @Json(name = "last_updated_epoch") val lastUpdatedEpoch: Long,
        @Json(name = "temp_c") val tempCelsius: Double,
        @Json(name = "temp_f") val tempFahrenheit: Double,
        val condition: Condition,
        val humidity: Int,
        @Json(name = "feelslike_c") val feelsLikeC: Double,
        @Json(name = "feelslike_f") val feelsLikeF: Double,
        val uv: Double
    )

    data class Condition(
        val text: String, val icon: String, val code: Int
    ) {
        val iconUrl: String
            get() = "https://${icon.replaceFirst("//", "")}"
    }
}