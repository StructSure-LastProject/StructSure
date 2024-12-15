package fr.uge.structsure.start_scan.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R
import fr.uge.structsure.start_scan.domain.ScanState

/**
 * Composant ToolBar pour gérer les actions liées à l'état du scan.
 */
@Composable
fun ToolBar(
    currentState: ScanState,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    onSyncClick: () -> Unit,
    onContentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bouton Undo
        Column(
            modifier = Modifier
                .width(77.dp)
                .height(58.dp)
                .background(color = Variables.White, shape = RoundedCornerShape(size = 50.dp))
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.undo),
                contentDescription = "Undo",
                contentScale = ContentScale.None,
                modifier = Modifier.size(22.dp)
            )
        }

        // Boutons dynamiques en fonction de l'état actuel du scan
        when (currentState) {
            ScanState.NOT_STARTED -> {
                ActionButton(
                    iconRes = R.drawable.play,
                    description = "Play",
                    onClick = onPlayClick
                )
            }
            ScanState.STARTED -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionButton(
                        iconRes = R.drawable.pause,
                        description = "Pause",
                        onClick = onPauseClick
                    )
                    ActionButton(
                        iconRes = R.drawable.stop,
                        description = "Stop",
                        onClick = onStopClick
                    )
                }
            }
            ScanState.PAUSED -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionButton(
                        iconRes = R.drawable.play,
                        description = "Resume",
                        onClick = onPlayClick
                    )
                    ActionButton(
                        iconRes = R.drawable.stop,
                        description = "Stop",
                        onClick = onStopClick
                    )
                }
            }
            ScanState.STOPPED -> {
                ActionButton(
                    iconRes = R.drawable.play, //  redémarrer un scan
                    description = "Play",
                    onClick = onPlayClick
                )
            }
        }

        // Bouton Notes
        Column(
            modifier = Modifier
                .width(75.dp)
                .height(58.dp)
                .background(color = Variables.White, shape = RoundedCornerShape(size = 50.dp))
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.notepad_text),
                contentDescription = "Notes",
                contentScale = ContentScale.None,
                modifier = Modifier.size(28.dp).clickable { onContentClick() }
            )
        }
    }
}


/**
 * Bouton d'action générique utilisé dans la ToolBar.
 * - Chaque bouton a une icône et une description.
 */
@Composable
fun ActionButton(iconRes: Int, description: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .width(72.dp)
            .height(72.dp)
            .background(color = Variables.Black, shape = RoundedCornerShape(50.dp))
            .padding(20.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = description,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(32.dp)
        )
    }
}
