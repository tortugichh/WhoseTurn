package com.example.project.weather

data class DayForecast(
    val date: String,
    val dayName: String,
    val label: String,
    val iconUrl: String,
    val minTemp: Int,
    val maxTemp: Int,
    val rainChance: Int,
    val weatherCode: Int,
    val choreTip: ChoreTip?,
)

enum class ChoreTip { SkipWatering, ClearEntrance, DoLaundry }
