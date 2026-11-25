package com.example.project.weather

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.project.WhoseTurnApplication
import com.example.project.weather.data.WeatherRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(val days: List<DayForecast>) : WeatherUiState
    data object Empty : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        retry()
    }

    fun retry() {
        _uiState.value = WeatherUiState.Loading
        viewModelScope.launch {
            try {
                val days = repository.loadForecast()
                _uiState.value = if (days.isEmpty()) WeatherUiState.Empty else WeatherUiState.Success(days)
            } catch (error: CancellationException) {
                throw error
            } catch (error: IOException) {
                Log.e(TAG, "Could not load weather", error)
                _uiState.value = WeatherUiState.Error("Не удалось загрузить прогноз")
            } catch (error: HttpException) {
                Log.e(TAG, "Weather service returned an error", error)
                _uiState.value = WeatherUiState.Error("Сервис погоды временно недоступен")
            } catch (error: Exception) {
                Log.e(TAG, "Unexpected weather error", error)
                _uiState.value = WeatherUiState.Error("Не удалось загрузить прогноз")
            }
        }
    }

    companion object {
        private const val TAG = "WeatherViewModel"

        val Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY]) as WhoseTurnApplication
                WeatherViewModel(app.weatherRepository)
            }
        }
    }
}
