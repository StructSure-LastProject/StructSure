package fr.uge.structsure.download_structure.presentation.structures

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.uge.structsure.components.InputSearch

@Preview
@Composable
fun SearchBar(modifier: Modifier = Modifier, input: String = "") {
    Row() {
        InputSearch(
            modifier = Modifier,
            value = input,
            placeholder = "Rechercher..."
        )
    }
}
