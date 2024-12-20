package fr.uge.structsure.connexionPage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import auth
import fr.uge.structsure.R
import fr.uge.structsure.components.ButtonText
import fr.uge.structsure.components.Header
import fr.uge.structsure.components.InputPassword
import fr.uge.structsure.components.InputText
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.Red
import fr.uge.structsure.ui.theme.White
import kotlinx.coroutines.launch


@Composable
fun ConnexionCard(navController: NavController) {
    Column (
        verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
        //horizontalAlignment = Alignment.End,
        modifier = Modifier
            .padding(start = 20.dp, top = 15.dp, end = 20.dp, bottom = 15.dp)
    )
    {
        Header()
        Column(
            verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
            //horizontalAlignment = Alignment.End,
            modifier = Modifier
                .background(color = White, shape = RoundedCornerShape(size = 20.dp))
                .padding(start = 20.dp, top = 15.dp, end = 20.dp, bottom = 15.dp)
        ) {

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Connexion",
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            Text(
                text = AnnotatedString.Builder().apply {
                    append("Renseignez vos identifiants ")
                    append(
                        AnnotatedString(
                            "Struct",
                            SpanStyle(fontWeight = FontWeight.Bold)
                        )
                    )
                    append("Sure pour accéder à l’application.")
                }.toAnnotatedString(),
                style = MaterialTheme.typography.bodyMedium
            )

            //Message d'erreur
            var errorMessage by remember { mutableStateOf("") }
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Red,
                    modifier = Modifier.fillMaxWidth()
                )
            }


            var login by remember { mutableStateOf("") }  //se souvient de la valeur entre deux reload de l'affichage
            InputText(
                label = "Identifiant",
                value = login,
                onChange = { s -> login = s }
            )

            var password by remember { mutableStateOf("") }  //se souvient de la valeur entre deux reload de l'affichage
            InputPassword(
                label = "Mot de passe",
                value = password,
                onChange = { s -> password = s }
            )

            val coroutineScope = rememberCoroutineScope()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ButtonText("Se connecter", R.drawable.connect, White, Black) {
                    coroutineScope.launch {
                        try {
                            // Assuming auth is a suspend function
                            val result = auth(login, password, navController)
                            if (result.isEmpty()) {
                                // Handle successful authentication (e.g., navigate)
                            } else {
                                errorMessage = result // Set error message if authentication fails
                            }
                        } catch (e: Exception) {
                            errorMessage =
                                "Une erreur est survenue: ${e.message}" // Set error message
                        }
                    }
                }
            }

        }
    }
}