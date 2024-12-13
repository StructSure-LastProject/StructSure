package fr.uge.structsure.start_scan.presentation.components.sensors.status


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uge.structsure.R
import fr.uge.structsure.start_scan.presentation.components.StatsView
import fr.uge.structsure.start_scan.presentation.components.Variables
import fr.uge.structsure.start_scan.presentation.components.poppinsFontFamily

@Preview(showBackground = true)
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

@Preview(showBackground = true)
@Composable
fun SensorStatusColumn() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // TODO
        SensorStatus(
            SensorState.OK,
            "27"
        )
        SensorStatus(
            SensorState.NOK,
            "12"
        )
        SensorStatus(
            SensorState.DEFECTIVE,
            "0"
        )
        SensorStatus(
            SensorState.UNSCAN,
            "171"
        )
    }
}