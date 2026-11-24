package com.example.project.people.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.R
import com.example.project.people.PeopleUiState
import com.example.project.people.PeopleViewModel
import com.example.project.people.Person
import com.example.project.ui.components.EmptyState

@Composable
fun PeopleRoute(
    modifier: Modifier = Modifier,
    viewModel: PeopleViewModel = viewModel(factory = PeopleViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    when (val current = state) {
        is PeopleUiState.Ready -> PeopleScreen(
            people = current.people,
            modifier = modifier,
        )
        PeopleUiState.Loading -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        PeopleUiState.Error -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(stringResource(R.string.people_load_error))
                Button(onClick = viewModel::retry) {
                    Text(stringResource(R.string.chores_retry))
                }
            }
        }
    }
}

@Composable
fun PeopleScreen(
    people: List<Person>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            PeopleHeader(isEmpty = people.isEmpty())
        }

        if (people.isEmpty()) {
            item {
                EmptyState(
                    icon = R.drawable.ic_people,
                    text = stringResource(R.string.people_empty),
                )
            }
        }

        itemsIndexed(people, key = { _, person -> person.id }) { index, person ->
            PersonRow(
                position = index + 1,
                person = person,
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Composable
private fun PeopleHeader(isEmpty: Boolean) {
    Column(
        modifier = Modifier.padding(bottom = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = stringResource(R.string.people_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        if (!isEmpty) {
            Text(
                text = stringResource(R.string.people_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PersonRow(
    position: Int,
    person: Person,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 64.dp)
                .padding(start = 16.dp, end = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.chore_participant_position, position),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            PersonAvatar(name = person.name, modifier = Modifier.size(40.dp))
            Text(
                text = person.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
