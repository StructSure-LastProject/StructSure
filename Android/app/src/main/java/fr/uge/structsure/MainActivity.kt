package fr.uge.structsure


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import fr.uge.structsure.database.AppDatabase
import fr.uge.structsure.dbTest.data.UserData
import fr.uge.structsure.start_scan.data.ScanEntity
import fr.uge.structsure.start_scan.data.SensorEntity
import fr.uge.structsure.start_scan.domain.ScanViewModel
import fr.uge.structsure.start_scan.presentation.components.HeaderView
import fr.uge.structsure.start_scan.presentation.components.PlansView
import fr.uge.structsure.start_scan.presentation.components.StructureSummaryView
import fr.uge.structsure.start_scan.presentation.components.ToolBar
import fr.uge.structsure.start_scan.presentation.components.sensors.list.SensorsListView
import fr.uge.structsure.ui.theme.StructSureTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val db = AppDatabase.getDatabase(this)

        // Récupération de l'instance DAO pour Scan
        val scanDao = db.scanDao()


        setContent {
            StructSureTheme {
                val scanViewModel: ScanViewModel =
                    viewModel(factory = ScanViewModel.provideFactory(scanDao))

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        ToolBar(
                            currentState = scanViewModel.currentScanState.value,
                            onPlayClick = {
                                scanViewModel.createNewScan(
                                    structureId = 1, // Remplacer par l'ID de la structure actuelle
                                    sensorDetails = listOf(
                                        "Capteur 1" to (100 to 200),
                                        "Capteur 2" to (300 to 400)
                                    )
                                )
                                Toast.makeText(
                                    this@MainActivity,
                                    "Scan démarré !",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onPauseClick = {
                                scanViewModel.pauseScan()
                                Toast.makeText(
                                    this@MainActivity,
                                    "Scan mis en pause.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onStopClick = {
                                scanViewModel.stopScan()
                                Toast.makeText(
                                    this@MainActivity,
                                    "Scan arrêté.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onSyncClick = {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Synchronisation en cours...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onContentClick = {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Accès aux notes...",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .offset(x = 0.dp, y = 815.dp)
                                .width(428.dp)
                                .height(113.dp)

                        )
                    }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                    ) {
                        HeaderView()
                        StructureSummaryView(viewModel = scanViewModel)
                        PlansView(
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        SensorsListView(
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

