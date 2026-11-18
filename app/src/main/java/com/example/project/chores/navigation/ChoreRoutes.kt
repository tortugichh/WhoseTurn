package com.example.project.chores.navigation

import android.net.Uri

object ChoreRoutes {
    const val LIST = "chores"
    const val TASK_ID = "taskId"
    const val DETAIL = "$LIST/{$TASK_ID}"

    fun detail(taskId: String): String = "$LIST/${Uri.encode(taskId)}"
}
