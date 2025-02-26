package fr.uge.structsure.scanPage.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R
import fr.uge.structsure.components.Button
import fr.uge.structsure.components.InputCheck
import fr.uge.structsure.components.InputSearch
import fr.uge.structsure.components.Select
import fr.uge.structsure.scanPage.data.TreePlan
import fr.uge.structsure.scanPage.domain.ScanViewModel
import fr.uge.structsure.structuresPage.data.FilterOption
import fr.uge.structsure.structuresPage.data.SensorDB
import fr.uge.structsure.structuresPage.data.SortOption
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.Typography
import fr.uge.structsure.ui.theme.White

/**
 * A composable function that displays a list of sensors during a scan.
 *
 * @param scanViewModel The ViewModel that provides the sensor data.
 * @param onClick action to run once a sensor of the list is clicked
 */
@Composable
fun SensorsList(scanViewModel: ScanViewModel, onClick: (sensor: SensorDB) -> Unit) {
    val planViewModel = scanViewModel.planViewModel
    val selected by planViewModel.selected.observeAsState()
    val sensors by scanViewModel.sensorsNotScanned.observeAsState(emptyList())
    val optionsVisible = remember { mutableStateOf(true) }

    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Capteurs", Modifier.weight(1f), style = Typography.titleLarge)
            val icon = if (optionsVisible.value) R.drawable.chevron_up else R.drawable.chevron_down
            Button(icon, "Filter", Color.Black, Color.White) {
                optionsVisible.value = !optionsVisible.value
            }
            Button(R.drawable.plus, "Add", Color.White, Color.Black)
        }
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(size = 20.dp))
                .background(Black.copy(alpha = 0.05f)),
            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
        ) {
            val sort = remember { mutableStateOf(SortOption.entries[0]) }
            val filter = remember { mutableStateOf(FilterOption.entries[0]) }
            val asc = remember { mutableStateOf(true) }
            val link = remember { mutableStateOf(false) }
            val search = remember { mutableStateOf("") }

            if (optionsVisible.value) SortFilter(search, link, asc, sort, filter)
            val filtered = remember(sensors, selected, search.value, link.value, asc.value, sort.value, filter.value) {
                sensors.filterAndSort(selected, search.value, link.value, asc.value, sort.value, filter.value)
            }
            SensorsList(filtered, onClick)
        }
    }
}

/**
 * Applies the given filters and sorting rules to the given list of
 * sensors and return the filtered values.
 * @param selected the selected plan
 * @param search the value of the search field
 * @param link whether to link to the selected plan or not
 * @param asc whether to order ascendant or descendant
 * @param sort the value of the sort field
 * @param filter the value of the filter field
 */
private fun List<SensorDB>.filterAndSort(
    selected: TreePlan?,
    search: String,
    link: Boolean,
    asc: Boolean,
    sort: SortOption,
    filter: FilterOption
): List<SensorDB> {
    val selectedPlan = selected?.plan?.id?:-1
    val list = sort.sort(this.filter {
        it.name.contains(search, true)
                && filter.filter(it)
                && (!link || it.plan == selectedPlan)
    })
    return if (asc) list else list.reversed()
}

/**
 * Header of the sensors list that contains the sort and filter inputs
 * to reduce the number of visible sensors.
 * @param search the value of the search field
 * @param link whether to link to the selected plan or not
 * @param asc whether to order ascendant or descendant
 * @param sort the value of the sort field
 * @param filter the value of the filter field
 */
@Composable
private fun SortFilter(
    search: MutableState<String>,
    link: MutableState<Boolean>,
    asc: MutableState<Boolean>,
    sort: MutableState<SortOption>,
    filter: MutableState<FilterOption>
) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(20.dp, 15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start,
    ) {
        InputSearch(
            label = "Rechercher",
            value = search.value,
            placeholder = "Rechercher...",
            onChange = {
                search.value = it
            }
        )

        Select(
            label = "Trier",
            options = SortOption.entries.map { it.displayName },
            selected = sort.value.displayName,
            onSelect = { s -> sort.value = SortOption.from(s) }
        ) {
            Row (
                Modifier.fillMaxHeight()
                    .background(Black)
                    .padding(horizontal = 16.dp, vertical = 9.dp)
                    .clickable { asc.value = !asc.value }
            ) {
                Image(painterResource(if (asc.value) R.drawable.sort_desc else R.drawable.sort_asc), "sortIcon", colorFilter = ColorFilter.tint(White))
            }
        }

        Select(
            label = "Filtrer",
            options = FilterOption.entries.map { it.displayName },
            selected = filter.value.displayName,
            onSelect = { s -> filter.value = FilterOption.from(s) }
        )

        InputCheck("Capteurs du plan sélectionné uniquement", link.value) { link.value = !link.value }
    }
}

/**
 * Basic list of sensors without any settings, just sensors.
 * @param sensors the sensors to display
 * @param onClick the action to run when a sensor is clicked
 */
@Composable
private fun SensorsList(sensors: List<SensorDB>, onClick: (sensor: SensorDB) -> Unit) {
    if (sensors.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp)
                .background(color = White, shape = RoundedCornerShape(size = 20.dp))
                .padding(start = 20.dp, top = 15.dp, end = 20.dp, bottom = 15.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start,
        ) {
            items(sensors) { sensor ->
                val state = SensorState.from(sensor.state)
                SensorBean(Modifier.fillMaxWidth(), sensor.name, state) { onClick(sensor) }
            }
        }
    } else {
        Text(
            "Aucun capteur",
            Modifier.fillMaxWidth()
                .background(White, RoundedCornerShape(size = 20.dp))
                .padding(start = 20.dp, top = 15.dp, end = 20.dp, bottom = 15.dp)
                .alpha(0.5f),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}
