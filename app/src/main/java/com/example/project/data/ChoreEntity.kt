package com.example.project.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "chores",
    foreignKeys = [
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["currentPersonId"],
        ),
    ],
    indices = [Index("currentPersonId")],
)
data class ChoreEntity(
    @PrimaryKey val id: String,
    val title: String,
    val icon: String = "home",
    val currentPersonId: Long?,
    val repeatDays: Int = 7,
    val dueDay: Long,
    val lastCompletedBy: String? = null,
)
