package fr.uge.structsure.components

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.MainActivity
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.StructSureTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Page(
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit = {},
    backgroundColor: Color = LightGray,
    decorated: Boolean = true,
    scrollable: Boolean = true,
    navController: NavController,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable ColumnScope.(scrollState: ScrollState) -> Unit
) {
    StructSureTheme {
        Scaffold(
            bottomBar = bottomBar,
            contentWindowInsets = contentWindowInsets,
            containerColor = backgroundColor
        ) {
            var mod = Modifier.padding()
            val scrollState = rememberScrollState()
            if (decorated) mod = mod
                .systemBarsPadding()
                .padding(start = 20.dp, top = 15.dp, end = 20.dp, bottom = 15.dp)
            mod = mod.clip(RoundedCornerShape(20.dp))
            if (scrollable) mod = mod.verticalScroll(scrollState)
            Column(
                verticalArrangement = Arrangement.spacedBy(35.dp, Alignment.CenterVertically),
                modifier = mod.then(modifier),
            ) {
                val brightness = (0.299 * backgroundColor.red + 0.587 * backgroundColor.green + 0.114 * backgroundColor.blue)
                MainActivity.darkStatusBar.set(brightness < 0.5)
                if (decorated) Header(navController)
                content(scrollState)
            }
        }
    }
}