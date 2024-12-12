package fr.uge.structsure.bluetoothConnection.presentation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumTouchTargetEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R

@Composable
@Preview
fun Preview() {
    SmallButton(R.drawable.x, "")
}

/**
 * Component for a header menu button defined by an icon, a description, color and link
 * @param id the icon to display
 * @param description the alternative text of the icon
 * @param tint the color of the icon
 * @param onClick activity to switch to when clicking on the button
 */
@Composable
fun SmallButton(@DrawableRes id: Int, description: String, tint: Color = MaterialTheme.colorScheme.onSurface, background: Color = MaterialTheme.colorScheme.surface, onClick: () -> Unit = {}) {
    IconButton(
        modifier = Modifier.size(40.dp).background(color = background, shape = RoundedCornerShape(size = 50.dp)), onClick = onClick
    ) {
        Icon(
            painter = painterResource(id),
            contentDescription = description,
            modifier = Modifier.size(20.dp),
            tint = tint
        )
    }
}