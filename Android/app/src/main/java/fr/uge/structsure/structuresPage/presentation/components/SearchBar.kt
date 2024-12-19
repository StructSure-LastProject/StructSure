package fr.uge.structsure.structuresPage.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import fr.uge.structsure.components.InputSearch

@Composable
fun SearchBar(modifier: Modifier = Modifier, input: MutableState<String>) {
    Row() {
        InputSearch(
            modifier = Modifier,
            value = input.value,
            placeholder = "Rechercher...",
            onChange = {
                input.value = it
            }
        )
    }
}
