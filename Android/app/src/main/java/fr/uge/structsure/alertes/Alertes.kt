package fr.uge.structsure.alertes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.R
import fr.uge.structsure.components.ButtonText
import fr.uge.structsure.components.Page
import fr.uge.structsure.ui.theme.Red
import fr.uge.structsure.ui.theme.White

/**
 * The composable to the alert page in case of a NOK or Défaillant sensor
 * @param navController to navigate around the different pages
 * @param state true if NOK, false if Défaillant
 * @param sensorName the name of the sensor
 * @param lastStateSensor the last state of the sensor
 */
@Composable
fun Alerte(navController: NavController, state:Boolean, sensorName:String, lastStateSensor:String) {

    //Parameters of the page according to the state
    val colors = if(state) listOf(Color(0xFFF13327), Color(0xFFF15627)) else listOf(Color(0xFFF18527), Color(0xFFF15627))

    Page (
        backgroundColor = colors[0],
        decorated = false,
        scrollable = false
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
                AlertDetails(state, sensorName, lastStateSensor)
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
 * @param state whether or not the sensor is NOK or not
 * @param sensorName the name of the failing sensor
 * @param lastStateSensor last known state of sensor
 */
@Composable
private fun AlertDetails(state: Boolean, sensorName: String, lastStateSensor: String) {
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

        SensorDetails(sensorName, lastStateSensor)

        Plan()

        // Sensor note
        Text(
            modifier = Modifier.padding(horizontal = 50.dp).alpha(.5f),
            text = "Capteur caché derrière la poutre métallique à environ 30cm du point d'ancrage.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = White,
        )
    }
}

/**
 * Row displaying the name of the sensor and the last known state.
 * @param name the custom name of the sensor
 * @param state the last saved state of the sensor (NOK, OK, ...)
 */
@Composable
private fun SensorDetails(name: String, state: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterHorizontally)
    ) {
        SensorDetail("Nom du capteur :", name)
        SensorDetail("Dernier état :", state)
    }
}

/**
 * Displays the details of one attribute of a sensor (name of state).
 * @param title the name of the attribute to display
 * @param value the value
 */
@Composable
private fun SensorDetail(title: String, value: String) {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.alpha(0.5f),
            text = title,
            color = White,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = value,
            color = White,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}