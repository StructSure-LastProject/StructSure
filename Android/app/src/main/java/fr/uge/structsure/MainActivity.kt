package fr.uge.structsure


import ToolBar
import android.os.Bundle
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
import fr.uge.structsure.database.AppDatabase
import fr.uge.structsure.dbTest.data.UserData
import fr.uge.structsure.start_scan.presentation.components.HeaderView
import fr.uge.structsure.start_scan.presentation.components.PlansView
import fr.uge.structsure.start_scan.presentation.components.sensors.list.SensorsListView
import fr.uge.structsure.start_scan.presentation.components.sensors.status.StructureSummaryView
import fr.uge.structsure.ui.theme.StructSureTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        val db = AppDatabase.getDatabase(this)
        val userDao = db.userDao()

        val user = UserData(uid = 1, firstName = "John", lastName = "Doe")
        val existingUser = userDao.getAll().find { it.uid == user.uid }

        if (existingUser == null) {
            userDao.insert(user)
        }

        setContent {
            StructSureTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        ToolBar(
                            onPlayClick = { /* Lancer le scan */ },
                            onSyncClick = { /* Synchroniser les données */ },
                            onContentClick = { /* Afficher du contenu supplémentaire */ },
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
                        StructureSummaryView()
                        PlansView(
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        SensorsListView(modifier = Modifier
                            .fillMaxWidth())
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

@Preview(showBackground = true)
@Composable
fun SensorsListView() {
    StructSureTheme {
        SensorsListView()
    }
}

@Preview(showBackground = true)
@Composable
fun StructureSummaryViewPreview() {
    StructSureTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            HeaderView()
            StructureSummaryView()
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