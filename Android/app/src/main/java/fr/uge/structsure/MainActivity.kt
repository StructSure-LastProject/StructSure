package fr.uge.structsure

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
//import com.csl.cs108library4a.Cs108Library4A
import com.csl.cslibrary4a.Cs108Library4A
//import com.csl.cslibrary4a.Cs108Library4A
import fr.uge.structsure.bluetooth.cs108.Connexion
import fr.uge.structsure.components.ButtonText
import fr.uge.structsure.database.AppDatabase
import fr.uge.structsure.ui.theme.Black

import fr.uge.structsure.ui.theme.Red
import fr.uge.structsure.settings.presentation.SettingsPage
import fr.uge.structsure.structuresPage.domain.StructureRepository
import fr.uge.structsure.structuresPage.presentation.HomePage
import fr.uge.structsure.ui.theme.StructSureTheme

class MainActivity : ComponentActivity() {
    companion object {
        lateinit var csLibrary4A: Cs108Library4A
    }

    private val db by lazy {
        AppDatabase.getDatabase(applicationContext)
    }

    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }

    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val structureRepository = StructureRepository(structureDao = db.structureDao())

        csLibrary4A = Cs108Library4A(this, TextView(this))

        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { /* Not needed */ }

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val canEnableBluetooth = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                perms[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else true

            if(canEnableBluetooth && !isBluetoothEnabled) {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                )
            )
        }

        setContent {
            StructSureTheme {
                val connexion = Connexion(LocalContext.current)
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    /*Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            // Header
                        },
                        content = {
                            var visible by remember { mutableStateOf(false) }
                            Column(
                                Modifier
                                    .blur(radius = if (visible) 10.dp else 0.dp)
                                    .fillMaxSize()
                                    .background(LightGray)
                                    .padding(it)
                                    .padding(25.dp),
                                verticalArrangement = Arrangement.spacedBy(25.dp, Alignment.Top),
                                horizontalAlignment = Alignment.Start,
                            ) {
                                Text("Bonjour")
                            }
                            if (visible) {
                                // PopUp()
                            }
                        },
                        floatingActionButton = {
//                        FloatingActionButton(
//                            onClick = { navController.navigate(Pages.TEST.name + "/-1") },
//                            shape = CircleShape,
//                            modifier = Modifier.padding(end=10.dp, bottom=10.dp),
//                            containerColor = MaterialTheme.colorScheme.primary
//                        ) {
//                            Icon(painterResource(R.drawable.icon_add), "New test", tint = MaterialTheme.colorScheme.onPrimary)
//                        }
                            BluetoothButton(connexion)
                        }
                        , floatingActionButtonPosition = FabPosition.Start
                    )*/
                }
            }

            val navController = rememberNavController()

            var connexion = true  // false si pas de connexion
            var loggedIn = true  // true si déjà connecté
            val homePage = if (connexion && !loggedIn) "ConnexionPage" else "HomePageNoCon"
            NavHost(navController = navController, startDestination = "HomePage") {
                /* Example code */
                composable("ecran1") { Ecran1(navController)}
                composable("ecran2") { Ecran2(navController)}

                /* Code à compléter */
                composable("HomePage") { HomePage(navController, structureRepository) }
                composable("ConnexionPage") { /*ConnexionCard(navController)*/ }
                composable("ScanPage"){ /*ScanPage(navController)*/ }
                composable("AlerteOk"){ /*AlerteOk(navController)*/ }
                composable("AlerteNok"){ /*AlerteNok(navController)*/ }
                composable("SettingsPage"){ SettingsPage() }
            }
        }




    }

    override fun onRestart() {
        super.onRestart()
        csLibrary4A.connect(null)
    }

    override fun onDestroy() {
        csLibrary4A.disconnect(true)
        super.onDestroy()
    }
}

//Exemples  -- A retirer quand plus d'utilité
@Composable
fun Ecran1(navController: NavController){
    ButtonText(
        text = "ecran1 Vers ecran2",
        color = Red,
        onClick = { navController.navigate("ecran2") },
        id = R.drawable.check
    )
}

@Composable
fun Ecran2(navController: NavController){
    ButtonText(
        text = "ecran2 vers ecran1",
        color = Black,
        onClick = { navController.navigate("ecran1") },
        id = R.drawable.x
    )
}
