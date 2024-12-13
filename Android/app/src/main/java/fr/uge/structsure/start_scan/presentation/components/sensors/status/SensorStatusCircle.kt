package fr.uge.structsure.start_scan.presentation.components.sensors.status

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val ALPHA_BORDER = 0.2f
val STATUS_CIRCLE_WIDTH = 15.dp
val STATUS_CIRCLE_BORDER_WIDTH = 5.dp

@Preview(showBackground = true)
@Composable
fun SensorStatusCircle(sensorState: SensorState = SensorState.UNSCAN) {
    val borderColor = sensorState.color.copy(alpha = ALPHA_BORDER)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(STATUS_CIRCLE_WIDTH + STATUS_CIRCLE_BORDER_WIDTH)     // We draw the big transparent circle first
            .background(borderColor, shape = CircleShape)
    ) {
        Box(
            modifier = Modifier
                .size(STATUS_CIRCLE_WIDTH)
                .background(sensorState.color, shape = CircleShape)
        )
    }
}
