package com.example.project.chores.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.project.R

@Composable
internal fun repeatLabel(days: Int): String = when (days) {
    1 -> stringResource(R.string.chore_repeat_daily)
    7 -> stringResource(R.string.chore_repeat_weekly)
    14 -> stringResource(R.string.chore_repeat_two_weeks)
    else -> stringResource(R.string.chore_repeat_days, days)
}

@Composable
internal fun dueLabel(dueInDays: Int): String = when {
    dueInDays < 0 -> stringResource(R.string.chore_due_overdue, -dueInDays)
    dueInDays == 0 -> stringResource(R.string.chore_due_today)
    dueInDays == 1 -> stringResource(R.string.chore_due_tomorrow)
    else -> stringResource(R.string.chore_due_in, dueInDays)
}

@Composable
internal fun ChoreDueLabel(
    dueInDays: Int,
    style: TextStyle,
    modifier: Modifier = Modifier,
) {
    Text(
        text = dueLabel(dueInDays),
        modifier = modifier,
        style = style,
        color = when {
            dueInDays < 0 -> MaterialTheme.colorScheme.error
            dueInDays == 0 -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        },
        fontWeight = if (dueInDays <= 0) FontWeight.SemiBold else FontWeight.Normal,
    )
}
