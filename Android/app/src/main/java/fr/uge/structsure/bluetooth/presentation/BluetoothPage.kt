package fr.uge.structsure.bluetooth.presentation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.csl.cslibrary4a.ReaderDevice
import fr.uge.structsure.R
import fr.uge.structsure.bluetooth.cs108.Cs108Connector
import fr.uge.structsure.components.Button
import fr.uge.structsure.components.Title
import fr.uge.structsure.ui.theme.White

@Composable
fun BluetoothPage(bleConnexion: Cs108Connector, onClose: () -> Unit) {
    bleConnexion.start()
    val bleEnabled = Cs108Connector.isBleEnabled.observeAsState()

    Column( // PopUp
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Bottom),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = Color(0x40000000))
    ) {
        Column( // Pairing pane
            verticalArrangement = Arrangement.spacedBy(35.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .shadow(elevation = 50.dp, spotColor = Color(0x40333333), ambientColor = Color(0x40333333))
                .fillMaxWidth()
                .background(color = White, shape = RoundedCornerShape(25.dp, 25.dp, 0.dp, 0.dp))
                .padding(start = 25.dp, top = 25.dp, end = 25.dp, bottom = 25.dp)
        ) {
            PaneHeader {
                bleConnexion.stop()
                onClose.invoke()
            }
            val devices = remember { Cs108Connector.devices }
            if (bleEnabled.value == false) {
                BleDisabled()
            } else if (devices.value.isEmpty()) {
                NoBleDevice()
            } else {
                DevicesList(devices.value) { device ->
                    if (!device.isConnected) bleConnexion.connect(device) else bleConnexion.disconnect()
                }
            }
        }
    }
}

/**
 * Title and close button of the pane
 * @param onClick action to run when the close button is pressed
 */
@Composable
private fun PaneHeader(onClick: () -> Unit) {
    Title("Appairage") {
        Button(R.drawable.x, "close", MaterialTheme.colorScheme.onSurface, MaterialTheme.colorScheme.surface, onClick)
    }
}

/**
 * Device card to show available and connected devices
 * @param device the device to display
 * @param onClick callback once the card is pressed
 */
@Composable
private fun Device(device: ReaderDevice, onClick: () -> Unit) {
    val connected = device.isConnected // TODO change to device.connected
    val fg = if (connected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface
    val bg = if (connected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.surface
    Row(
        Modifier
            .clip(RoundedCornerShape(size = 20.dp))
            .background(bg)
            .clickable(true, onClick=onClick)
            .padding(start = 20.dp, top = 15.dp, end = 20.dp, bottom = 15.dp)
    ) {
        Column( // Name and Address
            Modifier.fillMaxWidth().weight(2f),
            verticalArrangement = Arrangement.spacedBy((-5).dp, Alignment.Top),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(device.name, color=fg, style = typography.headlineMedium)
            Text(device.address, Modifier.alpha(.5f), color=fg, style = typography.bodyMedium)
        }
        if (connected) {
            IconButton(
                onClick,
                Modifier.size(40.dp).background(color = fg, shape = RoundedCornerShape(size = 50.dp)),
                false
            ) {
                Icon(painterResource(R.drawable.check), "close", Modifier.size(20.dp),bg)
            }
        }
    }
}

/**
 * Displays a list of devices in one column and call the given
 * function if one device tile is clicked
 * @param devices the list of devices to display
 * @param callback the action to run when a device is clicked
 */
@Composable
private fun DevicesList(devices:  List<ReaderDevice>, callback: (ReaderDevice) -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start
    ) {
        items(devices) { device ->
            Device(device) { callback(device) }
        }
    }
}

@Composable
private fun BleDisabled() {
    Error(R.drawable.bluetooth_not_connected, "Bluetooth désactivé", "Activez le Bluetooth dans les paramètres du smartphone pour commencer l’appairage")
}

@Composable
private fun NoBleDevice() {
    Error(R.drawable.bluetooth_not_found, "Aucun appareil trouvé pour le moment", "Assurez vous que l’interrogateur est en mode “appairage”")
}

@Composable
private fun Error(@DrawableRes icon: Int, title: String, description: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(start = 38.dp, end = 38.dp, bottom = 25.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = "Bluetooth alert icon",
            Modifier
                .width(72.dp)
                .height(82.dp)
                .padding(5.dp)
        )
        Text(title, style = typography.titleLarge, textAlign = TextAlign.Center)
        Text(description, style = typography.bodyMedium, textAlign = TextAlign.Center)
    }
}