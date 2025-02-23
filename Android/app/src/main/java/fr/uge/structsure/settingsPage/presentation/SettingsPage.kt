package fr.uge.structsure.settingsPage.presentation

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.MainActivity
import fr.uge.structsure.components.ButtonText
import fr.uge.structsure.components.InputText
import fr.uge.structsure.components.Page
import fr.uge.structsure.navigateNoReturn
import fr.uge.structsure.retrofit.RetrofitInstance
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.Red
import fr.uge.structsure.ui.theme.White

/**
 * SettingsPage is a composable function that renders the settings screen of the application.
 * Users can update server configuration and interrogator sensitivity from this page.
 *
 * @param navController NavController instance for handling navigation between screens.
 */
@Composable
fun SettingsPage(navController: NavController) {
    var serverAddress by remember { mutableStateOf(RetrofitInstance.getBaseUrl().orEmpty()) }
    var errorMessage by remember { mutableStateOf("") }
    val context = LocalContext.current
    val accountDao = MainActivity.db.accountDao()

    Page(
        backgroundColor = LightGray,
        decorated = true,
        navController = navController
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .background(color = White, shape = RoundedCornerShape(size = 20.dp))
                .padding(20.dp, 15.dp)
        ) {
            Column (
                verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Réglages",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = AnnotatedString.Builder().apply {
                        append("Personnalisez le comportement de l’application")
                    }.toAnnotatedString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Black.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center

                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
                horizontalAlignment = Alignment.Start,
            ) {
                InputText(
                    label = "Adresse du serveur",
                    value = serverAddress,
                    onChange = { s -> serverAddress = s }
                )

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Red,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "Sensibilité de l’interrogateur",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Filtrer les tags RFID en fonction de l’atténuation du signal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Black.copy(alpha = 0.5f)
                )
            }

            RangeSliderSensitivityInterog()

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()) {
                ButtonText("Annuler", null, Black, LightGray, onClick = { navController.popBackStack() })

                ButtonText("Enregistrer", null, White, Black, onClick = {
                    if (serverAddress.isBlank()) {
                        errorMessage = "Veuillez renseigner l'adresse du serveur"
                    } else if (!isValidUrl(serverAddress)) {
                        errorMessage = "Veuillez entrer une URL valide du serveur"
                    } else {
                        val currentUser = accountDao.get()
                        if (currentUser != null) {
                            accountDao.disconnect(currentUser.login)
                        }
                        if (serverAddress != RetrofitInstance.getBaseUrl()) {
                            PreferencesManager.saveServerUrl(context, serverAddress)
                            PreferencesManager.clearServerUrl(context)
                            RetrofitInstance.init(serverAddress)
                            errorMessage = ""
                            navController.navigateNoReturn("LoginPage")
                        } else {
                            errorMessage = "L'adresse du serveur n'a pas changé. Vous êtes déjà connecté."
                        }
                    }
                })

            }
        }
    }
}

/**
 * A composable function that represents a customized range slider for controlling the sensitivity of the interrogator.
 * The slider allows users to select a range of values within a specified range.
 */
@Composable
private fun RangeSliderSensitivityInterog() {
    var rangeStart by remember { mutableFloatStateOf(45f) }
    var rangeEnd by remember { mutableFloatStateOf(100f) }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "-${rangeStart.toInt()}dB .. -${rangeEnd.toInt()}dB",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        RangeSlider(
            value = rangeStart..rangeEnd,
            onValueChange = { values ->
                rangeStart = values.start
                rangeEnd = values.endInclusive
            },
            valueRange = 45f..100f,
            colors = SliderDefaults.colors(
                activeTrackColor = Black,
                inactiveTrackColor = LightGray,
                thumbColor = Black
            ),
            modifier = Modifier.semantics { contentDescription = "Sensibilité de l’interrogateur" }
        )
    }
}


/**
 * Validates whether the provided URL is valid and starts with "http://" or "https://".
 *
 * @param url The URL string to validate.
 * @return True if the URL is valid, false otherwise.
 */
private fun isValidUrl(url: String): Boolean =
    Patterns.WEB_URL.matcher(url).matches() && (url.startsWith("http://") || url.startsWith("https://"))


