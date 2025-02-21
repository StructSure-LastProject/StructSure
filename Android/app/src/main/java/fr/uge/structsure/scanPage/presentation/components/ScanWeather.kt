package fr.uge.structsure.scanPage.presentation.components


import android.annotation.SuppressLint
import android.content.res.Resources.getSystem
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import fr.uge.structsure.R
import fr.uge.structsure.components.Button
import fr.uge.structsure.scanPage.domain.ScanViewModel
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.White


private val Int.toDp: Dp get() = (this / getSystem().displayMetrics.density).toInt().dp
private val Int.toPx: Int get() = (this * getSystem().displayMetrics.density).toInt()

/**
 * Header of the Scan page that contains the name of the structure,
 * the button to edit its note and the structure weather.
 */
@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun ScanWeather(viewModel: ScanViewModel, scrollState: ScrollState) {
    var isSticky by remember { mutableStateOf(false) }
    val offset = (20 + 35 + 50).toPx // Size of the header + margins

    // Observe the state counts from the view model
    val stateCounts = viewModel.sensorStateCounts.observeAsState(emptyMap()).value ?: emptyMap()

    // Sticky Weather
    LaunchedEffect(scrollState.value) {
        isSticky = scrollState.value > offset
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
                text = "Viaduc de Sylans",
                style = MaterialTheme.typography.titleLarge
            )

            Button(
                R.drawable.notepad_text,
                "Note de l'ouvrage",
                background = White
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
