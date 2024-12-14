package fr.uge.structsure.download_structure.presentation.accountframe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R
import fr.uge.structsure.components.Button

@Preview(showBackground = true)
@Composable
fun AccountInformationsView(modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .background(color = Color.Black, shape = RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 15.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            UserInformations()
            Box(modifier = Modifier.align(Alignment.End)) {
                Button(
                    id = R.drawable.log_out,
                    description = "Log out button that disconnect users who click on it."
                )
            }
        }
    }
}