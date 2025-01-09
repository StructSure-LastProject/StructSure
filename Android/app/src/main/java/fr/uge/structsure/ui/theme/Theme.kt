package fr.uge.structsure.ui.theme

import android.app.Activity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat


private val LightColorScheme = lightColorScheme(
    primary = Red,
    onPrimary = White,

    error = Red,

    background = White,
    onBackground = Black,

    surface = LightGray,
    onSurface = Black,

    secondaryContainer = Black,
    onSecondaryContainer = White
)

/**
 * Allow the app to adapt to the global theme of the phone.
 */
@Composable
fun StructSureTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            WindowCompat.getInsetsController((view.context as Activity).window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = MaterialTheme.shapes.copy(medium = RoundedCornerShape(20.dp))
    ) {
        CompositionLocalProvider(LocalTextSelectionColors provides TextSelectionColors(
            handleColor = Black,
            backgroundColor = Black.copy(alpha = 0.25f)
        ), content)
    }
}
