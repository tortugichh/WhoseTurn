package com.example.project.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChoreDao {
    @Query("SELECT * FROM chores ORDER BY id")
    fun observeAll(): Flow<List<ChoreEntity>>

    @Query("SELECT * FROM chores WHERE id = :id")
    suspend fun find(id: String): ChoreEntity?

    @Insert
    suspend fun insert(chore: ChoreEntity)

    @Update
    suspend fun update(chore: ChoreEntity)

    @Query("DELETE FROM chores WHERE id = :id")
    suspend fun delete(id: String)

    @Query(
        """
        UPDATE chores
        SET currentPersonId = CASE WHEN currentPersonId = :id THEN :nextId ELSE currentPersonId END,
            startPersonId = CASE WHEN startPersonId = :id THEN :nextId ELSE startPersonId END
        WHERE currentPersonId = :id OR startPersonId = :id
        """,
    )
    suspend fun replacePerson(id: Long, nextId: Long?)

    @Query(
        """
        UPDATE chores SET currentPersonId = :id, startPersonId = :id
        WHERE currentPersonId IS NULL AND startPersonId IS NULL
        """,
    )
    suspend fun assignUnassignedTurns(id: Long)
}