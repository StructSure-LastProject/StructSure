package fr.uge.structsure.ui.components


import android.text.TextUtils.EllipsizeCallback
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.uge.structsure.R
import java.security.spec.EllipticCurve

@Composable
fun StructureSummaryView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Viaduc de Sylans",
                fontSize = 22.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Icon(
                painter = painterResource(id = R.drawable.structsure_logo),
                contentDescription = "Sync Icon",
                modifier = Modifier.size(40.dp).
                background(Color.White, shape = RoundedCornerShape(20.dp))
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
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