package com.example.project.chores

enum class ChoreIcon(val key: String) {
    Trash("trash"),
    Dishes("dishes"),
    Plant("plant"),
    Broom("broom"),
    Laundry("laundry"),
    Home("home");

    companion object {
        fun fromKey(key: String): ChoreIcon = entries.firstOrNull { it.key == key } ?: Home
    }
}
