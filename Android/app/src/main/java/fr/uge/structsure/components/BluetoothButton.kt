package fr.uge.structsure.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import fr.uge.structsure.MainActivity.Companion.csLibrary4A
import fr.uge.structsure.R
import fr.uge.structsure.bluetooth.cs108.Connexion
import fr.uge.structsure.bluetooth.presentation.BluetoothPage


/**
 * Bluetooth button
 * This button shows the battery level in percentage of the RFID reader device CS108.
 * This button shows the bluetooth state
 */
@Composable
fun BluetoothButton(connexion: Connexion) {
    val isBluetoothConnected = csLibrary4A.isBleConnected
    val batteryLevel = if (isBluetoothConnected) csLibrary4A.batteryLevel else 0
    var showPopUp by remember { mutableStateOf(false) }

    // Determine color based on battery level
    val color = if (batteryLevel <= 20) Color.Red else Color.Black

    IconButton(
        modifier = Modifier
            .width(77.dp)
            .height(58.dp)
            .background(color = Color.White, shape = RoundedCornerShape(size = 45.dp)),
        onClick = {
            showPopUp = !showPopUp
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Display appropriate Bluetooth icon based on connection status and battery level
            val bluetoothIcon = when {
                isBluetoothConnected && batteryLevel <= 20 -> R.drawable.red_bluetooth
                isBluetoothConnected -> R.drawable.bluetooth
                else -> R.drawable.bluetooth_not_connected
            }

            Image(
                painter = painterResource(id = bluetoothIcon),
                contentDescription = "bluetooth connection information",
                contentScale = ContentScale.None
            )

            if (isBluetoothConnected) {
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