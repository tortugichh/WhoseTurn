package com.example.project.chores.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconToggleButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.project.R
import com.example.project.chores.ChoreIcon

@Composable
internal fun ChoreIconBadge(icon: ChoreIcon) {
    Surface(
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Box(contentAlignment = Alignment.Center) {
            ChoreIconImage(icon)
        }
    }
}

@Composable
internal fun ChoreIconPicker(
    selectedIcon: ChoreIcon,
    onSelect: (ChoreIcon) -> Unit,
) {
    val iconSize = 48.dp
    val spacing = 4.dp
    val icons = ChoreIcon.entries
    val fullRowWidth = iconSize * icons.size + spacing * (icons.size - 1)

    BoxWithConstraints {
        FlowRow(
            maxItemsInEachRow = if (maxWidth >= fullRowWidth) icons.size else 3,
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalArrangement = Arrangement.spacedBy(spacing),
        ) {
            icons.forEach { icon ->
                OutlinedIconToggleButton(
                    checked = icon == selectedIcon,
                    onCheckedChange = { onSelect(icon) },
                    modifier = Modifier.size(iconSize),
                ) {
                    ChoreIconImage(icon)
                }
            }
        }
    }
}

@Composable
private fun ChoreIconImage(icon: ChoreIcon) {
    val drawable = when (icon) {
        ChoreIcon.Trash -> R.drawable.ic_trash
        ChoreIcon.Dishes -> R.drawable.ic_dishes
        ChoreIcon.Plant -> R.drawable.ic_plant
        ChoreIcon.Broom -> R.drawable.ic_broom
        ChoreIcon.Laundry -> R.drawable.ic_laundry
        ChoreIcon.Home -> R.drawable.ic_home
    }
    val label = when (icon) {
        ChoreIcon.Trash -> R.string.chore_icon_trash
        ChoreIcon.Dishes -> R.string.chore_icon_dishes
        ChoreIcon.Plant -> R.string.chore_icon_plant
        ChoreIcon.Broom -> R.string.chore_icon_broom
        ChoreIcon.Laundry -> R.string.chore_icon_laundry
        ChoreIcon.Home -> R.string.chore_icon_home
    }
    Icon(
        painter = painterResource(drawable),
        contentDescription = stringResource(label),
    )
}
