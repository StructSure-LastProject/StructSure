package fr.uge.structsure.alertes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
    val fade = if(state) listOf(Color(0xFFF13327),Color(0xFFF15627)) else listOf(Color(0xFFF18527),Color(0xFFF15627))
    val errorMessage = if(state) "Non OK" else "Défaillant"

     Scaffold (
         content = {
            Column(
                modifier = Modifier
                    .background(Brush.linearGradient(colors = fade, Offset.Zero, Offset.Infinite))
                    .padding(it)
                    .fillMaxSize()
                    .padding(start = 25.dp, top = 50.dp, end = 25.dp, bottom = 113.dp),
                verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Triangle alert
                Image(painterResource(id = R.drawable.lucide_triangle_alert), "Triangle d'alerte")

                // Error message
                Text(
                    text = "Capteur $errorMessage",
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
        },

        //Poursuivre le scan button
        bottomBar = {
            Row (
                modifier = Modifier.fillMaxWidth().padding(30.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                ButtonText("Poursuivre le scan", null, Red, White) {
                    navController.navigateUp()
                }
            }
        }
    )
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