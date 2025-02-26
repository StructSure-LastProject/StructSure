package fr.uge.structsure.structuresPage.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import fr.uge.structsure.components.InputSearch

@Composable
fun SearchBar(input: MutableState<String>) {
    Row {
        InputSearch(
            value = input.value,
            placeholder = "Rechercher",
            onChange = {
                input.value = it
            }
        )
    }
}
