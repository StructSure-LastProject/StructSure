package fr.uge.structsure.download_structure.presentation.structures

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R
import fr.uge.structsure.components.Button
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.Red

@Preview(showBackground = true)
@Composable
fun StructureButtons(state: StructureDownloadState = StructureDownloadState.downloadable) {
    when (state) {
        StructureDownloadState.downloadable -> {
            // Bouton de téléchargement
            Button(
                id = R.drawable.download,
                description = "Télécharger",
                background = LightGray,
                color = Black,
                onClick = { /* Logic de téléchargement */ }
            )
        }

        StructureDownloadState.downloading, StructureDownloadState.synchronizing -> {
            // Roue de chargement
            IconButton({ }, Modifier.size(40.dp), false) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Black,
                    strokeWidth = 2.dp
                )
            }
        }

        StructureDownloadState.downloaded -> {
            // Boutons "play" et "suppression"
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    id = R.drawable.play,
                    description = "Lire",
                    color = Black,
                    background = LightGray,
                    onClick = { /* Logic de lecture */ }
                )
                Button(
                    id = R.drawable.x,
                    description = "Supprimer",
                    color = Red,
                    background = Red.copy(alpha = 0.05f),
                    onClick = { /* Logic de suppression */ }
                )
            }
        }
    }
}