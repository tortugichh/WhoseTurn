package com.example.project.weather.data

import com.example.project.weather.DayForecast
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class WeatherRepository(private val api: WeatherApi) {
    suspend fun loadForecast(): List<DayForecast> {
        val daily = api.getForecast().daily ?: return emptyList()
        val dates = daily.dates.orEmpty()
        val codes = daily.weatherCodes.orEmpty()
        val highs = daily.maxTemperatures.orEmpty()
        val lows = daily.minTemperatures.orEmpty()
        val rain = daily.rainChances.orEmpty()
        val count = listOf(dates.size, codes.size, highs.size, lows.size, rain.size).minOrNull() ?: 0

        return (0 until count).map { index ->
            val code = codes[index]
            DayForecast(
                date = dates[index],
                dayName = dayName(dates[index]),
                label = weatherLabel(code),
                iconUrl = "https://openweathermap.org/img/wn/${iconCode(code)}@2x.png",
                minTemp = lows[index].toInt(),
                maxTemp = highs[index].toInt(),
                rainChance = rain[index],
                weatherCode = code,
            )
        }
    }

    private fun dayName(date: String): String = try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }
        val parsed = parser.parse(date)
        parsed?.let { SimpleDateFormat("EEEE", Locale("ru")).format(it) }.orEmpty()
    } catch (_: IllegalArgumentException) {
        date
    }

    private fun weatherLabel(code: Int): String = when (code) {
        0 -> "Ясно"
        1, 2 -> "Переменная облачность"
        3 -> "Облачно"
        45, 48 -> "Туман"
        in 51..57 -> "Морось"
        in 61..67, in 80..82 -> "Дождь"
        in 71..77, 85, 86 -> "Снег"
        in 95..99 -> "Гроза"
        else -> "Облачно"
    }

    private fun iconCode(code: Int): String = when (code) {
        0 -> "01d"
        1, 2 -> "02d"
        3 -> "04d"
        45, 48 -> "50d"
        in 51..57 -> "09d"
        in 61..67, in 80..82 -> "10d"
        in 71..77, 85, 86 -> "13d"
        in 95..99 -> "11d"
        else -> "03d"
    }

    companion object {
        private const val BASE_URL = "https://api.open-meteo.com/"

        fun create(): WeatherRepository {
            val api = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApi::class.java)
            return WeatherRepository(api)
        }
    }
}
