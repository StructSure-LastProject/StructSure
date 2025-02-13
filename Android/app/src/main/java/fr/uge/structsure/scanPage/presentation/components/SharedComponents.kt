package fr.uge.structsure.scanPage.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import fr.uge.structsure.ui.theme.White

/**
 * Bean containing a colored circle and a value that can tell how many
 * sensor are contained in a given state or display a sensor.
 * @param modifier to apply custom styling to the bean
 * @param value the value to display (number or name of the sensor)
 * @param state (weather) state to display
 * @param onClick action to run when the bean is clicked
 */
@Composable
fun SensorBean(modifier: Modifier = Modifier, value: String, state: SensorState, onClick: (() -> Unit)? = null) {
    var mod = modifier.height(40.dp)
        .clip(RoundedCornerShape(size = 50.dp))
    if (onClick != null) {
        mod = mod.clickable { onClick() }
    }
    Row(
        modifier = mod
            .background(color = White)
            .padding(start = 15.dp, top = 5.dp, end = 15.dp, bottom = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .border(2.dp, state.color.copy(alpha = 0.25F), CircleShape)
                .padding(2.dp)
                .clip(CircleShape)
                .background(state.color)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}