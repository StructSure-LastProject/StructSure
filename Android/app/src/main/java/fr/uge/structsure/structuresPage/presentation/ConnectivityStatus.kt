package fr.uge.structsure.structuresPage.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R
import fr.uge.structsure.structuresPage.domain.ConnectivityViewModel
import fr.uge.structsure.ui.theme.Typography

@Composable
fun ConnectivityStatus(connectivityViewModel: ConnectivityViewModel) {
    val isConnected = connectivityViewModel.isConnected.observeAsState()


    if (isConnected.value == false) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.lucide_wifi),
                contentDescription = "StructSure Logo",
                modifier = Modifier.size(100.dp)
            )
            Text("Pas de connexion", style = Typography.titleLarge)
            Text("Connectez vous à internet pour télécharger des ouvrages", textAlign = TextAlign.Center, style = Typography.bodyMedium)
        }
    }
}