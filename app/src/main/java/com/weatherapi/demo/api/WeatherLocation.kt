package com.weatherapi.demo.api


data class WeatherLocation(
    val id: Int?,
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double
)