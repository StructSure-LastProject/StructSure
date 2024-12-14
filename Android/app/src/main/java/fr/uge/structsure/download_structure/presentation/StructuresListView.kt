package fr.uge.structsure.download_structure.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StructuresListView(){
    Box(modifier = Modifier
        .background(color = Color.LightGray, shape = RoundedCornerShape(16.dp))
        .fillMaxWidth()
        .fillMaxHeight()) {
    }
}