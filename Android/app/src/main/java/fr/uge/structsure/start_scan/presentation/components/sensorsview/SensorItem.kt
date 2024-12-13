package fr.uge.structsure.start_scan.presentation.components.sensorsview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import fr.uge.structsure.ui.theme.Defaillant
import fr.uge.structsure.ui.theme.Ok
import fr.uge.structsure.ui.theme.Red
import fr.uge.structsure.ui.theme.Unknown
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import fr.uge.structsure.ui.theme.Typography


@Preview(showBackground = true)
@Composable
fun SensorItem(sensorName: String="Default", state: Int=-1) {
    // Définit les couleurs en fonction de l'état
    val stateColor = when (state) {
        0 -> Ok         //OK
        1 -> Red        //Nok
        2 -> Defaillant // Defaillant
        3 -> Unknown    // Non scanné la plupart du temps
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(15.dp)
                    .background(stateColor, shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = sensorName, style = Typography.bodyMedium)
        }
    }
}
