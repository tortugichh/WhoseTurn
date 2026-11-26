package com.example.project.chores

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.project.chores.data.ChoresRepository
import com.example.project.chores.data.currentEpochDay
import com.example.project.data.AppDatabase
import com.example.project.people.data.PeopleRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChoresRepositoryTest {
    private val today = currentEpochDay()
    private lateinit var database: AppDatabase
    private lateinit var chores: ChoresRepository
    private lateinit var people: PeopleRepository

    @Before
    fun openDatabase() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .addCallback(AppDatabase.demoSeed)
            .build()
        chores = ChoresRepository(database, today = { today })
        people = PeopleRepository(database)
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    private suspend fun chore(title: String) = chores.chores.first().single { it.title == title }

    private suspend fun personId(name: String) = people.people.first().single { it.name == name }.id

    @Test
    fun newChoreUsesChosenPeopleInChosenOrder() = runBlocking {
        val order = listOf(personId("Нурмек"), personId("Асхат"))

        chores.add("Пылесос", ChoreIcon.Broom, order, repeatDays = 3)

        val added = chore("Пылесос")
        assertEquals(listOf("Нурмек", "Асхат"), added.participants)
        assertEquals("Нурмек", added.currentParticipant)
        assertEquals(0, added.dueInDays)
        assertEquals(3, added.repeatDays)
    }

    @Test
    fun doneMovesTurnAndDueDate() = runBlocking {
        val id = chore("Помыть посуду").id

        assertEquals("Нурмек", chores.complete(id))

        val done = chore("Помыть посуду")
        assertEquals("Нурмек", done.currentParticipant)
        assertEquals("Данияр", done.lastCompletedBy)
        assertEquals(1, done.dueInDays)
    }

    @Test
    fun skipMovesTurnButKeepsDueDate() = runBlocking {
        val id = chore("Полить растения").id

        assertEquals("Асхат", chores.skip(id))

        val skipped = chore("Полить растения")
        assertEquals("Асхат", skipped.currentParticipant)
        assertEquals(null, skipped.lastCompletedBy)
        assertEquals(3, skipped.dueInDays)
    }

    @Test
    fun turnOnlyRotatesThroughTheChoresOwnPeople() = runBlocking {
        val pair = listOf(personId("Асхат"), personId("Данияр"))
        chores.add("Пылесос", ChoreIcon.Broom, pair)
        val id = chore("Пылесос").id

        assertEquals("Данияр", chores.complete(id))
        assertEquals("Асхат", chores.complete(id))
    }

    @Test
    fun editingKeepsCurrentPersonWhenStillIncluded() = runBlocking {
        val dishes = chore("Помыть посуду")
        val withoutAskhat = dishes.participantIds - personId("Асхат")

        chores.update(dishes.id, "Посуда", ChoreIcon.Dishes, withoutAskhat, repeatDays = 7)

        val edited = chore("Посуда")
        assertEquals(listOf("Данияр", "Нурмек"), edited.participants)
        assertEquals("Данияр", edited.currentParticipant)
        assertEquals(7, edited.repeatDays)
    }

    @Test
    fun deletingCurrentPersonPassesTurnToNextMember() = runBlocking {
        people.delete(personId("Данияр"))

        val dishes = chore("Помыть посуду")
        assertEquals(listOf("Нурмек", "Асхат"), dishes.participants)
        assertEquals("Нурмек", dishes.currentParticipant)
    }

    @Test
    fun newPersonJoinsTheEndOfEveryChore() = runBlocking {
        people.add("Али")

        chores.chores.first().forEach { chore ->
            assertEquals("Али", chore.participants.last())
        }
    }
}
