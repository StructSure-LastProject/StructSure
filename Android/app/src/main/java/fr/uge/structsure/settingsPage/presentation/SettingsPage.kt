package fr.uge.structsure.settingsPage.presentation

import android.content.Context
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import fr.uge.structsure.ui.theme.White

/**
 * SettingsPage is a composable function that renders the settings screen of the application.
 * Users can update server configuration and interrogator sensitivity from this page.
 *
 * @param navController NavController instance for handling navigation between screens.
 */
@Composable
fun SettingsPage(navController: NavController) {
    val context = LocalContext.current
    var serverAddress by remember { mutableStateOf(RetrofitInstance.getBaseUrl().orEmpty()) }
    val sensitivity = remember { PreferencesManager.getScannerSensitivity(context) }
    var rfidMin by remember { mutableIntStateOf(-sensitivity[0]) }
    var rfidMax by remember { mutableIntStateOf(-sensitivity[1]) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Page(
        backgroundColor = LightGray,
        decorated = true,
        navController = navController
    ) {
        Column(
            modifier = Modifier
                .background(White, RoundedCornerShape(size = 20.dp))
                .padding(20.dp, 15.dp),
            verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start
        ) {
            SettingsHeader()

            InputText(
                label = "Adresse du serveur",
                value = serverAddress,
                placeholder = "https://serveur.com",
                errorMessage = errorMessage
            ) { s -> serverAddress = s }

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

            SensitivityRangeSlider(rfidMin, rfidMax, { min -> rfidMin = min }, { max -> rfidMax = max })

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()) {
                ButtonText("Annuler", null, Black, LightGray) { navController.popBackStack() }

                ButtonText("Enregistrer", null, White, Black) {
                    errorMessage = onSubmit(context, navController, serverAddress, rfidMin, rfidMax)
                }
            }
        }
    }
}

/**
 * Title and subtitle of the settings page.
 */
@Composable
private fun SettingsHeader() {
    Column (
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Réglages",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Personnalisez le comportement de l’application",
            color = Black.copy(alpha = 0.5f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * A composable function that represents a customized range slider for controlling the sensitivity of the interrogator.
 * The slider allows users to select a range of values within a specified range.
 */
@Composable
private fun SensitivityRangeSlider(min: Int, max: Int, onMinChange: (Int) -> Unit, onMaxChange: (Int) -> Unit) {

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "-${min}dB .. -${max}dB",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        RangeSlider(
            value = min.toFloat()..max.toFloat(),
            onValueChange = { values ->
                onMinChange(values.start.toInt())
                onMaxChange(values.endInclusive.toInt())
            },
            valueRange = 0f..100f,
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
 * Handles the submission of the settings form.
 * @param context needed to save the settings
 * @param navController to navigate back to the previous page or connexion page
 * @param serverAddress the value of the server address field
 * @param rfidMin the lower bound of the sensitivity range
 * @param rfidMax the upper bound of the sensitivity range
 */
private fun onSubmit(context: Context, navController: NavController, serverAddress: String, rfidMin: Int, rfidMax: Int): String? {
    val accountDao = MainActivity.db.accountDao()
    if (serverAddress.isBlank()) {
        return "Veuillez renseigner l'adresse du serveur"
    } else if (!isValidUrl(serverAddress)) {
        return "Veuillez entrer une URL valide du serveur"
    }
    if (serverAddress != RetrofitInstance.getBaseUrl()) {
        accountDao.get()?.let { accountDao.disconnect(it.login) }
        PreferencesManager.saveServerUrl(context, serverAddress)
        PreferencesManager.deleteUserData(context)
        RetrofitInstance.init(serverAddress)
        navController.navigateNoReturn("LoginPage?backRoute=HomePage")
    } else {
        navController.popBackStack()
    }
    PreferencesManager.saveScannerSensitivity(context, rfidMin, rfidMax)
    return null
}

/**
 * Validates whether the provided URL is valid and starts with "http://" or "https://".
 *
 * @param url The URL string to validate.
 * @return True if the URL is valid, false otherwise.
 */
private fun isValidUrl(url: String): Boolean = Patterns.WEB_URL.matcher(url).matches()
        && (url.startsWith("http://") || url.startsWith("https://"))
        && url.length > 11


