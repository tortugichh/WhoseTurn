package com.example.project.chores

import androidx.lifecycle.ViewModel
import com.example.project.chores.data.loadDemoChores
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChoresViewModel : ViewModel() {
    private val _chores = MutableStateFlow(loadDemoChores())

    val chores: StateFlow<List<Chore>> = _chores.asStateFlow()
}