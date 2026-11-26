package com.example.project.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "chore_members",
    primaryKeys = ["choreId", "personId"],
    foreignKeys = [
        ForeignKey(
            entity = ChoreEntity::class,
            parentColumns = ["id"],
            childColumns = ["choreId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PersonEntity::class,
            parentColumns = ["id"],
            childColumns = ["personId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("personId")],
)
data class ChoreMemberEntity(
    val choreId: String,
    val personId: Long,
    val position: Int,
)
