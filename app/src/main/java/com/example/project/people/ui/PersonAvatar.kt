package com.example.project.people.ui

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun PersonAvatar(name: String, modifier: Modifier = Modifier) {
    val url = remember(name) {
        Uri.parse("https://api.dicebear.com/9.x/initials/png")
            .buildUpon()
            .appendQueryParameter("seed", name)
            .toString()
    }
    var imageLoaded by remember(url) { mutableStateOf(false) }

    Surface(
        modifier = modifier.size(32.dp).clearAndSetSemantics {},
        shape = CircleShape,
        color = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (!imageLoaded) {
                Text(
                    text = name.firstOrNull()?.uppercase().orEmpty(),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onLoading = { imageLoaded = false },
                onSuccess = { imageLoaded = true },
                onError = { imageLoaded = false },
            )
        }
    }
}
