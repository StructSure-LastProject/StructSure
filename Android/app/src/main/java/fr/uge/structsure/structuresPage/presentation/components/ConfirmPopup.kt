package fr.uge.structsure.structuresPage.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R
import fr.uge.structsure.components.ButtonText
import fr.uge.structsure.components.PopUp
import fr.uge.structsure.components.Title
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.Red
import fr.uge.structsure.ui.theme.White

@Composable
fun ConfirmPopup(visible: MutableState<Boolean>, structure: String, onSubmit: () -> Unit) {
    if (!visible.value) return
    PopUp({ visible.value = false }, {
        Title("Supprimer l'ouvrage", false)
    }) {
        Text("Êtes-vous sûr de vouloir supprimer \"$structure\" ?", style = typography.bodyMedium)
        Text(
            "La suppression effacera les données de l'ouvrage sur votre appareil, mais vous pourrez toujours les télécharger à nouveau depuis le serveur.",
            Modifier.alpha(.5f),
            style = typography.bodyMedium
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ButtonText("Annuler", null, Black, LightGray, Modifier.weight(1f)) {
                visible.value = false
            }
            ButtonText("Supprimer", R.drawable.x, White, Red, Modifier.weight(1f), onSubmit)
        }
    }
}