package fr.uge.structsure.alertes

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.White

@Composable
fun Plan() {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //Section
        Text(
            text = "section",
            style = MaterialTheme.typography.bodyMedium,
            color = White
        )

        //Plan image
        Image(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(size = 15.dp))
                .border(width = 5.dp, color = LightGray, shape = RoundedCornerShape(size = 15.dp)),
            painter = painterResource(id = R.drawable.plan_glaciere),
            contentDescription = "Plan", //nom du plan peut être

        )
    }
}