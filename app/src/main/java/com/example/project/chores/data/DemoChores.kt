package com.example.project.chores.data

import com.example.project.chores.Chore

fun loadDemoChores(): List<Chore> = listOf(
    Chore(
        id = "trash",
        title = "Вынести мусор",
        participants = listOf("Асхат", "Данияр", "Нурмек"),
    ),
    Chore(
        id = "dishes",
        title = "Помыть посуду",
        participants = listOf("Данияр", "Нурмек", "Асхат"),
    ),
    Chore(
        id = "plants",
        title = "Полить растения",
        participants = listOf("Нурмек", "Асхат", "Данияр"),
    ),
)