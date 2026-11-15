package com.example.project.chores.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.project.R
import com.example.project.chores.Chore

@Composable
fun ChoreCard(
    chore: Chore,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = chore.title,
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = stringResource(R.string.chore_current_assignee, chore.currentParticipant),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(R.string.chore_turn_position, chore.turnNumber, chore.participants.size),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
