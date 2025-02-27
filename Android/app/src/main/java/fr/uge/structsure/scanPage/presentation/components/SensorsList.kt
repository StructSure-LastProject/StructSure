package fr.uge.structsure.scanPage.presentation.components

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import fr.uge.structsure.components.InputText
import fr.uge.structsure.components.InputTextArea
import fr.uge.structsure.components.PopUp
import fr.uge.structsure.components.Select
import fr.uge.structsure.components.Title
import fr.uge.structsure.scanPage.data.SensorValidator
import fr.uge.structsure.scanPage.data.TreePlan
import fr.uge.structsure.scanPage.data.ValidationResult
import fr.uge.structsure.scanPage.domain.ScanState
import fr.uge.structsure.scanPage.domain.ScanViewModel
import fr.uge.structsure.structuresPage.data.FilterOption
import fr.uge.structsure.structuresPage.data.SensorDB
import fr.uge.structsure.structuresPage.data.SortOption
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.LightGray
import fr.uge.structsure.ui.theme.Red
import fr.uge.structsure.ui.theme.White
import kotlinx.coroutines.delay

/**
 * A composable function that displays a list of sensors during a scan.
 *
 * @param scanViewModel The ViewModel that provides the sensor data.
 * @param onClick action to run once a sensor of the list is clicked
 */
@Composable
fun SensorsList(scanViewModel: ScanViewModel, context: Context, onClick: (sensor: SensorDB) -> Unit) {
    val planViewModel = scanViewModel.planViewModel
    val selected by planViewModel.selected.observeAsState()
    val sensors by scanViewModel.sensorsNotScanned.observeAsState(emptyList())
    val optionsVisible = remember { mutableStateOf(true) }

    val errorMessage by scanViewModel.addSensorError.observeAsState()
    var showAddSensorPopUp by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            showError = true
            delay(3000)
            showError = false
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
    ) {
        val icon = if (optionsVisible.value) R.drawable.chevron_up else R.drawable.chevron_down
        Title("Capteurs", false) {
            Button(icon, "Filter", Color.Black, Color.White) {
                optionsVisible.value = !optionsVisible.value
            }
            Button(R.drawable.plus, "Add", Color.White, Color.Black) {
                if (scanViewModel.currentScanState.value != ScanState.NOT_STARTED) {
                    showAddSensorPopUp = true
                } else {
                    showToast("Veuillez lancer un scan avant d'ajouter un capteur")
                }
            }
        }

        AnimatedVisibility(
            visible = showError && errorMessage != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(Red.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = Red,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (showAddSensorPopUp) {
            AddSensorPopUp(
                onSubmit = { controlChip, measureChip, name, note ->
                    scanViewModel.addSensor(controlChip, measureChip, name, note)
                    if (errorMessage == null) showAddSensorPopUp = false
                },
                onCancel = { showAddSensorPopUp = false }
            )
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
            placeholder = "Rechercher",
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

/**
 * Popup that prompt the user to create a new sensor with a name, a
 * control chip, a measure chip and a note.
 * @param onSubmit the action to run when the user submit values
 * @param onCancel the action to run when the user cancel
 */
@Composable
fun AddSensorPopUp(onSubmit: (controlChip: String, measureChip: String, name: String, note: String) -> Unit, onCancel: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var controlChip by remember { mutableStateOf("") }
    var measureChip by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var errors by remember { mutableStateOf(null as ValidationResult?) }

    PopUp(onCancel) {
        Title("Ajouter un capteur", false) {
            Button(R.drawable.x, "Annuler", Black, LightGray, onCancel)
            Button(R.drawable.check, "Valider", White, Black) {
                errors = SensorValidator.validate(controlChip, measureChip, name, note)
                if (errors == null) {
                    onSubmit(controlChip, measureChip, name, note)
                    onCancel()
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
            InputText(Modifier, "Nom *", name, "Capteur 42",
                errorMessage = errors?.nameError,
            ) {
                name = it.take(SensorValidator.MAX_NAME_LENGTH)
            }


            InputText(Modifier, "Puce Témoin *", controlChip, "E280 6F12 0000 0002 208F FACE",
                errorMessage = errors?.controlChipError
            ) {
                controlChip = it.take(SensorValidator.MAX_CHIP_LENGTH)
            }


            InputText(Modifier, "Puce Mesure *", measureChip, "E280 6F12 0000 0002 208F FACD",
                errorMessage = errors?.measureChipError,
            ) {
                measureChip = it.take(SensorValidator.MAX_CHIP_LENGTH)
            }

            InputTextArea(Modifier, "Note", note, "Commentaires (optionnel)",
                    errorMessage = errors?.noteError
            ) {
                note = it.take(SensorValidator.MAX_NOTE_LENGTH)
            }
        }
    }
}
