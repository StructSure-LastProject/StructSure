package fr.uge.structsure.start_scan.presentation.components.sensors.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.uge.structsure.ui.theme.Typography

@Preview(showBackground = true)
@Composable
fun SensorStatus(
    sensorStatus: SensorState = SensorState.UNSCAN,
    associatedText: String = "",
    typography: TextStyle = Typography.bodyMedium
) {
    Box(
        modifier = Modifier
            .background(Color.White, shape = RoundedCornerShape(20.dp))
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            SensorStatusCircle(sensorStatus)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = associatedText,
                style = typography
            )
        }
    }
}