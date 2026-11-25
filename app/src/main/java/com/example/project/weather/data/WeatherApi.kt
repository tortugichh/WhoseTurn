package com.example.project.weather.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double = 43.24,
        @Query("longitude") longitude: Double = 76.89,
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max",
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7,
    ): ForecastResponse
}

data class ForecastResponse(
    @SerializedName("daily") val daily: DailyForecastResponse?,
)

data class DailyForecastResponse(
    @SerializedName("time") val dates: List<String>?,
    @SerializedName("weather_code") val weatherCodes: List<Int>?,
    @SerializedName("temperature_2m_max") val maxTemperatures: List<Double>?,
    @SerializedName("temperature_2m_min") val minTemperatures: List<Double>?,
    @SerializedName("precipitation_probability_max") val rainChances: List<Int>?,
)
