package com.example.project.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM people ORDER BY id")
    fun observeAll(): Flow<List<PersonEntity>>

    @Query("SELECT * FROM people ORDER BY id")
    suspend fun getAll(): List<PersonEntity>

    @Insert
    suspend fun insert(person: PersonEntity): Long

    @Query("UPDATE people SET name = :name WHERE id = :id")
    suspend fun rename(id: Long, name: String)

    @Query("DELETE FROM people WHERE id = :id")
    suspend fun delete(id: Long)
}