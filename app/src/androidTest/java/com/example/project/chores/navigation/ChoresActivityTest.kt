package com.example.project.chores.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.espresso.Espresso.pressBack
import com.example.project.MainActivity
import org.junit.Rule
import org.junit.Test

class ChoresActivityTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun detailsAndTurnSurviveActivityRecreationAndSystemBack() {
        compose.onNodeWithText("Вынести мусор").performClick()
        compose.onNodeWithText("Выполнено").performScrollTo().performClick()

        compose.activityRule.scenario.recreate()

        compose.onNodeWithText("Сейчас очередь: Данияр").assertIsDisplayed()
        compose.onNodeWithText("Последний исполнитель: Асхат").assertIsDisplayed()
        pressBack()

        compose.onNode(hasText("Вынести мусор") and hasText("Сейчас очередь: Данияр"))
            .assertIsDisplayed()
        compose.onNodeWithText("Помыть посуду").performClick()
        compose.onNodeWithText("Ход 1 из 3").assertIsDisplayed()
        compose.onNodeWithText("Сейчас очередь: Данияр").assertIsDisplayed()
    }
}
