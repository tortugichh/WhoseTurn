package com.example.project.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.project.WhoseTurnApplication
import com.example.project.settings.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    val theme: StateFlow<ThemeMode?> = repository.theme
        .stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = null)

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch {
            try {
                repository.setTheme(mode)
            } catch (error: IOException) {
                Log.e(TAG, "Could not save theme", error)
            }
        }
    }

    companion object {
        private const val TAG = "SettingsViewModel"

        val Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) as WhoseTurnApplication
                SettingsViewModel(app.settingsRepository)
            }
        }
    }
}
