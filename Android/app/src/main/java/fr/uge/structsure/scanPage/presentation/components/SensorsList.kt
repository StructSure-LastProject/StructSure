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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.uge.structsure.R
import fr.uge.structsure.components.Button
import fr.uge.structsure.components.InputCheck
import fr.uge.structsure.components.Select
import fr.uge.structsure.scanPage.domain.ScanViewModel
import fr.uge.structsure.structuresPage.data.FilterOptions
import fr.uge.structsure.structuresPage.data.SensorDB
import fr.uge.structsure.structuresPage.data.SortOptions
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.Typography
import fr.uge.structsure.ui.theme.White

/**
 * A composable function that displays a list of sensors during a scan.
 *
 * @param viewModel The ViewModel that provides the sensor data.
 * @param onClick action to run once a sensor of the list is clicked
 */
@Composable
fun SensorsList(viewModel: ScanViewModel, onClick: (sensor: SensorDB) -> Unit) {
    val sensors by viewModel.sensorsNotScanned.observeAsState(emptyList())

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
            SortFilter()
            SensorsList(sensors, onClick)
        }
    }
}

@Composable
private fun SortFilter() {
    val sorts = SortOptions.entries.map { it.displayName }
    val filters = FilterOptions.entries.map { it.displayName }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(20.dp, 15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.Start,
    ) {

        var sort by remember { mutableStateOf(sorts[0]) }
        var asc by remember { mutableStateOf(true) }
        var filter by remember { mutableStateOf(filters[0]) }
        var link by remember { mutableStateOf(true) }
        Select(
            label = "Trier",
            options = sorts,
            selected = sort,
            onSelect = { s -> sort = s }
        ) {
            Row (
                Modifier.fillMaxHeight()
                    .background(Black)
                    .padding(horizontal = 16.dp, vertical = 9.dp)
                    .clickable { asc = !asc }
            ) {
                Image(painterResource(if (asc) R.drawable.sort_desc else R.drawable.sort_asc), "sortIcon", colorFilter = ColorFilter.tint(White))
            }
        }

        Select(
            label = "Filtrer",
            options = filters,
            selected = filter,
            onSelect = { s -> filter = s }
        )

        InputCheck("Capteurs du plan sélectionné uniquement", link) { link = !link }
    }
}

/**
 * Basic list of sensors without any settings, just sensors.
 * @param sensors the sensors to display
 * @param onClick the action to run when a sensor is clicked
 */
@Composable
private fun SensorsList(sensors: List<SensorDB>, onClick: (sensor: SensorDB) -> Unit) {
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
}
