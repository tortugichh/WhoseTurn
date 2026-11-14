package com.example.project.chores

data class Chore(
    val id: String,
    val title: String,
    val participants: List<String>,
    val currentIndex: Int = 0,
) {
    val currentParticipant: String
        get() = participants[currentIndex]

    fun advance(): Chore {
        val nextIndex = (currentIndex + 1) % participants.size
        return copy(currentIndex = nextIndex)
    }
}