package com.example.project.chores.data

import androidx.room.withTransaction
import com.example.project.chores.Chore
import com.example.project.chores.ChoreIcon
import com.example.project.data.AppDatabase
import com.example.project.data.ChoreEntity
import com.example.project.data.ChoreMemberEntity
import com.example.project.data.PersonEntity
import com.example.project.people.Person
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.UUID

class ChoresRepository(
    private val database: AppDatabase,
    private val today: () -> Long = { currentEpochDay() },
) {
    private val choreDao = database.choreDao()
    private val personDao = database.personDao()

    val people: Flow<List<Person>> = personDao.observeAll().map { rows ->
        rows.map { Person(id = it.id, name = it.name) }
    }

    val chores: Flow<List<Chore>> = combine(
        choreDao.observeAll(),
        choreDao.observeMembers(),
        personDao.observeAll(),
    ) { chores, members, people ->
        val membersByChore = members.groupBy({ it.choreId }, { it.personId })
        val day = today()
        chores.map { it.toChore(membersByChore[it.id].orEmpty(), people, day) }
    }

    suspend fun add(
        title: String,
        icon: ChoreIcon = ChoreIcon.Home,
        memberIds: List<Long>? = null,
        repeatDays: Int = 7,
    ) {
        val trimmedTitle = title.trim()
        require(trimmedTitle.isNotEmpty())

        database.withTransaction {
            val members = memberIds ?: personDao.getAll().map { it.id }
            val id = UUID.randomUUID().toString()
            choreDao.insert(
                ChoreEntity(
                    id = id,
                    title = trimmedTitle,
                    icon = icon.key,
                    currentPersonId = members.firstOrNull(),
                    repeatDays = repeatDays,
                    dueDay = today(),
                ),
            )
            saveMembers(id, members)
        }
    }

    suspend fun update(
        id: String,
        title: String,
        icon: ChoreIcon,
        memberIds: List<Long>,
        repeatDays: Int,
    ) {
        val trimmedTitle = title.trim()
        require(trimmedTitle.isNotEmpty())

        database.withTransaction {
            val chore = choreDao.find(id) ?: return@withTransaction
            val current = chore.currentPersonId?.takeIf { it in memberIds } ?: memberIds.firstOrNull()
            choreDao.update(
                chore.copy(
                    title = trimmedTitle,
                    icon = icon.key,
                    currentPersonId = current,
                    repeatDays = repeatDays,
                ),
            )
            choreDao.deleteMembers(id)
            saveMembers(id, memberIds)
        }
    }

    suspend fun delete(id: String) {
        choreDao.delete(id)
    }

    suspend fun complete(id: String): String? = database.withTransaction {
        val chore = choreDao.find(id) ?: return@withTransaction null
        val people = personDao.getAll().associateBy { it.id }
        val current = chore.currentPersonId?.let(people::get) ?: return@withTransaction null
        val next = choreDao.memberIds(id).nextAfter(current.id)?.let(people::get)
            ?: return@withTransaction null

        choreDao.update(
            chore.copy(
                currentPersonId = next.id,
                lastCompletedBy = current.name,
                dueDay = today() + chore.repeatDays,
            ),
        )
        next.name
    }

    private suspend fun saveMembers(choreId: String, memberIds: List<Long>) {
        choreDao.insertMembers(
            memberIds.distinct().mapIndexed { position, personId ->
                ChoreMemberEntity(choreId = choreId, personId = personId, position = position)
            },
        )
    }
}

private fun ChoreEntity.toChore(memberIds: List<Long>, people: List<PersonEntity>, today: Long): Chore {
    val names = people.associate { it.id to it.name }
    val members = memberIds.filter(names::containsKey)

    return Chore(
        id = id,
        title = title,
        participants = members.map(names::getValue),
        currentIndex = members.indexOf(currentPersonId).coerceAtLeast(0),
        lastCompletedBy = lastCompletedBy,
        icon = ChoreIcon.fromKey(icon),
        participantIds = members,
        repeatDays = repeatDays,
        dueInDays = (dueDay - today).toInt(),
    )
}
