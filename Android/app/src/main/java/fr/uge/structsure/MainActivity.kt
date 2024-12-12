package fr.uge.structsure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import fr.uge.structsure.start_scan.domain.ScanState
import fr.uge.structsure.start_scan.domain.ScanViewModel
import fr.uge.structsure.start_scan.presentation.components.ToolBar
import fr.uge.structsure.start_scan.presentation.components.HeaderView
import fr.uge.structsure.start_scan.presentation.components.PlansView
import fr.uge.structsure.start_scan.presentation.components.StructureSummaryView
import fr.uge.structsure.ui.theme.StructSureTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scanViewModel: ScanViewModel = viewModel()

            StructSureTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        ToolBar(
                            currentState = scanViewModel.scanState.value,
                            onPlayClick = { scanViewModel.startScan() },
                            onPauseClick = { scanViewModel.pauseScan() },
                            onStopClick = { scanViewModel.stopScan() },
                            onSyncClick = { /* Ajoutez la synchronisation */ },
                            onContentClick = { /* Ajoutez une action pour les notes */ }
                        )
                    }
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .verticalScroll(rememberScrollState())
                    ) {
                        HeaderView()
                        StructureSummaryView(viewModel = scanViewModel)
                        PlansView()
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HeaderViewPreview() {
    StructSureTheme {
        HeaderView()
    }
}
