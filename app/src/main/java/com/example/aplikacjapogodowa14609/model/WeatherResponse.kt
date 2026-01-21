package com.example.aplikacjapogodowa14609.model

import android.opengl.Visibility
import java.sql.Date
import java.sql.Time


data class WeatherResponse(
    val data: WeatherData,
    val location: LocationInfo
)

data class WeatherData(
    val values: WeatherValues,
    val time: String
)

data class WeatherValues(
    val temperature: Double,
    val temperatureApparent: Double,
    val humidity: Double,
    val windSpeed: Double,
    val windDirection: Double,
    val pressureSeaLevel: Double,
    val weatherCode: Double,
    val rainIntensity: Double,
    val sleetIntensity: Double,
    val snowIntensity: Double,
    val uvIndex: Int,
    val cloudCover: Double,
    val precipitationProbability: Double,
    val visibility: Double,



)
data class LocationInfo(
    val name: String
)
