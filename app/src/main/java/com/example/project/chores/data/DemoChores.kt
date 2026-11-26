package com.example.project.chores.data

import com.example.project.chores.Chore
import com.example.project.chores.ChoreIcon

fun loadDemoChores(): List<Chore> = listOf(
    Chore(
        id = "trash",
        title = "Вынести мусор",
        participants = listOf("Асхат", "Данияр", "Нурмек"),
        icon = ChoreIcon.Trash,
        repeatDays = 3,
        dueInDays = 1,
    ),
    Chore(
        id = "dishes",
        title = "Помыть посуду",
        participants = listOf("Данияр", "Нурмек", "Асхат"),
        icon = ChoreIcon.Dishes,
        repeatDays = 1,
    ),
    Chore(
        id = "plants",
        title = "Полить растения",
        participants = listOf("Нурмек", "Асхат", "Данияр"),
        icon = ChoreIcon.Plant,
        dueInDays = 3,
    ),
)
