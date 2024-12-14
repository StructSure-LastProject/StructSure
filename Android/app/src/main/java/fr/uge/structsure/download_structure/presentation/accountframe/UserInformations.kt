package fr.uge.structsure.download_structure.presentation.accountframe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.uge.structsure.ui.theme.Typography

@Preview
@Composable
fun UserInformations(
    modifier: Modifier = Modifier,
    name: String = "User NAME",
    id: String = "user.name@mail.service.com"
) {
    Column(modifier = Modifier) {
        Text(style = Typography.titleMedium,
            color = Color.White,
            text = name)
        Text(style = Typography.titleSmall,
            color = Color.White.copy(alpha = 0.5f),
            text = id)
    }
}