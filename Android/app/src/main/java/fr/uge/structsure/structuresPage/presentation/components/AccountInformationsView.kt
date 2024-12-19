package fr.uge.structsure.structuresPage.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R
import fr.uge.structsure.components.Button
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.White

@Preview(showBackground = true)
@Composable
fun AccountInformationsView(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .background(color = Black, shape = RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 15.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            UserInformations()
            Box(modifier = Modifier.align(Alignment.End)) {
                Button(
                    id = R.drawable.log_out,
                    background = White,
                    description = "Log out button that disconnect users who click on it."
                )
            }
        }
    }
}