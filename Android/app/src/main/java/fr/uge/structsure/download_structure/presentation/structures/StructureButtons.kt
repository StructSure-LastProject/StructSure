package fr.uge.structsure.download_structure.presentation.structures

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R
import fr.uge.structsure.components.Button

@Preview(showBackground = true)
@Composable
fun StructureButtons(state : StructureDownloadState = StructureDownloadState.synchronizing){
    when (state) {
        StructureDownloadState.downloadable -> {
            // Bouton de téléchargement
            Button(
                id = R.drawable.download,
                description = "Télécharger",
                color = Color.Black,
                background = Color.LightGray,
                onClick = { /* Logic de téléchargement */ }
            )
        }
        StructureDownloadState.downloading, StructureDownloadState.synchronizing -> {
            // Roue de chargement
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = Color.Black,
                strokeWidth = 4.dp
            )
        }
        StructureDownloadState.downloaded -> {
            // Boutons "play" et "suppression"
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    id = R.drawable.play,
                    description = "Lire",
                    color = Color.Black,
                    background = Color.LightGray,
                    onClick = { /* Logic de lecture */ }
                )
                Button(
                    id = R.drawable.x,
                    description = "Supprimer",
                    color = Color.Red,
                    background = Color.Red.copy(alpha = 0.05f),
                    onClick = { /* Logic de suppression */ }
                )
            }
        }
    }
}