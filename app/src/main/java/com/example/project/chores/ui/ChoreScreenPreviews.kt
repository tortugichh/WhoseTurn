package com.example.project.chores.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.project.chores.Chore
import com.example.project.chores.data.loadDemoChores
import com.example.project.ui.theme.ProjectTheme

private val initialChores: List<Chore> = loadDemoChores()

private val choresAfterTrash: List<Chore> = initialChores.map { chore ->
    if (chore.id == "trash") chore.advance() else chore
}

@Composable
private fun PreviewSurface(content: @Composable () -> Unit) {
    ProjectTheme {
        Surface(content = content)
    }
}

@Preview(name = "List / initial", showBackground = true)
@Composable
private fun ChoreListScreenInitialPreview() {
    PreviewSurface {
        ChoreListScreen(
            chores = initialChores,
            onOpenChore = {},
            onReset = {},
            onAdd = { _, _ -> },
            onComplete = {},
        )
    }
}

@Preview(name = "List / after completion", showBackground = true)
@Preview(
    name = "List / after completion / dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun ChoreListScreenCompletedPreview() {
    PreviewSurface {
        ChoreListScreen(
            chores = choresAfterTrash,
            onOpenChore = {},
            onReset = {},
            onAdd = { _, _ -> },
            onComplete = {},
        )
    }
}

@Preview(name = "Card / before completion", showBackground = true)
@Composable
private fun ChoreCardInitialPreview() {
    PreviewSurface {
        ChoreCard(
            chore = initialChores.first(),
            onClick = {},
            onComplete = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "Card / after completion", showBackground = true)
@Composable
private fun ChoreCardCompletedPreview() {
    PreviewSurface {
        ChoreCard(
            chore = choresAfterTrash.first(),
            onClick = {},
            onComplete = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "Details / before completion", showBackground = true)
@Composable
private fun ChoreDetailScreenInitialPreview() {
    PreviewSurface {
        ChoreDetailScreen(
            chore = initialChores.first(),
            onComplete = {},
            onRename = { _, _ -> },
            onDelete = {},
        )
    }
}

@Preview(name = "Details / after completion", showBackground = true)
@Preview(name = "Details / large text", showBackground = true, fontScale = 2f)
@Composable
private fun ChoreDetailScreenCompletedPreview() {
    PreviewSurface {
        ChoreDetailScreen(
            chore = choresAfterTrash.first(),
            onComplete = {},
            onRename = { _, _ -> },
            onDelete = {},
        )
    }
}
