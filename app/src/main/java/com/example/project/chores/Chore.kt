package com.example.project.chores

data class Chore(
    val id: String,
    val title: String,
    val participants: List<String>,
    val currentIndex: Int = 0,
    val lastCompletedBy: String? = null,
) {
    val currentParticipant: String
        get() = participants[currentIndex]

    val turnNumber: Int
        get() = currentIndex + 1

    private val nextIndex: Int
        get() = (currentIndex + 1) % participants.size

    val nextParticipant: String
        get() = participants[nextIndex]

    fun advance(): Chore = copy(
        currentIndex = nextIndex,
        lastCompletedBy = currentParticipant,
    )
}