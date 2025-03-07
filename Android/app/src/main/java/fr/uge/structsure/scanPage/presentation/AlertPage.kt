package fr.uge.structsure.scanPage.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.MainActivity.Companion.db
import fr.uge.structsure.R
import fr.uge.structsure.components.ButtonText
import fr.uge.structsure.components.Page
import fr.uge.structsure.components.PlanForSensor
import fr.uge.structsure.components.SensorDetails
import fr.uge.structsure.scanPage.domain.PlanViewModel
import fr.uge.structsure.scanPage.presentation.components.SensorState
import fr.uge.structsure.structuresPage.data.SensorDB
import fr.uge.structsure.ui.theme.Red
import fr.uge.structsure.ui.theme.White

/**
 * The composable to the alert page in case of a NOK or Défaillant sensor
 * @param navController to navigate around the different pages
 * @param planViewModel holder of the plans data
 * @param state true if NOK, false if Défaillant
 * @param sensorId the ID of the sensor
 */
@Composable
fun AlertPage(navController: NavController, planViewModel: PlanViewModel, state:Boolean, sensorId: String) {

    //Parameters of the page according to the state
    val colors = if(state) listOf(Color(0xFFF13327), Color(0xFFF15627)) else listOf(Color(0xFFF18527), Color(0xFFF15627))
    val sensor by remember(sensorId) { mutableStateOf(db.sensorDao().getSensor(sensorId)) }

    Page (
        backgroundColor = colors[0],
        decorated = false,
        scrollable = false,
        navController = navController
    ) {
        Column (
            modifier = Modifier
                .background(Brush.linearGradient(colors = colors, Offset.Zero, Offset.Infinite))
                .fillMaxSize()
                .padding(
                    bottom = WindowInsets.Companion.navigationBars.asPaddingValues().calculateBottomPadding()
                )
                .padding(horizontal = 25.dp, vertical = 50.dp),
            verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier.weight(1.0f)
            ) {
                AlertDetails(planViewModel, state, sensor)
            }
            ButtonText("Poursuivre le scan", null, Red, White) {
                // Putting "ScanPage" prevent from double clicking and going to the homepage
                navController.popBackStack("ScanPage", false)
            }
        }
    }
}

/**
 * Block containing all the details about the alert (error message,
 * sensor details, ...)
 * @param planViewModel holder of plans data
 * @param state whether or not the sensor is NOK or not
 * @param sensor the data of the sensor
 */
@Composable
private fun AlertDetails(planViewModel: PlanViewModel, state: Boolean, sensor: SensorDB?) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Triangle alert
        Image(painterResource(id = R.drawable.lucide_triangle_alert), "Triangle d'alerte")

        // Error message
        Text(
            text = "Capteur ${if(state) "Non OK" else "Défaillant"}",
            color = White,
            style = MaterialTheme.typography.titleLarge
        )

        SensorDetails(White,
            "Nom du capteur :", sensor?.name ?: "?",
            "Dernier état :", SensorState.getStateDisplayName(sensor?.state ?: SensorState.UNKNOWN.displayName))
        val displaySensor = sensor?.copy(_state = (if(state) SensorState.NOK else SensorState.DEFECTIVE).name)

        Box(
            Modifier.clip(RoundedCornerShape(20.dp))
                .background(White.copy(alpha = .5f))
                .padding(5.dp)
                .clip(RoundedCornerShape(15.dp))
        ) {
            PlanForSensor(planViewModel, displaySensor, White)
        }

        // Sensor note
        sensor?.note?.let {
            Text(
                modifier = Modifier.padding(horizontal = 50.dp).alpha(.5f),
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = White,
            )
        }
    }
}