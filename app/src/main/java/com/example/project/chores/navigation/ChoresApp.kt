package com.example.project.chores.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.project.R
import com.example.project.chores.Chore
import com.example.project.chores.ChoresEvent
import com.example.project.chores.ChoresUiState
import com.example.project.chores.ChoresViewModel
import com.example.project.chores.ui.ChoreDetailScreen
import com.example.project.chores.ui.ChoreListScreen
import com.example.project.chores.ui.ChoreNotFoundScreen
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChoresApp(
    viewModel: ChoresViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current
    val hapticFeedback = LocalHapticFeedback.current

    LaunchedEffect(viewModel, navController, resources, hapticFeedback) {
        var snackbarJob: Job? = null
        viewModel.events.collect { event ->
            val message = when (event) {
                ChoresEvent.WriteFailed -> resources.getString(R.string.chores_write_error)
                is ChoresEvent.Completed -> {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    resources.getString(R.string.chore_complete_feedback, event.nextPerson)
                }
                is ChoresEvent.Deleted -> {
                    val currentEntry = navController.currentBackStackEntry
                    if (currentEntry?.destination?.route == ChoreRoutes.DETAIL &&
                        currentEntry.arguments?.getString(ChoreRoutes.TASK_ID) == event.choreId
                    ) {
                        navController.popBackStack(ChoreRoutes.LIST, inclusive = false)
                    }
                    null
                }
            }
            if (message != null) {
                snackbarJob?.cancel()
                snackbarJob = launch { snackbarHostState.showSnackbar(message) }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (backStackEntry?.destination?.route == ChoreRoutes.DETAIL) {
                TopAppBar(
                    title = { Text(stringResource(R.string.chore_detail_title)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                painter = painterResource(R.drawable.ic_arrow_back),
                                contentDescription = stringResource(R.string.chore_navigate_back),
                            )
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ChoreRoutes.LIST,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(ChoreRoutes.LIST) {
                WithChores(state, viewModel::retry) { chores ->
                    ChoreListScreen(
                        chores = chores,
                        onOpenChore = { taskId ->
                            navController.navigate(ChoreRoutes.detail(taskId)) {
                                launchSingleTop = true
                            }
                        },
                        onAdd = viewModel::add,
                        onComplete = viewModel::complete,
                    )
                }
            }
            composable(
                route = ChoreRoutes.DETAIL,
                arguments = listOf(navArgument(ChoreRoutes.TASK_ID) { type = NavType.StringType }),
            ) { entry ->
                WithChores(state, viewModel::retry) { chores ->
                    val taskId = entry.arguments?.getString(ChoreRoutes.TASK_ID)
                    val chore = chores.find { it.id == taskId }

                    if (chore != null) {
                        ChoreDetailScreen(
                            chore = chore,
                            onComplete = { viewModel.complete(chore.id) },
                            onRename = { title, icon -> viewModel.rename(chore.id, title, icon) },
                            onDelete = { viewModel.delete(chore.id) },
                        )
                    } else {
                        ChoreNotFoundScreen(
                            onBackToList = { navController.popBackStack(ChoreRoutes.LIST, false) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WithChores(
    state: ChoresUiState,
    onRetry: () -> Unit,
    content: @Composable (List<Chore>) -> Unit,
) {
    when (state) {
        is ChoresUiState.Ready -> content(state.chores)
        ChoresUiState.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        ChoresUiState.Error -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(stringResource(R.string.chores_load_error))
                Button(onClick = onRetry) {
                    Text(stringResource(R.string.chores_retry))
                }
            }
        }
    }
}
