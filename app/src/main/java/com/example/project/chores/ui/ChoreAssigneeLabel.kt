package com.example.project.chores.ui

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import com.example.project.R

@Composable
internal fun ChoreAssigneeLabel(
    participant: String?,
    style: TextStyle,
    @StringRes label: Int = R.string.chore_current_assignee,
) {
    val hint = stringResource(R.string.chore_add_people_first)
    val accessibleText = if (participant == null) {
        hint
    } else {
        stringResource(R.string.chore_current_assignee, participant)
    }

    AnimatedContent(
        targetState = participant,
        modifier = Modifier.clearAndSetSemantics { text = AnnotatedString(accessibleText) },
        transitionSpec = {
            (fadeIn(tween(160)) togetherWith fadeOut(tween(100))).using(null)
        },
        label = "chore assignee",
    ) { name ->
        Text(
            text = if (name == null) hint else stringResource(label, name),
            style = style,
        )
    }
}
