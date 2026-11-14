package com.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.chores.Chore
import com.example.project.chores.ChoresViewModel
import com.example.project.chores.ui.ResetChoresButton
import com.example.project.ui.theme.ProjectTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ProjectTheme {
                val choresViewModel: ChoresViewModel = viewModel()
                val chores by choresViewModel.chores.collectAsStateWithLifecycle()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ChoresOverview(
                        chores = chores,
                        onComplete = choresViewModel::complete,
                        onReset = choresViewModel::resetChores,
                        modifier = Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}

@Composable
private fun ChoresOverview(
    chores: List<Chore>,
    onComplete: (String) -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.chores_home_title),
                style = MaterialTheme.typography.headlineMedium,
            )
        }

        items(chores, key = { it.id }) { chore ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(chore.title, style = MaterialTheme.typography.titleLarge)
                    Text(stringResource(R.string.chore_current_assignee, chore.currentParticipant))
                    Text(stringResource(R.string.chore_turn_position, chore.turnNumber, chore.participants.size))

                    chore.lastCompletedBy?.let { name ->
                        Text(stringResource(R.string.chore_last_completed_by, name))
                    }

                    Button(onClick = { onComplete(chore.id) }) {
                        Text(stringResource(R.string.chore_complete))
                    }
                }
            }
        }

        item {
            ResetChoresButton(
                enabled = chores.any { it.lastCompletedBy != null },
                onReset = onReset,
            )
        }
    }
}