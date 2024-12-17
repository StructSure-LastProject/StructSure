package fr.uge.structsure.download_structure.presentation.structures

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.uge.structsure.download_structure.domain.StructureData
import fr.uge.structsure.download_structure.domain.StructureDownloadState
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.Typography
import fr.uge.structsure.ui.theme.White

@Composable
fun Structure() {
    Row(
        modifier = Modifier
            .background(color = White, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
        ) {
            Text(
                style = Typography.headlineMedium,
                text = "test"
            )
            Text(
                style = Typography.bodyMedium,
                color = Black.copy(alpha = 0.5f),
                text = "test"
            )
        }
        StructureButtons()
    }
}