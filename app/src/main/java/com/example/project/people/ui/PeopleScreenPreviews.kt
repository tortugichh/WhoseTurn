package com.example.project.people.ui

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.project.people.Person
import com.example.project.ui.theme.ProjectTheme

private val previewPeople = listOf(
    Person(id = 1, name = "Асхат"),
    Person(id = 2, name = "Данияр"),
    Person(id = 3, name = "Нурмек"),
)

@Preview(name = "Three people", showBackground = true)
@Preview(name = "Three people, dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PeopleScreenPreview() {
    ProjectTheme {
        Surface {
            PeopleScreen(people = previewPeople, onAdd = {}, onRename = { _, _ -> }, onDelete = {})
        }
    }
}

@Preview(name = "No people", showBackground = true)
@Composable
private fun EmptyPeopleScreenPreview() {
    ProjectTheme {
        Surface {
            PeopleScreen(people = emptyList(), onAdd = {}, onRename = { _, _ -> }, onDelete = {})
        }
    }
}
