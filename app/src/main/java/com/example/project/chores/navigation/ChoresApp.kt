package com.example.project.chores.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.project.R
import com.example.project.chores.ChoresViewModel
import com.example.project.chores.ui.ChoreDetailScreen
import com.example.project.chores.ui.ChoreListScreen
import com.example.project.chores.ui.ChoreNotFoundScreen
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ChoresApp(
    viewModel: ChoresViewModel,
    modifier: Modifier = Modifier,
) {
    val chores by viewModel.chores.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val resetMessage = stringResource(R.string.chores_reset_done)

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
                ChoreListScreen(
                    chores = chores,
                    onOpenChore = { taskId ->
                        navController.navigate(ChoreRoutes.detail(taskId)) {
                            launchSingleTop = true
                        }
                    },
                    onReset = {
                        viewModel.resetChores()
                        scope.launch { snackbarHostState.showSnackbar(resetMessage) }
                    },
                )
            }
            composable(
                route = ChoreRoutes.DETAIL,
                arguments = listOf(navArgument(ChoreRoutes.TASK_ID) { type = NavType.StringType }),
            ) { entry ->
                val taskId = entry.arguments?.getString(ChoreRoutes.TASK_ID)
                val chore = chores.find { it.id == taskId }

                if (chore != null) {
                    ChoreDetailScreen(
                        chore = chore,
                        onComplete = { viewModel.complete(chore.id) },
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
