package fr.uge.structsure.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import fr.uge.structsure.ui.theme.LightGray

/**
 * Field containing a single line text input and a label.
 * @param modifier to customise the text input
 * @param label the name of the field
 * @param value the variable in which the value will be stored
 * @param placeholder indicative text placed in the input when no
 *     value is typed
 * @param keyboardOptions modifier to format the text while being typed
 * @param onChange callback to capture input value modifications
 */
@Composable
fun InputText(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onChange: (String) -> Unit = {}
) {
    Input(modifier, label, value, placeholder, false, onChange, false, keyboardOptions)
}

/**
 * Field containing a single line password input and a label. This
 * input displays points instead of characters and blocks keyboard
 * text completion.
 * @param modifier to customise the text input
 * @param label the name of the field
 * @param value the variable in which the value will be stored
 * @param placeholder indicative text placed in the input when no
 *     value is typed
 * @param onChange callback to capture input value modifications
 */
@Composable
fun InputPassword(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    placeholder: String = "",
    onChange: (String) -> Unit = {}
)  {
    Input(modifier, label, value, placeholder, true, onChange)
}

/**
 * Field containing a multiple lines text input and a label.
 * @param modifier to customise the text input
 * @param label the name of the field
 * @param value the variable in which the text will be stored
 * @param placeholder indicative text placed in the input when no
 *     value is typed
 * @param keyboardOptions modifier to format the text while being typed
 * @param onChange callback to capture input value modifications
 */
@Composable
fun InputTextArea (
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onChange: (String) -> Unit = {}
) {
    Input(Modifier.defaultMinSize(minHeight = 75.dp).then(modifier), label, value, placeholder, false, onChange, true, keyboardOptions)
}

@Composable
private fun Input(
    modifier: Modifier,
    label: String,
    value: String,
    placeholder: String,
    password: Boolean,
    onChange: (String) -> Unit,
    multiLines: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
    ) {
        Label(label)
        BasicTextField(
            value = value,
            onValueChange = onChange,
            modifier = modifier.fillMaxWidth()
                .background(color = LightGray, shape = RoundedCornerShape(size = if (multiLines) 10.dp else 50.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            enabled = true,
            textStyle = if (multiLines) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge,
            keyboardOptions = if (password) KeyboardOptions(keyboardType = KeyboardType.Password) else keyboardOptions,
            singleLine = !multiLines,
            visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
            decorationBox = { innerTextField -> // Placeholder
                if (value.isEmpty()) Text(
                    placeholder,
                    Modifier.alpha(.5f).padding(vertical = 2.dp),
                    style = MaterialTheme.typography.bodyMedium)
                innerTextField.invoke()
            }
        )
    }
}

@Composable
private fun Label(label: String) {
    Text(label, Modifier.alpha(0.75f), style = MaterialTheme.typography.bodyMedium)
}

