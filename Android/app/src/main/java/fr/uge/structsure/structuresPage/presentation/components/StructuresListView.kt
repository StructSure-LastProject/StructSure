package fr.uge.structsure.structuresPage.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import fr.uge.structsure.MainActivity
import fr.uge.structsure.structuresPage.data.StructureData
import fr.uge.structsure.structuresPage.domain.StructureViewModel
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun StructuresListView(
    structureViewModel: StructureViewModel,
    navController: NavController
) {
    val structures = structureViewModel.getAllStructures.observeAsState()

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
        structures.value?.filter { it.name.contains(searchByName.value) }?.forEach {
            Structure(it, structureViewModel, navController)
        }
    }
}