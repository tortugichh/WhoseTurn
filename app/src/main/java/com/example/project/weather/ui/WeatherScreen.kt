package com.example.project.weather.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.project.R
import com.example.project.ui.components.EmptyState
import com.example.project.weather.DayForecast
import com.example.project.weather.WeatherUiState
import com.example.project.weather.WeatherViewModel

@Composable
fun WeatherRoute(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = viewModel(factory = WeatherViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    WeatherScreen(state = state, onRetry = viewModel::retry, modifier = modifier)
}

@Composable
fun WeatherScreen(
    state: WeatherUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        WeatherUiState.Loading -> Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator()
        }
        WeatherUiState.Empty -> EmptyState(
            icon = R.drawable.ic_weather,
            text = stringResource(R.string.weather_empty),
            modifier = modifier,
            actionLabel = stringResource(R.string.chores_retry),
            onAction = onRetry,
        )
        is WeatherUiState.Error -> Column(
            modifier = modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(state.message, style = MaterialTheme.typography.bodyLarge)
            Button(onClick = onRetry, modifier = Modifier.padding(top = 12.dp)) {
                Text(stringResource(R.string.chores_retry))
            }
        }
        is WeatherUiState.Success -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                Text(
                    text = stringResource(R.string.weather_title),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            items(state.days, key = DayForecast::date) { day -> WeatherDayCard(day) }
        }
    }
}

@Composable
private fun WeatherDayCard(day: DayForecast) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AsyncImage(
                model = day.iconUrl,
                contentDescription = day.label,
                modifier = Modifier.size(48.dp),
                contentScale = ContentScale.Fit,
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(day.dayName.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleMedium)
                Text(day.label, style = MaterialTheme.typography.bodyMedium)
                Text(
                    stringResource(R.string.weather_temperatures, day.minTemp, day.maxTemp),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    stringResource(R.string.weather_rain, day.rainChance),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
