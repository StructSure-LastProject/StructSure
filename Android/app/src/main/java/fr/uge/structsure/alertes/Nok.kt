package fr.uge.structsure.alertes

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.R
import fr.uge.structsure.components.ButtonText
import fr.uge.structsure.ui.theme.Red
import fr.uge.structsure.ui.theme.White

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Nok(navController: NavController, sensorName:String, lastStateSensor:String){
     Scaffold (
         content = {
            Column(
                modifier = Modifier
                    .background(Brush.linearGradient(colors = listOf(Color(0xFFF13327),Color(0xFFF15627))))
                    .fillMaxSize()
                    .padding(start = 25.dp, top = 50.dp, end = 25.dp, bottom = 113.dp),
                verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Image(
                    painter = painterResource(id = R.drawable.lucide_triangle_alert),
                    contentDescription = "Triangle d'alerte"
                )
                Text(
                    text = "Capteur Non OK",
                    color = White,
                    style = MaterialTheme.typography.bodyLarge
                )

                //Ligne info capteur
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =  Arrangement.spacedBy(25.dp, Alignment.CenterHorizontally)
                ) {
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            text = "Nom du capteur :",
                            color = Color(0x80FFFFFF),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = sensorName,
                            color = White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        Text(
                            text = "Dernier état :",
                            color = Color(0x80FFFFFF),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = lastStateSensor,color = White,

                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                //Ligne info plan
                Plan()


            }
        },
        bottomBar = {
            //Bouton poursuivre le scan
            Row (
                modifier = Modifier.fillMaxWidth().padding(30.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                ButtonText(
                    text = "Poursuivre le scan",
                    color = Red,
                    background = White,
                    id = null,
                    onClick = {navController.navigateUp()} //pas sur de ça
                )
            }
        }
    )


}