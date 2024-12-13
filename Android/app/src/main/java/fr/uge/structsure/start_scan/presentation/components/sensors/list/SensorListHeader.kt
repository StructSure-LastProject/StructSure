package fr.uge.structsure.start_scan.presentation.components.sensors.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R
import fr.uge.structsure.bluetoothConnection.presentation.SmallButton
import fr.uge.structsure.ui.theme.Typography

@Composable
fun SensorListHeader() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Capteurs",
            style = Typography.titleLarge
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
        ) {
            SmallButton(R.drawable.arrow_down_narrow_wide, "Sort")
            SmallButton(R.drawable.filter, "Filter")
            SmallButton(R.drawable.plus, "Add", Color.White, Color.Black)
        }
    }
}
