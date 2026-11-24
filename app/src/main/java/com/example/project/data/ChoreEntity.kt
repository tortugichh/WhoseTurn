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
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["startPersonId"],
        ),
    ],
    indices = [Index("currentPersonId"), Index("startPersonId")],
)
data class ChoreEntity(
    @PrimaryKey val id: String,
    val title: String,
    val icon: String = "home",
    val currentPersonId: Long?,
    val startPersonId: Long?,
    val lastCompletedBy: String? = null,
)