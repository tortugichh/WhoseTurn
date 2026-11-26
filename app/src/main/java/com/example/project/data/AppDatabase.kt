package com.example.project.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.project.chores.data.currentEpochDay
import com.example.project.chores.data.loadDemoChores

@Database(
    entities = [ChoreEntity::class, PersonEntity::class, ChoreMemberEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun choreDao(): ChoreDao
    abstract fun personDao(): PersonDao

    companion object {
        private const val DATABASE_NAME = "whose-turn.db"

        internal val demoSeed = object : RoomDatabase.Callback() {
            private var seedAfterUpgrade = false

            override fun onCreate(db: SupportSQLiteDatabase) = seed(db)

            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                seedAfterUpgrade = true
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                if (seedAfterUpgrade) {
                    seedAfterUpgrade = false
                    seed(db)
                }
            }

            private fun seed(db: SupportSQLiteDatabase) {
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

                val today = currentEpochDay()
                chores.forEach { chore ->
                    val memberIds = chore.participants.map(personIds::getValue)
                    db.execSQL(
                        """
                        INSERT INTO chores (id, title, icon, currentPersonId, repeatDays, dueDay)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """.trimIndent(),
                        arrayOf<Any>(
                            chore.id,
                            chore.title,
                            chore.icon.key,
                            memberIds.first(),
                            chore.repeatDays,
                            today + chore.dueInDays,
                        ),
                    )
                    memberIds.forEachIndexed { position, personId ->
                        db.execSQL(
                            "INSERT INTO chore_members (choreId, personId, position) VALUES (?, ?, ?)",
                            arrayOf<Any>(chore.id, personId, position),
                        )
                    }
                }
            }
        }

        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                .addCallback(demoSeed)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}
