package fr.uge.structsure.structuresPage.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.R
import fr.uge.structsure.components.Button
import fr.uge.structsure.structuresPage.data.StructureData
import fr.uge.structsure.structuresPage.domain.StructureViewModel
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.Red
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun StructureButtons(structure: StructureData, state: MutableState<StructureStates>, structureViewModel: StructureViewModel, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    when (state.value) {
        StructureStates.ONLINE -> {
            // Bouton de téléchargement
            DownloadButton(
                structureName = structure.name,
                onDownloadClick = {
                    state.value = StructureStates.DOWNLOADING

                    // Launch a coroutine to call the download method
                    coroutineScope.launch {
                        structureViewModel.downloadStructure(structure)
                        delay(2000)
                        state.value = StructureStates.AVAILABLE
                    }
                }
            )
        }

        /*StructureDownloadState.synchronizing -> {
            // Roue de chargement
            LoadingButton();
        }*/

        StructureStates.AVAILABLE -> {
            // Boutons "play" et "suppression"
            PlaySupButton(state, structure, structureViewModel, navController)
        }

        StructureStates.DOWNLOADING, StructureStates.UPLOADING -> {
            LoadingButton()
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

@Composable
fun PlaySupButton(
    state: MutableState<StructureStates>,
    structure: StructureData,
    structureViewModel: StructureViewModel,
    navController: NavController
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            id = R.drawable.play,
            description = "Lire",
            color = Black,
            background = LightGray,
            onClick = {
                navController.navigate("ScanPage?structureId=${structure.id}")
            }
        )
        Button(
            id = R.drawable.x,
            description = "Supprimer",
            color = Red,
            background = Red.copy(alpha = 0.05f),
            onClick = {
                structureViewModel.deleteStructure(structure.id)
                state.value = StructureStates.ONLINE
            }
        )
    }
}