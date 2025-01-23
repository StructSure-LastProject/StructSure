package fr.uge.structsure.startScan.presentation.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R
import fr.uge.structsure.components.Button
import fr.uge.structsure.startScan.domain.ScanViewModel
import fr.uge.structsure.startScan.presentation.sensors.status.SensorState
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.White

@Preview
@Composable
fun StructureSummaryPreview() {
    Box(Modifier.background(LightGray)) {
        StructureSummaryView(null)
    }
}

/**
 * Header of the Scan page that contains the name of the structure,
 * the button to edit its note and the structure weather.
 */
@Composable
fun StructureSummaryView(viewModel: ScanViewModel?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,

    ) {
        /* Structure name and note button */
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
        /* Structure weather */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(4) { index ->
                SensorWeather(index + 21, SensorState.entries[index % 4])
            }
        }
    }
}

/**
 * Small bean containing a colored circle and a number that tells how
 * many sensor are contained in a given state.
 * @param count the number of sensor for the given state
 * @param state (weather) state to display
 */
@Composable
private fun SensorWeather(count: Int, state: SensorState) {
    Row(
        modifier = Modifier
             .horizontalScroll(rememberScrollState())
            .height(40.dp)
            .background(color = White, shape = RoundedCornerShape(size = 50.dp))
            .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .border(2.dp, state.color.copy(alpha = 0.25F), CircleShape)
                .padding(2.dp)
                .clip(CircleShape)
                .background(state.color)
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
