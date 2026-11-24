package com.example.project.people.data

import androidx.room.withTransaction
import com.example.project.data.AppDatabase
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
            choreDao.assignUnassignedTurns(id)
        }
    }

    suspend fun rename(id: Long, name: String) {
        val trimmedName = name.trim()
        require(trimmedName.isNotEmpty())
        personDao.rename(id, trimmedName)
    }

    suspend fun delete(id: Long) {
        database.withTransaction {
            val remainingPeople = personDao.getAll().filterNot { it.id == id }
            val nextId = remainingPeople.nextAfter(id)?.id
            choreDao.replacePerson(id, nextId)
            personDao.delete(id)
        }
    }
}

internal fun List<PersonEntity>.nextAfter(personId: Long): PersonEntity? =
    firstOrNull { it.id > personId } ?: firstOrNull()