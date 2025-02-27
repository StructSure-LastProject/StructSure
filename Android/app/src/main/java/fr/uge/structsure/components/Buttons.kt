package fr.uge.structsure.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uge.structsure.R

/**
 * Component for simple button defined by an icon, a description, color and link
 * @param id the icon to display
 * @param description the alternative text of the icon
 * @param color the color of the icon
 * @param background the color of the button's background
 * @param onClick callback to run when the button is clicked
 */
@Composable
fun Button(
    @DrawableRes id: Int,
    description: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    background: Color = MaterialTheme.colorScheme.surface,
    onClick: (() -> Unit)? = null
) {
    val enabled = onClick != null
    IconButton(
        modifier = Modifier.size(40.dp),
        colors = IconButtonColors(background, color, background, color),
        onClick = if (onClick == null) { {} } else onClick,
        enabled = enabled
    ) {
        Icon(painterResource(id), description, Modifier.size(20.dp))
    }
}

/**
 * Component for button defined by a text (and an optional icon), a
 * description, color and link
 * @param text label to write on the button
 * @param id the icon to display
 * @param color the color of the text and icon
 * @param background the color of the button's background
 * @param onClick callback to run when the button is clicked
 */
@Composable
fun ButtonText (
    text: String,
    @DrawableRes id: Int?,
    color: Color = MaterialTheme.colorScheme.onSurface,
    background: Color = MaterialTheme.colorScheme.surface,
    onClick: (() -> Unit)? = null
) {
    val enabled = onClick != null
    androidx. compose. material3.Button(
        modifier = Modifier.height(40.dp),
        colors = ButtonColors(background, color, background, color),
        onClick = if (onClick == null) { {} } else onClick,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        enabled = enabled
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text, Modifier.height(20.dp), color,
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 14.0.sp
            )
            if (id != null) Icon(painterResource(id), "icon", Modifier.size(20.dp))
        }
    }
}