package fr.uge.structsure.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import fr.uge.structsure.R
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.Red
import fr.uge.structsure.ui.theme.White

/**
 * Assisted input text with selectable options list
 * @param label the name of this field
 * @param options all the available options
 * @param value the value currently written
 * @param onChange callback to capture the value changes
 * @param color color of the text
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComboBox(
    label: String,
    options: Set<String>,
    value: String,
    onChange: (String) -> Unit,
    color: Color = Black
) {
    var expanded by remember { mutableStateOf(false) }
    val filtered = remember(value) { options.filter { it.contains(value, true)} }
    ExposedDropdownMenuBox(expanded, { expanded = !expanded }) {
        Input(
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true),
            label = label,
            value = value,
            placeholder = "",
            password = false,
            onChange = { onChange(it) },
            enabled = true,
            color = color,
            backgroundColor = LightGray,
            decorations = { innerTextField -> PlaceHolder(value, "", innerTextField, null, R.drawable.chevron_down) },
        )
        DropdownMenu(
            expanded, { expanded = false },
            Modifier.exposedDropdownSize(true),
            properties = PopupProperties(focusable = false),
            shape = RoundedCornerShape(20.dp),
            containerColor = White
        ) {
            filtered.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = typography.bodyLarge) },
                    onClick = {
                        onChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Select component with a label, a select and an options list
 * @param label the name of this field
 * @param options all the available options
 * @param selected the value of the selected option
 * @param onSelect callback to capture new option selection
 * @param rich content to but at the end of the select bar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Select(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    rich: (@Composable () -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded, { expanded = !expanded }) {
        Input(
            modifier = Modifier,
            label = label,
            value = selected,
            placeholder = "",
            password = false,
            onChange = {},
            enabled = false,
            backgroundColor = White,
            decorations = { SelectDecoration(rich, it) }
        )
        ExposedDropdownMenu(
            expanded, { expanded = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = White
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = typography.bodyLarge) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdownMenuBoxScope.SelectDecoration(
    rich: (@Composable () -> Unit)? = null,
    innerTextField: @Composable () -> Unit
) {
    Row (
        horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box (
            Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .weight(1f)
                .padding( 16.dp, 9.dp)
        ) { innerTextField.invoke() }
        Image(
            painterResource(R.drawable.chevron_down),
            "select icon",
            Modifier.padding(top = 9.dp, bottom = 9.dp, start = 16.dp, end = if (rich == null) 16.dp else 0.dp).menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
        )
        rich?.let { rich() }
    }
}

/**
 * Field containing a checkbox and a label
 * @param label the name of the field
 * @param checked whether the checkbox must appear checked or not
 * @param onCheckedChange callback to capture value changes
 */
@Composable
fun InputCheck(label: String, checked: Boolean = false, onCheckedChange: (Boolean) -> Unit = {}) {
    var state by remember { mutableStateOf(checked) }
    Row (
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, true) {
                // Disable the ripple when clicking
                state = !state
                onCheckedChange.invoke(state)
            }
            .padding(top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CheckBox(checked)
        Text(
            text = label,
            modifier = Modifier.fillMaxWidth().alpha(0.75f),
            style = typography.bodyMedium
        )
    }
}

@Composable
private fun CheckBox(checked: Boolean) {
    Row (
        Modifier.size(18.dp)
            .clip(RoundedCornerShape(5.dp))
            .border(2.dp, Black, RoundedCornerShape(5.dp))
            .background(if (checked) Black else Color.Transparent)
            .padding(2.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (checked) Image(painterResource(R.drawable.checkmark), "check", contentScale = ContentScale.FillWidth)
    }
}

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
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onChange: (String) -> Unit = {}
) {
    Input(modifier, label, value, placeholder, isError, errorMessage, false, onChange,
        multiLines = false,
        enabled = true,
        keyboardOptions = keyboardOptions
    )
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
    Input(modifier, label, value, placeholder,false, null, true, onChange)
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
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled : Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onChange: (String) -> Unit = {}
) {
    Input(
        Modifier.defaultMinSize(minHeight = 75.dp).then(modifier),
        label,
        value,
        placeholder,
        isError,
        errorMessage,
        false,
        onChange,
        true,
        enabled = enabled,
        keyboardOptions = keyboardOptions
    )
}

/**
 * Field containing a single line text input and a label.
 * @param label the name of the field
 * @param value the variable in which the value will be stored
 * @param placeholder indicative text placed in the input when no
 *     value is typed
 * @param keyboardOptions modifier to format the text while being typed
 * @param onChange callback to capture input value modifications
 */
@Composable
fun InputSearch(
    label: String? = null,
    value: String,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onChange: (String) -> Unit = {}
) {
    if (label != null) {
        Input(
            Modifier, label, value, placeholder, false, null, false, onChange, false,
            enabled = true,
            decorations = { innerTextField -> PlaceHolder(value, placeholder, innerTextField, R.drawable.search) },
            backgroundColor = White
        )
    } else {
        BasicTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth()
                .height(40.dp)
                .background(color = White, shape = RoundedCornerShape(50.dp))
                .padding(start = 0.dp, end = 16.dp, top = 0.dp, bottom = 0.dp),
            enabled = true,
            textStyle = typography.bodyLarge,
            keyboardOptions = keyboardOptions,
            singleLine = true,
            decorationBox = { innerTextField -> // Placeholder
                PlaceHolder(value, placeholder, innerTextField, R.drawable.search)
            }
        )
    }
}

@Composable
private fun Input(
    modifier: Modifier,
    label: String,
    value: String,
    placeholder: String,
    isError: Boolean = false,
    errorMessage: String? = null,
    password: Boolean,
    onChange: (String) -> Unit,
    multiLines: Boolean = false,
    enabled: Boolean = true,
    decorations: (@Composable (@Composable () -> Unit) -> Unit)? = null,
    color: Color = Black,
    backgroundColor: Color = LightGray,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
    ) {
        val textStyle = if (multiLines) typography.bodyMedium else typography.bodyLarge
        Label(label)
        BasicTextField(
            value = value,
            onValueChange = onChange,
            modifier = modifier.fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(size = if (multiLines) 10.dp else 50.dp))
                .background(backgroundColor),
            enabled = enabled,
            textStyle = textStyle.copy(color = color),
            keyboardOptions = if (password) KeyboardOptions(keyboardType = KeyboardType.Password) else keyboardOptions,
            singleLine = !multiLines,
            visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
            decorationBox = { innerTextField ->
                if (decorations == null) PlaceHolder(value, placeholder, innerTextField)
                else decorations(innerTextField)
            }
        )

        if (isError)
        errorMessage?.let {
            Text(
                text = errorMessage,
                style = typography.bodyMedium,
                color = Red,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PlaceHolder(
    value: String,
    placeholder: String,
    innerTextField: @Composable () -> Unit,
    iconPrefix: Int? = null,
    iconSuffix: Int? = null
) {
    Row (
        Modifier.padding(16.dp,  0.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        iconPrefix?.let { Icon(painterResource(it), "prefix icon", Modifier.size(20.dp)) }
        Box (
            Modifier.weight(1f).padding(0.dp, 10.dp).fillMaxHeight(),
            contentAlignment = Alignment.TopStart
        ) {
            innerTextField.invoke()
            if (value.isEmpty()) Text( // Placeholder
                placeholder,
                Modifier.alpha(.5f),
                style = typography.bodyMedium)
        }
        iconSuffix?.let { Icon(painterResource(it), "suffix icon") }
    }
}

@Composable
private fun Label(label: String) {
    Text(label, Modifier.alpha(0.75f), style = typography.bodyMedium)
}

