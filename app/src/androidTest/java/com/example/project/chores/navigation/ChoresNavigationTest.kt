package com.example.project.chores.navigation

import androidx.activity.ComponentActivity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.project.chores.ChoresViewModel
import com.example.project.ui.theme.ProjectTheme
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ChoresNavigationTest {
    @get:Rule(order = 0)
    val database = FreshChoresDatabase()

    @get:Rule(order = 1)
    val compose = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: NavHostController

    @Before
    fun openApp() {
        compose.setContent {
            val choresViewModel: ChoresViewModel = viewModel(factory = ChoresViewModel.Factory)
            navController = rememberNavController()
            ProjectTheme {
                ChoresApp(viewModel = choresViewModel, navController = navController)
            }
        }
    }

    @Test
    fun completingDishesUpdatesOnlyItsCardAfterGoingBack() {
        compose.onNodeWithText("Помыть посуду").performClick()
        compose.onNodeWithText("Сейчас очередь: Данияр").assertIsDisplayed()
        compose.onNodeWithText("Ход 1 из 3").assertIsDisplayed()

        completeChore()

        compose.onNodeWithText("Сейчас очередь: Нурмек").assertIsDisplayed()
        compose.onNodeWithText("Ход 2 из 3").assertIsDisplayed()
        compose.onNodeWithText("Последний исполнитель: Данияр").assertIsDisplayed()
        compose.onNodeWithContentDescription("Назад").performClick()

        assertCard("Помыть посуду", "Нурмек")
        assertCard("Вынести мусор", "Асхат")
        assertCard("Полить растения", "Нурмек")
    }

    @Test
    fun completingTrashThreeTimesReturnsToFirstParticipant() {
        compose.onNodeWithText("Вынести мусор").performClick()

        completeChore()
        compose.onNodeWithText("Сейчас очередь: Данияр").assertIsDisplayed()
        completeChore()
        compose.onNodeWithText("Сейчас очередь: Нурмек").assertIsDisplayed()
        completeChore()

        compose.onNodeWithText("Сейчас очередь: Асхат").assertIsDisplayed()
        compose.onNodeWithText("Ход 1 из 3").assertIsDisplayed()
        compose.onNodeWithText("Последний исполнитель: Нурмек").assertIsDisplayed()
    }

    @Test
    fun twoQuickCardClicksNeedOnlyOneBack() {
        val card = compose.onNode(hasText("Вынести мусор") and hasClickAction())
        val click = card.fetchSemanticsNode().config[SemanticsActions.OnClick].action!!

        // Both events reach the list before it has time to recompose.
        compose.runOnIdle {
            click()
            click()
        }

        compose.onNodeWithText("Выполнено").assertExists()
        compose.runOnIdle {
            assertEquals(ChoreRoutes.LIST, navController.previousBackStackEntry?.destination?.route)
        }
        compose.onNodeWithContentDescription("Назад").performClick()
        compose.onNodeWithText("Выберите дело, чтобы посмотреть очередь").assertIsDisplayed()
    }

    @Test
    fun missingChoreCanReturnToList() {
        compose.runOnIdle { navController.navigate(ChoreRoutes.detail("missing")) }

        compose.onNodeWithText("Дело не найдено").assertIsDisplayed()
        compose.onNodeWithText("Выполнено").assertDoesNotExist()
        compose.onNodeWithText("К списку").performClick()

        assertCard("Вынести мусор", "Асхат")
        compose.runOnIdle { assertEquals(ChoreRoutes.LIST, navController.currentDestination?.route) }
    }

    @Test
    fun taskIdWithSpecialCharactersIsPassedWithoutChanges() {
        val taskId = "missing/id ?#% имя"
        compose.runOnIdle { navController.navigate(ChoreRoutes.detail(taskId)) }

        compose.onNodeWithText("Дело не найдено").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(taskId, navController.currentBackStackEntry?.arguments?.getString(ChoreRoutes.TASK_ID))
        }
    }

    private fun completeChore() {
        compose.onNodeWithText("Выполнено").performScrollTo().performClick()
    }

    private fun assertCard(title: String, participant: String) {
        compose.onNode(hasText(title) and hasText("Сейчас очередь: $participant"))
            .performScrollTo()
            .assertIsDisplayed()
    }
}
