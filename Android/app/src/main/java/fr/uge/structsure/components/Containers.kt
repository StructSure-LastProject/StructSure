package fr.uge.structsure.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import fr.uge.structsure.R
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.White

@Composable
@Preview
fun ContainersPreview() {
    Title("Titre") {
        Button(R.drawable.x, "close")
    }
}

/**
 * Title expected to be used at the beginning of a section that
 * contains a title text and buttons.
 * @param text title of the section
 * @param content buttons to add in the title
 */
@Composable
fun Title (
    text: String,
    indent: Boolean = true,
    content: @Composable () -> Unit = {}
) {
    Row(Modifier.padding(start = if (indent) 20.dp else 0.dp)) {
        Text(text,
            style = MaterialTheme.typography.titleLarge,
            modifier= Modifier
                .fillMaxWidth()
                .weight(2f))
        content()
    }
}

/**
 * Dialog or PopUp (it depends how you prefer to call it) that makes
 * the background darker and displays a white centered window that
 * contains the given content.
 *
 * Recommendations:
 * Create a mutable boolean to save the popup visibility: 'var visible by remember { mutableStateOf(true) }'
 * Add a blur modifier to the container that will be behind the popup: 'Modifier.blur(radius = if (visible) 10.dp else 0.dp)'
 * Place the popup component in a if: 'if (visible) PopUp(Modifier, onClose = { visible = false }) { ... }'
 * @param onClose callback when the user click outside the popup
 * @param content component to place inside the popup
 */
@Composable
fun PopUp(
    onClose: () -> Unit,
    content: @Composable () -> Unit = {},
) {
    val interactionSource by remember { mutableStateOf(MutableInteractionSource()) }
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = true
        )
    ) {
        Box(
            modifier = Modifier
                .imePadding()
                .fillMaxHeight()
                .fillMaxWidth()
                .clickable(interactionSource, null, onClick = onClose)
                .background(Black.copy(.25f))
                .padding(25.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, true) {
                        // Disable the ripple when clicking
                    }
                    .background(White)
                    .padding(25.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                content.invoke()
            }
        }
    }
}

/**
 * Two columns long array that can display details of a sensor.
 * @param color the color of the text to display
 * @param labelLeft the label of the first column
 * @param valueLeft the value of the first column
 * @param labelRight the label of the second column
 * @param valueRight the label of the second column
 */
@Composable
fun SensorDetails(color: Color, labelLeft: String, valueLeft: String, labelRight: String, valueRight: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterHorizontally)
    ) {
        SensorDetail(color, labelLeft, valueLeft)
        SensorDetail(color, labelRight, valueRight)
    }
}

/**
 * Displays the details of one attribute of a sensor (name of state).
 * @param color the color of the text
 * @param title the name of the attribute to display
 * @param value the value
 */
@Composable
private fun RowScope.SensorDetail(color: Color, title: String, value: String) {
    Column (
        Modifier.weight(.5f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            title,
            Modifier.alpha(0.5f),
            color,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            value,
            color = color,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}