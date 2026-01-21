package com.example.aplikacjapogodowa14609.network


import com.example.aplikacjapogodowa14609.model.WeatherResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("weather/realtime")
    suspend fun getRealtimeWeather(
        @Query("location") location: String,
        @Query("apikey") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}

object RetrofitInstance {
    private const val BASE_URL = "https://api.tomorrow.io/v4/"

    val api: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}