package com.example.project

import android.app.Application
import com.example.project.chores.data.ChoresRepository
import com.example.project.data.AppDatabase
import com.example.project.people.data.PeopleRepository

open class WhoseTurnApplication : Application() {
    private val database by lazy { AppDatabase.create(this) }

    open val choresRepository by lazy { ChoresRepository(database) }
    val peopleRepository by lazy { PeopleRepository(database) }
}
