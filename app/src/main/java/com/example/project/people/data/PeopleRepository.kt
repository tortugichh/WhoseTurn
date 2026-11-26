package com.example.project.people.data

import androidx.room.withTransaction
import com.example.project.chores.data.nextAfter
import com.example.project.data.AppDatabase
import com.example.project.data.ChoreMemberEntity
import com.example.project.data.PersonEntity
import com.example.project.people.Person
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PeopleRepository(private val database: AppDatabase) {
    private val personDao = database.personDao()
    private val choreDao = database.choreDao()

    val people: Flow<List<Person>> = personDao.observeAll().map { rows ->
        rows.map { Person(id = it.id, name = it.name) }
    }

    suspend fun add(name: String) {
        val trimmedName = name.trim()
        require(trimmedName.isNotEmpty())

        database.withTransaction {
            val id = personDao.insert(PersonEntity(name = trimmedName))
            choreDao.getAll().forEach { chore ->
                val position = choreDao.nextPosition(chore.id)
                choreDao.insertMembers(listOf(ChoreMemberEntity(chore.id, id, position)))
                if (chore.currentPersonId == null) {
                    choreDao.update(chore.copy(currentPersonId = id))
                }
            }
        }
    }

    suspend fun rename(id: Long, name: String) {
        val trimmedName = name.trim()
        require(trimmedName.isNotEmpty())
        personDao.rename(id, trimmedName)
    }

    suspend fun delete(id: Long) {
        database.withTransaction {
            choreDao.getAll().filter { it.currentPersonId == id }.forEach { chore ->
                val nextId = choreDao.memberIds(chore.id).nextAfter(id)?.takeIf { it != id }
                choreDao.update(chore.copy(currentPersonId = nextId))
            }
            personDao.delete(id)
        }
    }
}
