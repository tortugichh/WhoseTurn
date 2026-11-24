package com.example.project.chores

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.project.WhoseTurnApplication
import com.example.project.chores.data.ChoresRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface ChoresUiState {
    data object Loading : ChoresUiState
    data class Ready(val chores: List<Chore>) : ChoresUiState
    data object Error : ChoresUiState
}

internal sealed interface ChoresEvent {
    data object TurnsReset : ChoresEvent
    data object WriteFailed : ChoresEvent
    data class Completed(val nextPerson: String) : ChoresEvent
    data class Deleted(val choreId: String) : ChoresEvent
}

class ChoresViewModel(private val repository: ChoresRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<ChoresUiState>(ChoresUiState.Loading)
    val uiState: StateFlow<ChoresUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<ChoresEvent>(Channel.BUFFERED)
    internal val events = eventChannel.receiveAsFlow()

    private var loadJob: Job? = null

    init {
        retry()
    }

    fun retry() {
        loadJob?.cancel()
        _uiState.value = ChoresUiState.Loading
        loadJob = viewModelScope.launch {
            repository.chores
                .catch { error ->
                    Log.e(TAG, "Could not load chores", error)
                    _uiState.value = ChoresUiState.Error
                }
                .collect { _uiState.value = ChoresUiState.Ready(it) }
        }
    }

    fun complete(taskId: String) = write {
        val nextPerson = repository.complete(taskId) ?: return@write
        eventChannel.send(ChoresEvent.Completed(nextPerson))
    }

    fun add(title: String, icon: ChoreIcon) = write {
        repository.add(title, icon)
    }

    fun rename(id: String, title: String, icon: ChoreIcon) = write {
        repository.rename(id, title, icon)
    }

    fun delete(id: String) = write {
        repository.delete(id)
        eventChannel.send(ChoresEvent.Deleted(id))
    }

    fun resetChores() = write {
        repository.resetTurns()
        eventChannel.send(ChoresEvent.TurnsReset)
    }

    private fun write(action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                action()
            } catch (error: SQLiteException) {
                Log.e(TAG, "Could not update chores", error)
                eventChannel.send(ChoresEvent.WriteFailed)
            }
        }
    }

    companion object {
        private const val TAG = "ChoresViewModel"

        val Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) as WhoseTurnApplication
                ChoresViewModel(app.choresRepository)
            }
        }
    }
}
