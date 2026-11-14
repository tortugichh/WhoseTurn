package com.example.project.chores.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.example.project.R

@Composable
fun ResetChoresButton(
    enabled: Boolean,
    onReset: () -> Unit,
) {
    var showConfirmation by rememberSaveable { mutableStateOf(false) }

    OutlinedButton(
        onClick = { showConfirmation = true },
        enabled = enabled,
    ) {
        Text(stringResource(R.string.chores_reset_action))
    }

    if (showConfirmation) {
        AlertDialog(
            onDismissRequest = { showConfirmation = false },
            title = { Text(stringResource(R.string.chores_reset_title)) },
            text = { Text(stringResource(R.string.chores_reset_description)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmation = false
                        onReset()
                    },
                ) {
                    Text(stringResource(R.string.chores_reset_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmation = false }) {
                    Text(stringResource(R.string.chores_reset_cancel))
                }
            },
        )
    }
}