package com.example.project.chores

import com.example.project.chores.data.currentEpochDay
import com.example.project.chores.data.nextAfter
import com.example.project.people.Person
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.TimeZone

class RotationTest {
    @Test
    fun turnGoesToNextMemberAndWrapsAround() {
        val members = listOf(2L, 3L, 1L)

        assertEquals(3L, members.nextAfter(2L))
        assertEquals(1L, members.nextAfter(3L))
        assertEquals(2L, members.nextAfter(1L))
    }

    @Test
    fun unknownCurrentStartsFromFirstMember() {
        assertEquals(2L, listOf(2L, 3L).nextAfter(null))
        assertEquals(2L, listOf(2L, 3L).nextAfter(99L))
    }

    @Test
    fun noMembersMeansNoTurn() {
        assertNull(emptyList<Long>().nextAfter(1L))
    }

    @Test
    fun dayChangesAtLocalMidnight() {
        val almaty = TimeZone.getTimeZone("GMT+05:00")
        val lateEvening = 1_795_717_800_000L
        val oneHourLater = lateEvening + 60 * 60 * 1000

        assertEquals(currentEpochDay(lateEvening, almaty) + 1, currentEpochDay(oneHourLater, almaty))
    }

    @Test
    fun newChoreStartsWithTheLeastBusyPerson() {
        val people = listOf(Person(1, "A"), Person(2, "B"), Person(3, "C"))
        val chores = listOf(
            Chore("x", "X", listOf("A", "B"), currentIndex = 0, participantIds = listOf(1, 2)),
            Chore("y", "Y", listOf("B", "C"), currentIndex = 0, participantIds = listOf(2, 3)),
        )

        assertEquals(listOf(3L, 1L, 2L), newChoreDraft(people, chores).memberIds)
    }

    @Test
    fun newChoreKeepsPeopleOrderWhenEveryoneIsEquallyBusy() {
        val people = listOf(Person(1, "A"), Person(2, "B"))

        assertEquals(listOf(1L, 2L), newChoreDraft(people, emptyList()).memberIds)
    }
}
