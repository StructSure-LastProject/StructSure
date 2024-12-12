package fr.uge.structsure.start_scan.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import fr.uge.structsure.R
import fr.uge.structsure.bluetoothConnection.presentation.SmallButton

@Composable
fun SensorsListView(
    modifier: Modifier = Modifier
) {
    var isSensorListVisible by remember { mutableStateOf(true) }
    val sensorBackgroundColors = remember { mutableStateListOf(*Array(5) { Variables.White }) }

    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start,
        modifier = modifier.padding(start = 20.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Capteur",
                style = MaterialTheme.typography.headlineMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SmallButton(R.drawable.arrow_down_narrow_wide, "Sort")
                SmallButton(R.drawable.filter, "Filter")
                SmallButton(R.drawable.plus, "Add")
            }
        }
    }
}
