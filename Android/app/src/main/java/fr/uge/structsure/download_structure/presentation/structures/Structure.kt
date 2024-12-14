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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.uge.structsure.ui.theme.Typography

data class StructureDate(
    val name: String, val state: StructureDownloadState
)

@Preview(showBackground = true)
@Composable
fun Structure(
    modifier: Modifier = Modifier,
    data: StructureDate = StructureDate(
        name = "Structure",
        state = StructureDownloadState.downloadable
    )
) {
    Row(
        modifier = Modifier
            .background(color = Color.White, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
        ) {
            Text(
                style = Typography.titleMedium,
                text = data.name
            )
            Text(
                style = Typography.titleSmall,
                color = Color.Black.copy(alpha = 0.5f),
                text = data.state.state
            )
        }
        StructureButtons(state = data.state)
    }
}