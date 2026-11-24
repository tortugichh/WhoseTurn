package com.example.project.people.ui

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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import com.example.project.R

const val PERSON_NAME_MAX_LENGTH = 30

@Composable
internal fun PersonNameDialog(
    @StringRes titleResource: Int,
    @StringRes confirmResource: Int,
    takenNames: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    initialName: String = "",
) {
    var name by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initialName, selection = TextRange(initialName.length)))
    }
    val focusRequester = remember { FocusRequester() }
    val trimmedName = name.text.trim()
    val isTaken = takenNames.any { it.equals(trimmedName, ignoreCase = true) } &&
        !trimmedName.equals(initialName, ignoreCase = true)
    val canSave = trimmedName.isNotEmpty() && !isTaken
    val submit = { if (canSave) onConfirm(trimmedName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(titleResource)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { if (it.text.length <= PERSON_NAME_MAX_LENGTH) name = it },
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                label = { Text(stringResource(R.string.people_name_label)) },
                singleLine = true,
                isError = isTaken,
                supportingText = if (isTaken) {
                    { Text(stringResource(R.string.people_name_taken)) }
                } else {
                    null
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { submit() }),
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        },
        confirmButton = {
            TextButton(onClick = submit, enabled = canSave) {
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
