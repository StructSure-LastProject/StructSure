package fr.uge.structsure.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.White
import kotlinx.coroutines.launch

/**
 * Title expected to be used at the beginning of a section that
 * contains a title text and buttons.
 * @param text title of the section
 * @param content buttons to add in the title
 */
@Composable
fun Title (
    text: String,
    indent: Boolean = true,
    content: @Composable () -> Unit = {}
) {
    Row(
        Modifier.padding(start = if (indent) 20.dp else 0.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text,
            style = MaterialTheme.typography.titleLarge,
            modifier= Modifier
                .fillMaxWidth()
                .weight(2f))
        content()
    }
}

/**
 * Dialog or PopUp (it depends how you prefer to call it) that makes
 * the background darker and displays a white centered window that
 * contains the given content.
 *
 * Recommendations:
 * Create a mutable boolean to save the popup visibility: 'var visible by remember { mutableStateOf(true) }'
 * Add a blur modifier to the container that will be behind the popup: 'Modifier.blur(radius = if (visible) 10.dp else 0.dp)'
 * Place the popup component in a if: 'if (visible) PopUp(Modifier, onClose = { visible = false }) { ... }'
 * @param onClose callback when the user click outside the popup
 * @param content component to place inside the popup
 */
@Composable
fun PopUp(
    onClose: () -> Unit,
    content: @Composable () -> Unit = {},
) {
    val interactionSource by remember { mutableStateOf(MutableInteractionSource()) }
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .imePadding()
                .clickable(interactionSource, null, onClick = onClose)
                .background(Black.copy(.25f))
                .padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, true) {
                        // Disable the ripple when clicking
                    }
                    .background(White)
                    .padding(25.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                content.invoke()
            }
            Spacer( modifier = Modifier.windowInsetsBottomHeight(WindowInsets.ime))
        }
    }
}

/**
 * Two columns long array that can display details of a sensor.
 * @param color the color of the text to display
 * @param labelLeft the label of the first column
 * @param valueLeft the value of the first column
 * @param labelRight the label of the second column
 * @param valueRight the label of the second column
 */
@Composable
fun SensorDetails(color: Color, labelLeft: String, valueLeft: String, labelRight: String, valueRight: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(25.dp, Alignment.CenterHorizontally)
    ) {
        SensorDetail(color, labelLeft, valueLeft)
        SensorDetail(color, labelRight, valueRight)
    }
}

/**
 * Displays the details of one attribute of a sensor (name of state).
 * @param color the color of the text
 * @param title the name of the attribute to display
 * @param value the value
 */
@Composable
private fun RowScope.SensorDetail(color: Color, title: String, value: String) {
    Column (
        Modifier.weight(.5f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            title,
            Modifier.alpha(0.5f),
            color,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            value,
            color = color,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

/**
 * List that can trigger refresh event to reload structures list.
 * @param isRefreshing whether or not the data  is currently being reloaded
 * @param onRefresh action to run on refresh request
 * @param content the element to put in the column
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefresh(modifier: Modifier = Modifier, isRefreshing: Boolean, onRefresh: () -> Unit, content: @Composable () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val state = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = Modifier.fillMaxWidth()
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.TopCenter,
        isRefreshing = isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                onRefresh()
            }
        },
        state = state,
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isRefreshing,
                containerColor = LightGray,
                color = Black,
                state = state
            )
        }
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(scrollState)
                .then(modifier),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            content()
        }
    }
}