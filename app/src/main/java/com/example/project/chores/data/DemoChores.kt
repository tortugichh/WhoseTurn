package com.example.project.chores.data

import com.example.project.chores.Chore
import com.example.project.chores.ChoreIcon

fun loadDemoChores(): List<Chore> = listOf(
    Chore(
        id = "trash",
        title = "Вынести мусор",
        participants = listOf("Асхат", "Данияр", "Нурмек"),
        icon = ChoreIcon.Trash,
    ),
    Chore(
        id = "dishes",
        title = "Помыть посуду",
        participants = listOf("Данияр", "Нурмек", "Асхат"),
        icon = ChoreIcon.Dishes,
    ),
    Chore(
        id = "plants",
        title = "Полить растения",
        participants = listOf("Нурмек", "Асхат", "Данияр"),
        icon = ChoreIcon.Plant,
    ),
)
