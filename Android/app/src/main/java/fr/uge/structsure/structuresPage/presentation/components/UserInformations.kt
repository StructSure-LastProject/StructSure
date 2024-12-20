package fr.uge.structsure.structuresPage.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import fr.uge.structsure.ui.theme.Typography

@Preview
@Composable
fun UserInformations(
    modifier: Modifier = Modifier,
    name: String = "User NAME",
    id: String = "user.name@mail.service.com"
) {
    Column(modifier = Modifier) {
        Text(style = Typography.titleLarge,
            color = Color.White,
            text = name)
        Text(style = Typography.titleSmall,
            color = Color.White.copy(alpha = 0.5f),
            text = id)
    }
}