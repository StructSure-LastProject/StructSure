package fr.uge.structsure.scanPage.presentation.components


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources.getSystem
import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import fr.uge.structsure.MainActivity.Companion.db
import fr.uge.structsure.R
import fr.uge.structsure.components.Button
import fr.uge.structsure.components.InputTextArea
import fr.uge.structsure.components.PopUp
import fr.uge.structsure.components.Title
import fr.uge.structsure.scanPage.domain.ScanState
import fr.uge.structsure.scanPage.domain.ScanViewModel
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.Red
import fr.uge.structsure.ui.theme.White
import kotlinx.coroutines.launch


val Int.toDp: Dp get() = (this / getSystem().displayMetrics.density).toInt().dp
val Int.toPx: Int get() = (this * getSystem().displayMetrics.density).toInt()

/**
 * Header of the Scan page that contains the name of the structure,
 * the button to edit its note and the structure weather.
 */
@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun ScanWeather(scanViewModel: ScanViewModel, scrollState: ScrollState, context: Context) {
    var isSticky by remember { mutableStateOf(false) }
    val offset = (20 + 35 + 50).toPx // Size of the header + margins

    // Observe the state counts from the view model
    val stateCounts = scanViewModel.sensorStateCounts.observeAsState(emptyMap()).value

    var showStructureNotePopup by remember { mutableStateOf(false) } // Control the structure note popup visibility

    val showToast: (String) -> Unit = { message ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    // Sticky Weather
    LaunchedEffect(scrollState.value) {
        isSticky = scrollState.value > offset
    }

    if (showStructureNotePopup && scanViewModel.currentScanState.value != ScanState.NOT_STARTED) {
        StructureNotePopUp(
            scanViewModel = scanViewModel,
            onSubmit = { showStructureNotePopup = false },
            onCancel = { showStructureNotePopup = false }
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth()
            .offset(y = if (isSticky) (scrollState.value - offset).toDp else 0.dp)
            .zIndex(100f)
            .background(LightGray)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    // Handle sticky header logic if needed
                    println(dragAmount)
                }
            },
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = scanViewModel.structure?.name?:"Ouvrage",
                style = MaterialTheme.typography.titleLarge
            )

            Button(
                R.drawable.notepad_text,
                "Note de l'ouvrage",
                background = White,
                onClick = {
                    if (scanViewModel.currentScanState.value != ScanState.NOT_STARTED) {
                        showStructureNotePopup = true
                    } else {
                        showToast("Veuillez lancer un scan avant d'ajouter une note")
                    }
                }
            )
        }
        Box {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SensorState.entries.forEach { state ->
                    val count = stateCounts[state] ?: 0
                    SensorBean(value = count.toString(), state = state)
                }
            }
            Box (
                Modifier
                    .offset(y = 40.dp)
                    .height(35.dp)
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(LightGray, LightGray.copy(0f))))
            )
        }
    }
}


@Composable
private fun StructureNotePopUp(
    scanViewModel: ScanViewModel,
    onSubmit: () -> Unit,
    onCancel: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val errorMessage by scanViewModel.noteErrorMessage.observeAsState()
    var structureNote by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        scanViewModel.structure?.let { structure ->
            structureNote = db.structureDao().getStructureNote(structure.id) ?: ""
        }
    }

    PopUp(onCancel) {
        Title("Note de l'ouvrage", false) {
            Button(
                R.drawable.check,
                "valider",
                MaterialTheme.colorScheme.onSurface,
                MaterialTheme.colorScheme.surface,
                onClick = {
                    coroutineScope.launch {
                        if (scanViewModel.updateStructureNote(structureNote)) {
                            onSubmit()
                        }
                    }
                }
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
        ) {
            InputTextArea(
                label = "Note",
                value = structureNote,
                placeholder = "Aucune note pour le moment",
            ) { s -> if (s.length <= 1000) structureNote = s }
        }

        errorMessage?.let {
            Text(
                text = it,
                color = Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}
