package com.example.project.settings.ui

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.project.settings.ThemeMode
import com.example.project.ui.theme.ProjectTheme

@Preview(name = "Settings", showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    ProjectTheme {
        Surface {
            SettingsScreen(
                theme = ThemeMode.System,
                onThemeChange = {},
                canResetTurns = true,
                onResetTurns = {},
            )
        }
    }
}

@Preview(name = "Settings, dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DarkSettingsScreenPreview() {
    ProjectTheme(darkTheme = true) {
        Surface {
            SettingsScreen(
                theme = ThemeMode.Dark,
                onThemeChange = {},
                canResetTurns = false,
                onResetTurns = {},
            )
        }
    }
}
