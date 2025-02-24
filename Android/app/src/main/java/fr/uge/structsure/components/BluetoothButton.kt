package fr.uge.structsure.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import fr.uge.structsure.R
import fr.uge.structsure.bluetooth.cs108.Cs108Connector
import fr.uge.structsure.bluetooth.presentation.BluetoothPage
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.Red
import fr.uge.structsure.ui.theme.White


/**
 * Bluetooth button
 * This button shows the battery level in percentage of the RFID reader device CS108.
 * This button shows the bluetooth state
 */
@Composable
fun BluetoothButton(connexion: Cs108Connector, inverted: Boolean = false) {
    val foreground = if (inverted) White else Black
    val background = if (inverted) Black else White
    val isBluetoothConnected = Cs108Connector.isBleConnected.observeAsState().value?:false

    var batteryLevel by remember { mutableIntStateOf(Cs108Connector.battery) }
    var showPopUp by remember { mutableStateOf(false) }

    // Determine color based on battery level
    val color = if (batteryLevel in 1..20) Red else foreground

    connexion.onBatteryChange { battery -> batteryLevel = battery }

    BigButton({ showPopUp = !showPopUp }, background) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Display appropriate Bluetooth icon based on connection status and battery level
            val bluetoothIcon = when {
                isBluetoothConnected -> R.drawable.bluetooth
                else -> R.drawable.bluetooth_not_connected
            }

            Image(
                painter = painterResource(id = bluetoothIcon),
                contentDescription = "bluetooth connection information",
                contentScale = ContentScale.None,
                colorFilter = ColorFilter.tint(color)
            )

            if (isBluetoothConnected && batteryLevel == -1) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp).padding(start = 5.dp, top = 5.dp),
                    color = color,
                    strokeWidth = 2.dp
                )
            } else if (isBluetoothConnected) {
                Text(
                    text = "$batteryLevel%",
                    style = TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 10.5.sp,
                        fontFamily = FontFamily(Font(R.font.poppins_regulat)),
                        fontWeight = FontWeight(600),
                        color = color,
                    ),
                    modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp)
                )
            }
        }
    }

    if (showPopUp) {
        Dialog(
            onDismissRequest = { showPopUp = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = true
            )
        ) {
            BluetoothPage(
                bleConnexion = connexion,
                onClose = { showPopUp = false },
            )
        }
    }
}

@Composable
fun BigButton(onClick: () -> Unit, background: Color = White, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .width(77.dp)
            .height(58.dp)
            .clip(RoundedCornerShape(45.dp))
            .background(background)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}