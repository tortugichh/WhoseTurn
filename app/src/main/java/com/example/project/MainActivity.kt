package com.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.chores.ChoresViewModel
import com.example.project.chores.navigation.ChoresApp
import com.example.project.ui.theme.ProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ProjectTheme {
                val choresViewModel: ChoresViewModel = viewModel(factory = ChoresViewModel.Factory)
                ChoresApp(viewModel = choresViewModel)
            }
        }
    }
}
