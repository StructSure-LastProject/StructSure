package fr.uge.structsure.startScan.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uge.structsure.R
import fr.uge.structsure.ui.theme.*


/**
 * This composable is used to display the plans of the structure.
 */
@Composable
fun PlansView(modifier: Modifier = Modifier) {
    var isSensorListVisible by remember { mutableStateOf(true) }
    val itemBackgroundColors = remember { mutableStateListOf(*Array(5) { White }) }

    Column(
        modifier = modifier
            .padding(horizontal = 20.dp, vertical = 15.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Plans",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = White, shape = RoundedCornerShape(size = 20.dp))
                .padding(horizontal = 20.dp, vertical = 15.dp)
        )  {
            Image(
                painter = painterResource(id = R.drawable.oa_plan),
                contentDescription = "Plan",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(171.dp)
            )

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(LightGray)
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isSensorListVisible = !isSensorListVisible }
                        .background(color = LightGray, shape = RoundedCornerShape(10.dp))
                        .padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = if (isSensorListVisible) R.drawable.chevron_down else R.drawable.chevron_down),
                        contentDescription = "Chevron",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Section OA",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (isSensorListVisible) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        items((1..5).toList()) { index ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = itemBackgroundColors[index - 1],
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        itemBackgroundColors[index - 1] =
                                            if (itemBackgroundColors[index - 1] == White)
                                                LightGray
                                            else White
                                    }
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = "•",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Black
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "Plan ${String.format("%02d", index)}",
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontFamily = fonts,
                                        fontWeight = FontWeight.Medium,
                                        color = Black
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

