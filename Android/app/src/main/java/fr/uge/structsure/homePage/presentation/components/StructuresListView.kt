package fr.uge.structsure.homePage.presentation.components

import android.content.res.Resources
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.homePage.domain.StructureViewModel
import fr.uge.structsure.scanPage.presentation.components.toDp
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.Typography
import kotlinx.coroutines.launch

private fun StateMapper(state: Boolean): StructureStates {
    if (state){
        return StructureStates.AVAILABLE
    }
    return StructureStates.ONLINE
}

@Composable
fun StructuresListView(structureViewModel: StructureViewModel, navController: NavController) {
    val structures = structureViewModel.getAllStructures.observeAsState()
    val isRefreshing = structureViewModel.isRefreshing.observeAsState()

    LaunchedEffect(structureViewModel) {
        structureViewModel.getAllStructures()
    }

    val searchByName = remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 20.dp),
            style = Typography.titleLarge,
            text = "Ouvrages",
        )
        SearchBar(input = searchByName)
        PullToRefresh(
            isRefreshing.value == true,
            { structureViewModel.getAllStructures() }
        ) {
            structures.value?.forEach {
                if (it.name.toLowerCase(Locale.current).contains(searchByName.value.toLowerCase(Locale.current))) {
                    val state = remember(it) { mutableStateOf(StateMapper(it.downloaded)) }
                    Structure(it, state, structureViewModel, navController)
                }
            }
        }
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
private fun PullToRefresh(isRefreshing: Boolean, onRefresh: () -> Unit, content: @Composable () -> Unit) {
    val height = Resources.getSystem().displayMetrics.heightPixels.toDp - 200.dp
    val coroutineScope = rememberCoroutineScope()
    val state = rememberPullToRefreshState()

    PullToRefreshBox(
        modifier = Modifier.fillMaxWidth()
            .height(height)
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
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            content()
        }
    }
}