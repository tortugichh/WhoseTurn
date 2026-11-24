package com.example.project.chores.data

import androidx.room.withTransaction
import com.example.project.chores.Chore
import com.example.project.chores.ChoreIcon
import com.example.project.data.AppDatabase
import com.example.project.data.ChoreEntity
import com.example.project.data.PersonEntity
import com.example.project.people.data.nextAfter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.UUID

class ChoresRepository(private val database: AppDatabase) {
    private val choreDao = database.choreDao()
    private val personDao = database.personDao()

    val chores: Flow<List<Chore>> =
        combine(choreDao.observeAll(), personDao.observeAll()) { chores, people ->
            chores.map { it.toChore(people) }
        }

    suspend fun add(title: String, icon: ChoreIcon = ChoreIcon.Home) {
        val trimmedTitle = title.trim()
        require(trimmedTitle.isNotEmpty())

        database.withTransaction {
            val startId = personDao.getAll().firstOrNull()?.id
            choreDao.insert(
                ChoreEntity(
                    id = UUID.randomUUID().toString(),
                    title = trimmedTitle,
                    icon = icon.key,
                    currentPersonId = startId,
                    startPersonId = startId,
                ),
            )
        }
    }

    suspend fun rename(id: String, title: String, icon: ChoreIcon? = null) {
        val trimmedTitle = title.trim()
        require(trimmedTitle.isNotEmpty())

        database.withTransaction {
            val chore = choreDao.find(id) ?: return@withTransaction
            choreDao.update(chore.copy(title = trimmedTitle, icon = icon?.key ?: chore.icon))
        }
    }

    suspend fun delete(id: String) {
        choreDao.delete(id)
    }

    suspend fun complete(id: String): String? =
        database.withTransaction {
            val chore = choreDao.find(id) ?: return@withTransaction null
            val people = personDao.getAll()
            val current = people.find { it.id == chore.currentPersonId }
                ?: return@withTransaction null
            val next = people.nextAfter(current.id) ?: return@withTransaction null

            choreDao.update(
                chore.copy(
                    currentPersonId = next.id,
                    lastCompletedBy = current.name,
                ),
            )
            next.name
        }

    suspend fun resetTurns() {
        choreDao.resetTurns()
    }
}

private fun ChoreEntity.toChore(people: List<PersonEntity>): Chore {
    val startIndex = people.indexOfFirst { it.id == startPersonId }.coerceAtLeast(0)
    val rotation = people.drop(startIndex) + people.take(startIndex)

    return Chore(
        id = id,
        title = title,
        participants = rotation.map { it.name },
        currentIndex = rotation.indexOfFirst { it.id == currentPersonId }.coerceAtLeast(0),
        lastCompletedBy = lastCompletedBy,
        icon = ChoreIcon.fromKey(icon),
    )
}
