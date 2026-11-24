package com.example.project.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.example.project.settings.data.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class SettingsRepositoryTest {
    @get:Rule
    val folder = TemporaryFolder()

    private val file: File by lazy { File(folder.root, "settings.preferences_pb") }

    private fun openStore(scope: CoroutineScope): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(scope = scope, produceFile = { file })

    @Test
    fun themeIsSystemWhenNothingIsSaved() = runBlocking {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val repository = SettingsRepository(openStore(scope))

        assertEquals(ThemeMode.System, repository.theme.first())
        scope.cancel()
    }

    @Test
    fun savedThemeIsReadAfterReopening() = runBlocking {
        val firstScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        SettingsRepository(openStore(firstScope)).setTheme(ThemeMode.Dark)
        firstScope.cancel()

        val secondScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val reopened = SettingsRepository(openStore(secondScope))

        assertEquals(ThemeMode.Dark, reopened.theme.first())
        secondScope.cancel()
    }

    @Test
    fun unknownSavedValueFallsBackToSystem() {
        assertEquals(ThemeMode.System, ThemeMode.fromKey("purple"))
        assertEquals(ThemeMode.System, ThemeMode.fromKey(null))
        assertEquals(ThemeMode.Light, ThemeMode.fromKey("light"))
    }
}
