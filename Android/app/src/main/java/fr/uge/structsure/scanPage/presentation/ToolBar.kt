package fr.uge.structsure.scanPage.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.R
import fr.uge.structsure.bluetooth.cs108.Cs108Connector
import fr.uge.structsure.components.BigButton
import fr.uge.structsure.components.BluetoothButton
import fr.uge.structsure.scanPage.domain.ScanState
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.LightGray

/**
 * Action bar or toolbar pinned at the bottom of the screen when doing
 * a scan. This bar contains a back button since the scan is not
 * started, the BLE button, the ability to start/pause/stop the scan
 * and a shortcut to display the Scan's note.
 * @param currentState state of the scan to adapt the buttons to
 * @param onPlayClick action to call when the play button is pressed
 * @param onPauseClick action to call when the pause button is pressed
 * @param onStopClick action to call when the pause button is pressed
 * @param onContentClick action when the not shortcut is pressed
 * @param connexionCS108 access to the CS108 scanner to monitor its state
 * @param navController enable to navigate between app's pages
 */
@Composable
fun ToolBar(
    currentState: ScanState,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    onContentClick: () -> Unit,
    connexionCS108: Cs108Connector,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = WindowInsets.Companion.navigationBars.asPaddingValues().calculateBottomPadding()
            )
            .height(100.dp)
            .background(Brush.verticalGradient(listOf(LightGray.copy(0f), LightGray, LightGray)))
            .padding(horizontal = 20.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        // Button Undo
        if (currentState == ScanState.NOT_STARTED) {
            BigButton({ navController.navigate("HomePage") {
                popUpTo(0) { inclusive = true } // Prevent going back
                launchSingleTop = true
            } }) {
                Image(
                    painter = painterResource(R.drawable.undo),
                    contentDescription = "Back",
                    contentScale = ContentScale.None
                )
            }
        } else {
            BluetoothButton(connexionCS108)
        }

        // Button Sync (Synchronisation)
        PlayPauseButton(currentState, onPlayClick, onPauseClick, onStopClick)

        // Button Notes
        BigButton(onContentClick) {
            Image(
                painter = painterResource(R.drawable.notepad_text),
                contentDescription = "Notes",
                contentScale = ContentScale.None
            )
        }
    }
}

/**
 * Multi-state button enable to play/pause/stop the scan.
 * @param state current state of the scan to which the button will adapt
 * @param onPlayClick action to call when the play button is pressed
 * @param onPauseClick action to call when the pause button is pressed
 * @param onStopClick action to call when the pause button is pressed
 */
@Composable
private fun PlayPauseButton(
    state: ScanState,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Row(
        Modifier
            .clip(RoundedCornerShape(size = 100.dp))
            .background(Black)
            .padding(5.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        /* Play Button */
        if (state != ScanState.STARTED) {
            ActionButton(R.drawable.play, "Start", onPlayClick)
        }

        /* Pause Button */
        if (state == ScanState.STARTED) {
            ActionButton(R.drawable.pause, "Pause", onPauseClick)
        }

        /* Stop Button */
        if (state == ScanState.STARTED || state == ScanState.PAUSED) {
            Spacer( Modifier
                .padding(vertical = 10.dp)
                .fillMaxHeight()
                .width(1.dp)
                .background(LightGray.copy(0.25f)) )
            ActionButton(R.drawable.stop, "Stop", onStopClick)
        }
    }
}

/**
 * Inner button of the PlayPauseButton component containing the icon,
 * the hit-box and the action to run on click.
 * @param icon the icon to display in the button
 * @param description text describing the button for accessibility
 * @param onClick action to run when the button is pressed
 */
@Composable
private fun ActionButton(icon: Int, description: String, onClick: () -> Unit) {
    Image(
        painter = painterResource(icon),
        contentDescription = description,
        modifier = Modifier
            .clickable { onClick() }
            .padding(15.dp)
            .size(35.dp)
    )
}