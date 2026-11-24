package com.example.project.chores.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.example.project.R

@Composable
internal fun ChoreTitleDialog(
    @StringRes titleResource: Int,
    @StringRes confirmResource: Int,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    initialTitle: String = "",
) {
    var title by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initialTitle, selection = TextRange(initialTitle.length)))
    }
    val focusRequester = remember { FocusRequester() }
    val trimmedTitle = title.text.trim()
    val submit = { if (trimmedTitle.isNotEmpty()) onConfirm(trimmedTitle) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(titleResource)) },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                label = { Text(stringResource(R.string.chore_title_label)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submit() }),
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        },
        confirmButton = {
            TextButton(onClick = submit, enabled = trimmedTitle.isNotEmpty()) {
                Text(stringResource(confirmResource))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.chore_dialog_cancel))
            }
        },
    )
}
