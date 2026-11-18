package com.example.project.chores.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project.chores.ChoresViewModel
import com.example.project.chores.ui.ChoreListScreen

@Composable
fun ChoresApp(
    viewModel: ChoresViewModel,
    modifier: Modifier = Modifier,
) {
    val chores by viewModel.chores.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    Scaffold(modifier = modifier) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ChoreRoutes.LIST,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(ChoreRoutes.LIST) {
                ChoreListScreen(
                    chores = chores,
                    onOpenChore = {},
                    onReset = viewModel::resetChores,
                )
            }
        }
    }
}
