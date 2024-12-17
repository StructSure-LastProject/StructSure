package fr.uge.structsure.structuresPage.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R
import fr.uge.structsure.components.Button
import fr.uge.structsure.structuresPage.domain.StructureRepository
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.Red

@Composable
fun StructureButtons(name: String, state: StructureStates, structureRepository: StructureRepository) {
    when (state) {
        StructureStates.ONLINE -> {
            // Bouton de téléchargement
            DownloadButton(
                structureName = name,
                onDownloadClick = {
                    structureRepository.downloadStructure(name)
                }
            )
        }

        /*StructureDownloadState.synchronizing -> {
            // Roue de chargement
            LoadingButton();
        }*/

        StructureStates.AVAILABLE -> {
            // Boutons "play" et "suppression"
            PlaySupButton()
        }
    }
}

// @Preview(showBackground = true)
@Composable
fun DownloadButton(
    structureName: String, // Ajout du nom de l'ouvrage
    onDownloadClick: (String) -> Unit // Callback pour le clic
) {
    Button(
        id = R.drawable.download,
        description = "Télécharger",
        background = LightGray,
        color = Black,
        onClick = { onDownloadClick(structureName) } // Passer le nom de l'ouvrage
    )
}

@Preview(showBackground = true)
@Composable
fun LoadingButton() {
    IconButton({ }, Modifier.size(40.dp), false) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = Black,
            strokeWidth = 2.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlaySupButton() {
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