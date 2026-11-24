package com.example.project.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.project.chores.data.loadDemoChores

@Database(
    entities = [ChoreEntity::class, PersonEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun choreDao(): ChoreDao
    abstract fun personDao(): PersonDao

    companion object {
        private const val DATABASE_NAME = "whose-turn.db"

        internal val demoSeed = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                val chores = loadDemoChores()
                val personIds = chores.first().participants
                    .mapIndexed { index, name -> name to (index + 1L) }
                    .toMap()

                personIds.forEach { (name, id) ->
                    db.execSQL(
                        "INSERT INTO people (id, name) VALUES (?, ?)",
                        arrayOf<Any>(id, name),
                    )
                }

                chores.forEach { chore ->
                    val startId = personIds.getValue(chore.participants.first())
                    db.execSQL(
                        """
                        INSERT INTO chores (id, title, icon, currentPersonId, startPersonId)
                        VALUES (?, ?, ?, ?, ?)
                        """.trimIndent(),
                        arrayOf<Any>(chore.id, chore.title, chore.icon.key, startId, startId),
                    )
                }
            }
        }

        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(demoSeed)
                .build()
    }
}
