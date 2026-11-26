package com.example.project.chores.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.project.R
import com.example.project.chores.Chore
import com.example.project.chores.ChoreDraft
import com.example.project.chores.newChoreDraft
import com.example.project.people.Person

@Composable
fun ChoreListScreen(
    chores: List<Chore>,
    people: List<Person>,
    onOpenChore: (String) -> Unit,
    onAdd: (ChoreDraft) -> Unit,
    onComplete: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = stringResource(R.string.chores_home_title),
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        text = stringResource(
                            if (chores.isEmpty()) R.string.chore_list_empty else R.string.chore_list_subtitle,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                FilledTonalIconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.size(48.dp),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = stringResource(R.string.chore_add_action),
                    )
                }
            }
        }

        items(chores, key = { it.id }) { chore ->
            ChoreCard(
                chore = chore,
                onClick = { onOpenChore(chore.id) },
                onComplete = { onComplete(chore.id) },
                modifier = Modifier.animateItem(),
            )
        }
    }

    if (showAddDialog) {
        ChoreEditorDialog(
            titleResource = R.string.chore_add_title,
            confirmResource = R.string.chore_add_confirm,
            people = people,
            initial = remember { newChoreDraft(people, chores) },
            onDismiss = { showAddDialog = false },
            onConfirm = { draft ->
                showAddDialog = false
                onAdd(draft)
            },
        )
    }
}
