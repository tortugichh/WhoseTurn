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
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.project.R
import com.example.project.people.PeopleEvent
import com.example.project.people.PeopleUiState
import com.example.project.people.PeopleViewModel
import com.example.project.people.Person
import com.example.project.ui.components.EmptyState
import kotlinx.coroutines.launch

@Composable
fun PeopleRoute(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: PeopleViewModel = viewModel(factory = PeopleViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val writeError = stringResource(R.string.chores_write_error)

    LaunchedEffect(viewModel, snackbarHostState) {
        viewModel.events.collect { event ->
            when (event) {
                PeopleEvent.WriteFailed -> launch { snackbarHostState.showSnackbar(writeError) }
            }
        }
    }

    when (val current = state) {
        is PeopleUiState.Ready -> PeopleScreen(
            people = current.people,
            onAdd = viewModel::add,
            onRename = viewModel::rename,
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
    onAdd: (String) -> Unit,
    onRename: (Long, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var renamingId by rememberSaveable { mutableStateOf<Long?>(null) }
    val renamingPerson = people.find { it.id == renamingId }
    val takenNames = people.map { it.name }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            PeopleHeader(isEmpty = people.isEmpty(), onAddClick = { showAddDialog = true })
        }

        if (people.isEmpty()) {
            item {
                EmptyState(
                    icon = R.drawable.ic_people,
                    text = stringResource(R.string.people_empty),
                    actionLabel = stringResource(R.string.people_add_action),
                    onAction = { showAddDialog = true },
                )
            }
        }

        itemsIndexed(people, key = { _, person -> person.id }) { index, person ->
            PersonRow(
                position = index + 1,
                person = person,
                onRenameClick = { renamingId = person.id },
                modifier = Modifier.animateItem(),
            )
        }
    }

    if (showAddDialog) {
        PersonNameDialog(
            titleResource = R.string.people_add_title,
            confirmResource = R.string.chore_add_confirm,
            takenNames = takenNames,
            onDismiss = { showAddDialog = false },
            onConfirm = { name ->
                showAddDialog = false
                onAdd(name)
            },
        )
    }

    if (renamingPerson != null) {
        PersonNameDialog(
            titleResource = R.string.people_rename_title,
            confirmResource = R.string.chore_rename_confirm,
            takenNames = takenNames,
            initialName = renamingPerson.name,
            onDismiss = { renamingId = null },
            onConfirm = { name ->
                renamingId = null
                onRename(renamingPerson.id, name)
            },
        )
    }
}

@Composable
private fun PeopleHeader(isEmpty: Boolean, onAddClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
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
        FilledTonalIconButton(onClick = onAddClick, modifier = Modifier.size(48.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = stringResource(R.string.people_add_action),
            )
        }
    }
}

@Composable
private fun PersonRow(
    position: Int,
    person: Person,
    onRenameClick: () -> Unit,
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
            IconButton(onClick = onRenameClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = stringResource(R.string.people_rename_action, person.name),
                )
            }
        }
    }
}
