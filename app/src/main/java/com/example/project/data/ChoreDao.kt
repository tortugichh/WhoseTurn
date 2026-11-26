package com.example.project.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChoreDao {
    @Query("SELECT * FROM chores ORDER BY dueDay, title")
    fun observeAll(): Flow<List<ChoreEntity>>

    @Query("SELECT * FROM chores")
    suspend fun getAll(): List<ChoreEntity>

    @Query("SELECT * FROM chores WHERE id = :id")
    suspend fun find(id: String): ChoreEntity?

    @Insert
    suspend fun insert(chore: ChoreEntity)

    @Update
    suspend fun update(chore: ChoreEntity)

    @Query("DELETE FROM chores WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM chore_members ORDER BY choreId, position")
    fun observeMembers(): Flow<List<ChoreMemberEntity>>

    @Query("SELECT personId FROM chore_members WHERE choreId = :choreId ORDER BY position")
    suspend fun memberIds(choreId: String): List<Long>

    @Query("SELECT COALESCE(MAX(position) + 1, 0) FROM chore_members WHERE choreId = :choreId")
    suspend fun nextPosition(choreId: String): Int

    @Insert
    suspend fun insertMembers(members: List<ChoreMemberEntity>)

    @Query("DELETE FROM chore_members WHERE choreId = :choreId")
    suspend fun deleteMembers(choreId: String)
}
