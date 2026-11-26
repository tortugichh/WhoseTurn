package com.example.project.chores.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.project.R
import com.example.project.chores.ChoreDraft
import com.example.project.chores.ChoreIcon
import com.example.project.chores.RepeatOptions
import com.example.project.people.Person
import com.example.project.people.ui.PersonAvatar

@Composable
internal fun ChoreEditorDialog(
    @StringRes titleResource: Int,
    @StringRes confirmResource: Int,
    people: List<Person>,
    initial: ChoreDraft,
    onDismiss: () -> Unit,
    onConfirm: (ChoreDraft) -> Unit,
) {
    var title by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initial.title, selection = TextRange(initial.title.length)))
    }
    var selectedIcon by rememberSaveable { mutableStateOf(initial.icon) }
    var memberIds by rememberSaveable { mutableStateOf(initial.memberIds) }
    var repeatDays by rememberSaveable { mutableStateOf(initial.repeatDays) }
    val focusRequester = remember { FocusRequester() }
    val trimmedTitle = title.text.trim()
    val canSave = trimmedTitle.isNotEmpty() && (people.isEmpty() || memberIds.isNotEmpty())
    val submit = {
        if (canSave) onConfirm(ChoreDraft(trimmedTitle, selectedIcon, memberIds, repeatDays))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(titleResource)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    label = { Text(stringResource(R.string.chore_title_label)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submit() }),
                )
                EditorSection(R.string.chore_icon_label) {
                    ChoreIconPicker(selectedIcon, onSelect = { selectedIcon = it })
                }
                if (people.isNotEmpty()) {
                    EditorSection(R.string.chore_members_label, hint = R.string.chore_members_hint) {
                        MemberPicker(people, memberIds, onChange = { memberIds = it })
                    }
                }
                EditorSection(R.string.chore_repeat_label) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        RepeatOptions.forEach { days ->
                            FilterChip(
                                selected = days == repeatDays,
                                onClick = { repeatDays = days },
                                label = { Text(repeatLabel(days)) },
                            )
                        }
                    }
                }
            }
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

@Composable
private fun EditorSection(
    @StringRes title: Int,
    @StringRes hint: Int? = null,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(title),
            style = MaterialTheme.typography.labelLarge,
        )
        if (hint != null) {
            Text(
                text = stringResource(hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        content()
    }
}

@Composable
private fun MemberPicker(
    people: List<Person>,
    selected: List<Long>,
    onChange: (List<Long>) -> Unit,
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        people.forEach { person ->
            val position = selected.indexOf(person.id)
            FilterChip(
                selected = position >= 0,
                onClick = {
                    onChange(if (position >= 0) selected - person.id else selected + person.id)
                },
                label = { Text(person.name) },
                leadingIcon = {
                    if (position >= 0) {
                        TurnNumber(position + 1)
                    } else {
                        PersonAvatar(person.name, Modifier.size(24.dp))
                    }
                },
            )
        }
    }
}

@Composable
private fun TurnNumber(number: Int) {
    Surface(
        modifier = Modifier.size(24.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = number.toString(), style = MaterialTheme.typography.labelMedium)
        }
    }
}
