package com.example.project.chores

import androidx.lifecycle.ViewModel
import com.example.project.chores.data.loadDemoChores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChoresViewModel : ViewModel() {
    private val _chores = MutableStateFlow(loadDemoChores())

    val chores: StateFlow<List<Chore>> = _chores.asStateFlow()

    fun complete(taskId: String) {
        _chores.update { currentChores ->
            currentChores.map { chore ->
                if (chore.id == taskId) {
                    chore.advance()
                } else {
                    chore
                }
            }
        }
    }
}