package com.example.project.chores.navigation

import android.net.Uri

object ChoreRoutes {
    const val LIST = "chores"
    const val TASK_ID = "taskId"
    const val DETAIL = "$LIST/{$TASK_ID}"
    const val PEOPLE = "people"
    const val WEATHER = "weather"
    const val SETTINGS = "settings"

    fun detail(taskId: String): String = "$LIST/${Uri.encode(taskId)}"
}
