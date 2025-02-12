package fr.uge.structsure.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import fr.uge.structsure.R

@Composable
fun Header(navController: NavController, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .pointerInput(Unit) {
                detectTapGestures (
                    onLongPress = {
                        navController.navigate("SettingsPage")
                    }
                )
            }
        ,
        contentAlignment = Alignment.TopCenter

    ) {
        Image(
            painter = painterResource(id = R.drawable.structsure_logo),
            contentDescription = "StructSure Logo",
            modifier = Modifier.size(200.dp)
        )
    }
}