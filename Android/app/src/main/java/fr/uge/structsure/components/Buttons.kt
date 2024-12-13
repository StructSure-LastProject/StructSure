package fr.uge.structsure.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R

@Composable
@Preview
fun Preview() {
    Button(R.drawable.x, "")
}

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