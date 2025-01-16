package fr.uge.structsure.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.uge.structsure.MainActivity
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.StructSureTheme

@Composable
fun Page(
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    backgroundColor: Color = LightGray,
    decorated: Boolean = true,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable () -> Unit
) {
    StructSureTheme {
        Scaffold(
            modifier = modifier,
            bottomBar = bottomBar,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition,
            contentWindowInsets = contentWindowInsets,
            containerColor = backgroundColor,
        ) { innerPadding ->
            var mod = Modifier.padding(innerPadding)
            if (decorated) mod = mod.padding(start = 20.dp, top = 15.dp, end = 20.dp, bottom = 15.dp)
            Column(
                verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
                modifier = mod
            ) {
                val brightness = (0.299 * backgroundColor.red + 0.587 * backgroundColor.green + 0.114 * backgroundColor.blue)
                MainActivity.darkStatusBar.set(brightness < 0.5)
                if (decorated) Header()
                content()
            }
        }
    }
}