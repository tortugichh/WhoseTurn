package com.example.project.chores

import com.example.project.people.Person

data class ChoreDraft(
    val title: String,
    val icon: ChoreIcon,
    val memberIds: List<Long>,
    val repeatDays: Int,
)

val RepeatOptions = listOf(1, 3, 7, 14)

internal fun Chore.toDraft() = ChoreDraft(
    title = title,
    icon = icon,
    memberIds = participantIds,
    repeatDays = repeatDays,
)

internal fun newChoreDraft(people: List<Person>, chores: List<Chore>): ChoreDraft {
    val ids = people.map { it.id }
    val turns = chores
        .mapNotNull { it.participantIds.getOrNull(it.currentIndex) }
        .groupingBy { it }
        .eachCount()
    val start = ids.indexOf(ids.minByOrNull { turns[it] ?: 0 }).coerceAtLeast(0)

    return ChoreDraft(
        title = "",
        icon = ChoreIcon.Home,
        memberIds = ids.drop(start) + ids.take(start),
        repeatDays = 7,
    )
}
