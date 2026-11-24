package com.example.project.people

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.project.WhoseTurnApplication
import com.example.project.people.data.PeopleRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

sealed interface PeopleUiState {
    data object Loading : PeopleUiState
    data class Ready(val people: List<Person>) : PeopleUiState
    data object Error : PeopleUiState
}

class PeopleViewModel(private val repository: PeopleRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<PeopleUiState>(PeopleUiState.Loading)
    val uiState: StateFlow<PeopleUiState> = _uiState.asStateFlow()

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
