package fr.uge.structsure.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    content: @Composable () -> Unit = {}
) {
    Row(Modifier.padding(start = 20.dp)) {
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
