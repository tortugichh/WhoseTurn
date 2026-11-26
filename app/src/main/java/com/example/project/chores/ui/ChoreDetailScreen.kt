package com.example.project.chores.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.project.R
import com.example.project.chores.Chore
import com.example.project.chores.ChoreDraft
import com.example.project.chores.toDraft
import com.example.project.people.Person
import com.example.project.people.ui.PersonAvatar

@Composable
fun ChoreDetailScreen(
    chore: Chore,
    people: List<Person>,
    onComplete: () -> Unit,
    onEdit: (ChoreDraft) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var dialog by rememberSaveable(chore.id) { mutableStateOf<ChoreDialog?>(null) }
    val participant = chore.currentParticipant

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChoreIconBadge(chore.icon)
            Text(
                text = chore.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.headlineMedium,
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .semantics(mergeDescendants = true) {},
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    ChoreAssigneeLabel(
                        participant = participant,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    if (participant != null) {
                        Text(
                            text = stringResource(
                                R.string.chore_turn_position,
                                chore.turnNumber,
                                chore.participants.size,
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                    Text(
                        text = stringResource(
                            R.string.chore_schedule,
                            dueLabel(chore.dueInDays),
                            repeatLabel(chore.repeatDays),
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    chore.lastCompletedBy?.let { name ->
                        Text(
                            text = stringResource(R.string.chore_last_completed_by, name),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                if (participant != null) {
                    PersonAvatar(participant)
                }
            }
        }

        if (participant != null) {
            ParticipantRotation(chore = chore)
        }

        Button(
            onClick = onComplete,
            enabled = participant != null,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.chore_complete))
        }

        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { dialog = ChoreDialog.Edit }) {
                Text(stringResource(R.string.chore_edit_action))
            }
            TextButton(onClick = { dialog = ChoreDialog.Delete }) {
                Text(
                    text = stringResource(R.string.chore_delete_action),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }

    when (dialog) {
        ChoreDialog.Edit -> ChoreEditorDialog(
            titleResource = R.string.chore_edit_title,
            confirmResource = R.string.chore_edit_confirm,
            people = people,
            initial = chore.toDraft(),
            onDismiss = { dialog = null },
            onConfirm = { draft ->
                dialog = null
                onEdit(draft)
            },
        )
        ChoreDialog.Delete -> AlertDialog(
            onDismissRequest = { dialog = null },
            title = { Text(stringResource(R.string.chore_delete_title)) },
            text = {
                Text(text = chore.title, maxLines = 3, overflow = TextOverflow.Ellipsis)
            },
            confirmButton = {
                TextButton(onClick = {
                    dialog = null
                    onDelete()
                }) {
                    Text(
                        text = stringResource(R.string.chore_delete_action),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { dialog = null }) {
                    Text(stringResource(R.string.chore_dialog_cancel))
                }
            },
        )
        null -> Unit
    }
}

private enum class ChoreDialog { Edit, Delete }

@Composable
private fun ParticipantRotation(
    chore: Chore,
    modifier: Modifier = Modifier,
) {
    val nextIndex = chore.nextIndex

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = stringResource(R.string.chore_rotation_title),
            style = MaterialTheme.typography.titleMedium,
        )

        chore.participants.forEachIndexed { index, name ->
            val label = when (index) {
                chore.currentIndex -> stringResource(R.string.chore_participant_current)
                nextIndex -> stringResource(R.string.chore_participant_next)
                else -> null
            }
            ParticipantRow(
                position = index + 1,
                name = name,
                label = label,
                isCurrent = index == chore.currentIndex,
            )
        }
    }
}

@Composable
private fun ParticipantRow(
    position: Int,
    name: String,
    label: String?,
    isCurrent: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {},
        shape = MaterialTheme.shapes.medium,
        color = if (isCurrent) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.chore_participant_position, position),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
            )
            if (label != null) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
