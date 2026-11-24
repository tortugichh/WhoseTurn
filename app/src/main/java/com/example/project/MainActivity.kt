package com.example.project

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.chores.ChoresViewModel
import com.example.project.chores.navigation.ChoresApp
import com.example.project.settings.SettingsViewModel
import com.example.project.settings.ThemeMode
import com.example.project.ui.theme.ProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)
            val themeMode by settingsViewModel.theme.collectAsStateWithLifecycle()

            themeMode?.let { mode ->
                val darkTheme = mode.isDark()

                LaunchedEffect(darkTheme) {
                    val barStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme }
                    enableEdgeToEdge(statusBarStyle = barStyle, navigationBarStyle = barStyle)
                }

                ProjectTheme(darkTheme = darkTheme) {
                    val choresViewModel: ChoresViewModel = viewModel(factory = ChoresViewModel.Factory)
                    ChoresApp(viewModel = choresViewModel)
                }
            }
        }
    }
}

@Composable
private fun ThemeMode.isDark(): Boolean = when (this) {
    ThemeMode.System -> isSystemInDarkTheme()
    ThemeMode.Light -> false
    ThemeMode.Dark -> true
}
