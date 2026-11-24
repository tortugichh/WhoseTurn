package com.example.project.chores.navigation

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnitRunner
import com.example.project.WhoseTurnApplication
import com.example.project.chores.data.ChoresRepository
import com.example.project.data.AppDatabase
import org.junit.rules.ExternalResource

class ChoresTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader,
        className: String,
        context: Context,
    ): Application = super.newApplication(cl, TestApplication::class.java.name, context)
}

class TestApplication : WhoseTurnApplication() {
    override lateinit var choresRepository: ChoresRepository
}

class FreshChoresDatabase : ExternalResource() {
    private lateinit var database: AppDatabase

    override fun before() {
        val app = InstrumentationRegistry.getInstrumentation()
            .targetContext.applicationContext as TestApplication
        database = Room.inMemoryDatabaseBuilder(app, AppDatabase::class.java)
            .allowMainThreadQueries()
            .setQueryExecutor { it.run() }
            .setTransactionExecutor { it.run() }
            .addCallback(AppDatabase.demoSeed)
            .build()
        app.choresRepository = ChoresRepository(database)
    }

    override fun after() {
        database.close()
    }
}
