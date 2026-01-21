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
    val windDirection: Int,
    val pressureSeaLevel: Double,
    val weatherCode: Int,
    val rainIntensity: Int,
    val sleetIntensity: Int,
    val snowIntensity: Int,
    val uvIndex: Int,
    val cloudCover: Int,
    val precipitationProbability: Int,
    val visibility: Int,



)
data class LocationInfo(
    val name: String
)
