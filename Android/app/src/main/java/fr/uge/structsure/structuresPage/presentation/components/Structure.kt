package fr.uge.structsure.structuresPage.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.structuresPage.data.StructureData
import fr.uge.structsure.structuresPage.domain.StructureViewModel
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.Typography
import fr.uge.structsure.ui.theme.White

@Composable
fun Structure(structure: StructureData, state: MutableState<StructureStates>, structureViewModel: StructureViewModel, navController: NavController) {
    Row(
        modifier = Modifier
            .background(color = White, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 20.dp, vertical = 15.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier
        ) {
            Text(
                style = Typography.headlineMedium,
                text =  structure.name
            )
            Text(
                style = Typography.bodyMedium,
                color = Black.copy(alpha = 0.5f),
                text = state.value.message
            )
        }
        StructureButtons(structure, state, structureViewModel, navController)
    }
}