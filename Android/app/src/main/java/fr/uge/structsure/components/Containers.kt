package fr.uge.structsure.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R

@Composable
@Preview
fun ContainersPreview() {
    Title("Titre") {
        Button(R.drawable.x, "close")
    }
}
/**
 * Title expected to be used at the beginning of a section that
 * contains a title text and buttons.
 * @param text title of the section
 * @param content buttons to add in the title
 */
@Composable
fun Title (
    text: String,
    content: @Composable () -> Unit = {}
) {
    Row(Modifier.padding(start = 20.dp)) {
        Text(text,
            style = MaterialTheme.typography.titleLarge,
            modifier= Modifier.fillMaxWidth().weight(2f))
        content()
    }
}