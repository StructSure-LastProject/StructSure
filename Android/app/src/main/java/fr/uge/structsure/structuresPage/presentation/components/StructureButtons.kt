package fr.uge.structsure.structuresPage.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.R
import fr.uge.structsure.components.Button
import fr.uge.structsure.structuresPage.domain.StructureViewModel
import fr.uge.structsure.structuresPage.domain.StructureWithState
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.Red


@Composable
fun StructureButtons(structure: StructureWithState, structureViewModel: StructureViewModel, navController: NavController) {
    val state by structure.state.observeAsState(initial = StructureStates.ONLINE)

    when (state) {
        StructureStates.ONLINE, null -> {
            DownloadButton(
                structureName = structure.name,
                onDownloadClick = { structureViewModel.download(structure) }
            )
        }

        StructureStates.AVAILABLE -> {
            PlaySupButton(structure, structureViewModel, navController)
        }

        StructureStates.DOWNLOADING, StructureStates.UPLOADING -> {
            LoadingButton()
        }
    }
}

@Composable
fun DownloadButton(
    structureName: String,
    onDownloadClick: (String) -> Unit
) {
    Button(
        id = R.drawable.download,
        description = "Télécharger",
        background = LightGray,
        color = Black,
        onClick = { onDownloadClick(structureName) }
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
    structure: StructureWithState,
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
                navController.navigate("ScanPage?structureId=${structure.id}") {
                    popUpTo(0) { inclusive = true } // Prevent going back
                    launchSingleTop = true
                }
            }
        )
        Button(
            id = R.drawable.x,
            description = "Supprimer",
            color = Red,
            background = Red.copy(alpha = 0.05f),
            onClick = {
                structureViewModel.delete(structure.id)
                structure.state.value = StructureStates.ONLINE
            }
        )
    }
}