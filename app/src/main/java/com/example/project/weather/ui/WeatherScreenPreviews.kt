package com.example.project.weather.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.project.ui.theme.ProjectTheme
import com.example.project.weather.ChoreTip
import com.example.project.weather.DayForecast
import com.example.project.weather.WeatherUiState

private val previewDays = listOf(
    DayForecast(
        date = "2026-11-25",
        dayName = "среда",
        label = "Облачно",
        iconUrl = "",
        minTemp = 4,
        maxTemp = 12,
        rainChance = 10,
        weatherCode = 3,
        choreTip = ChoreTip.DoLaundry,
    ),
    DayForecast(
        date = "2026-11-26",
        dayName = "четверг",
        label = "Дождь",
        iconUrl = "",
        minTemp = 2,
        maxTemp = 8,
        rainChance = 75,
        weatherCode = 61,
        choreTip = ChoreTip.SkipWatering,
    ),
)

@Composable
private fun WeatherPreview(state: WeatherUiState) {
    ProjectTheme {
        Surface { WeatherScreen(state = state, onRetry = {}) }
    }
}

@Preview(name = "Weather / loading", showBackground = true)
@Composable
private fun WeatherLoadingPreview() = WeatherPreview(WeatherUiState.Loading)

@Preview(name = "Weather / success", showBackground = true)
@Composable
private fun WeatherSuccessPreview() = WeatherPreview(WeatherUiState.Success(previewDays))

@Preview(name = "Weather / empty", showBackground = true)
@Composable
private fun WeatherEmptyPreview() = WeatherPreview(WeatherUiState.Empty)

@Preview(name = "Weather / error", showBackground = true)
@Composable
private fun WeatherErrorPreview() = WeatherPreview(WeatherUiState.Error("Не удалось загрузить прогноз"))
