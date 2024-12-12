package fr.uge.structsure.start_scan.presentation.components


import android.text.TextUtils.EllipsizeCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uge.structsure.R

@Composable
fun StructureSummaryView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Viaduc de Sylans",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight(600),
                    color = Variables.Black,
                )
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(color = Variables.White, shape = RoundedCornerShape(size = 50.dp))
                    .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.notepad_text),
                    contentDescription = "image description",
                    contentScale = ContentScale.None,
                    modifier = Modifier
                        .padding(1.dp)
                        .width(25.dp)
                        .height(25.dp)
                )
            }
        }
        StatsView()
        Spacer(modifier = Modifier.height(16.dp))
        SensorStatusColumn()
    }
}

@Composable
fun SensorStatusColumn() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        repeat(5) { index ->
            SensorStatusCircle(sensorNumber = index + 21, sensorState = SensorState.values()[index % 4])
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
fun SensorStatusCircle(sensorNumber: Int, sensorState: SensorState) {
    Box(
        modifier = Modifier
            .size(width = 80.dp, height = 40.dp)
            .background(Color.White, shape = RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(sensorState.color, shape = RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = sensorNumber.toString(),
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

enum class SensorState(val color: Color) {
    GREEN(Color.Green),
    RED(Color.Red),
    ORANGE(Color(0xFFFFA500)), // Orange color
    GRAY(Color.Gray)
}