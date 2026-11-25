package com.example.project

import android.app.Application
import com.example.project.chores.data.ChoresRepository
import com.example.project.data.AppDatabase
import com.example.project.people.data.PeopleRepository
import com.example.project.settings.data.SettingsRepository
import com.example.project.settings.data.settingsDataStore
import com.example.project.weather.data.WeatherRepository

open class WhoseTurnApplication : Application() {
    private val database by lazy { AppDatabase.create(this) }

    open val choresRepository by lazy { ChoresRepository(database) }
    val peopleRepository by lazy { PeopleRepository(database) }
    val settingsRepository by lazy { SettingsRepository(settingsDataStore) }
    val weatherRepository by lazy { WeatherRepository.create() }
}
