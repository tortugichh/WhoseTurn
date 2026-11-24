package com.example.project.people

import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.project.WhoseTurnApplication
import com.example.project.people.data.PeopleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface PeopleUiState {
    data object Loading : PeopleUiState
    data class Ready(val people: List<Person>) : PeopleUiState
    data object Error : PeopleUiState
}

internal sealed interface PeopleEvent {
    data object WriteFailed : PeopleEvent
}

class PeopleViewModel(private val repository: PeopleRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<PeopleUiState>(PeopleUiState.Loading)
    val uiState: StateFlow<PeopleUiState> = _uiState.asStateFlow()

    private val eventChannel = Channel<PeopleEvent>(Channel.BUFFERED)
    internal val events = eventChannel.receiveAsFlow()

    private var loadJob: Job? = null

    init {
        retry()
    }

    fun retry() {
        loadJob?.cancel()
        _uiState.value = PeopleUiState.Loading
        loadJob = viewModelScope.launch {
            repository.people
                .catch { error ->
                    Log.e(TAG, "Could not load people", error)
                    _uiState.value = PeopleUiState.Error
                }
                .collect { _uiState.value = PeopleUiState.Ready(it) }
        }
    }

    fun add(name: String) = write {
        repository.add(name)
    }

    private fun write(action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                action()
            } catch (error: SQLiteException) {
                Log.e(TAG, "Could not update people", error)
                eventChannel.send(PeopleEvent.WriteFailed)
            }
        }
    }

    companion object {
        private const val TAG = "PeopleViewModel"

        val Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) as WhoseTurnApplication
                PeopleViewModel(app.peopleRepository)
            }
        }
    }
}
