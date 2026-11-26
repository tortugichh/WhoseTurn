package com.example.project.chores

data class Chore(
    val id: String,
    val title: String,
    val participants: List<String>,
    val currentIndex: Int = 0,
    val lastCompletedBy: String? = null,
    val icon: ChoreIcon = ChoreIcon.Home,
    val participantIds: List<Long> = emptyList(),
    val repeatDays: Int = 7,
    val dueInDays: Int = 0,
) {
    val currentParticipant: String?
        get() = participants.getOrNull(currentIndex)

    val turnNumber: Int
        get() = if (currentIndex in participants.indices) currentIndex + 1 else 0

    internal val nextIndex: Int?
        get() = if (currentIndex in participants.indices) {
            (currentIndex + 1) % participants.size
        } else {
            null
        }

    val nextParticipant: String?
        get() = nextIndex?.let { participants[it] }

    fun advance(): Chore {
        val next = nextIndex ?: return this
        return copy(currentIndex = next, lastCompletedBy = currentParticipant)
    }
}
