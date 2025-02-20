package fr.uge.structsure

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.csl.cslibrary4a.Cs108Library4A
import fr.uge.structsure.alertes.Alerte
import fr.uge.structsure.bluetooth.cs108.Cs108Connector
import fr.uge.structsure.bluetooth.cs108.Cs108Connector.Companion.bluetoothAdapter
import fr.uge.structsure.connexionPage.ConnexionCard
import fr.uge.structsure.database.AppDatabase
import fr.uge.structsure.retrofit.RetrofitInstance
import fr.uge.structsure.scanPage.domain.ScanViewModel
import fr.uge.structsure.scanPage.presentation.ScanPage
import fr.uge.structsure.settingsPage.presentation.SettingsPage
import fr.uge.structsure.structuresPage.domain.StructureViewModel
import fr.uge.structsure.structuresPage.domain.StructureViewModelFactory
import fr.uge.structsure.structuresPage.presentation.HomePage
import java.util.concurrent.atomic.AtomicBoolean


class MainActivity : ComponentActivity() {
    companion object {
        var darkStatusBar: AtomicBoolean = AtomicBoolean(true)
        lateinit var csLibrary4A: Cs108Library4A

        /** Live data use to trigger redirect to the login page */
        val navigateToLogin = MutableLiveData<Boolean>()
        lateinit var db: AppDatabase
            private set
    }

    /** Name of the login page to avoid string duplication */
    private val connexionPage = "ConnexionPage"

    private lateinit var structureViewModel: StructureViewModel

    private val viewModelFactory: StructureViewModelFactory by lazy {
        StructureViewModelFactory(applicationContext)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = AppDatabase.getDatabase(applicationContext)
        RetrofitInstance.initFromPreferences(applicationContext)

        val accountDao = db.accountDao()
        val scanViewModel by lazy {
            ScanViewModel(applicationContext, structureViewModel)
        }
        structureViewModel = ViewModelProvider(this, viewModelFactory)[StructureViewModel::class.java]
        csLibrary4A = Cs108Library4A(this, TextView(this))
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothAdapter, filter)

        requestPermissions()

        setContent {
            SetDynamicStatusBar()
            val navController = rememberNavController()
            val connexionCS108 = Cs108Connector(applicationContext)
            connexionCS108.onBleConnected { success ->
                runOnUiThread {
                    if (!success) Toast.makeText(
                        applicationContext,
                        "Echec d'appairage Bluetooth",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            connexionCS108.onReady {
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "Interrogateur inititialisé!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            navigateToLogin.observeAsState(false).value.let {
                if (it) {
                    navController.navigate(connexionPage)
                    navigateToLogin.value = false
                }
            }

            val homePage =
                if (RetrofitInstance.isInitialized() && accountDao.get()?.token != null) "HomePage" else connexionPage
            NavHost(navController = navController, startDestination = homePage) {
                composable("HomePage") {
                    scanViewModel.setStructure(-1)
                    HomePage(connexionCS108, navController, accountDao, structureViewModel)
                    SetDynamicStatusBar()
                }
                composable("SettingsPage") { SettingsPage(navController) }
                composable("ScanPage?structureId={structureId}") { backStackEntry ->
                    val structureId = backStackEntry.arguments?.getString("structureId")?.toLong() ?: 1L
                    ScanPage(applicationContext, scanViewModel, structureId, connexionCS108, navController)
                    SetDynamicStatusBar()
                }
                composable(connexionPage) {
                    ConnexionCard(navController, accountDao, structureViewModel)
                    SetDynamicStatusBar()
                }
                composable("ScanPage") { /*ScanPage(navController)*/ }
                composable("Alerte?state={state}&name={name}&lastState={lastState}") { backStackEntry ->
                    val state = backStackEntry.arguments?.getString("state")?.toBoolean() ?: true
                    val name = backStackEntry.arguments?.getString("name").orEmpty()
                    val lastState = backStackEntry.arguments?.getString("lastState").orEmpty()
                    Alerte(navController, state, name, lastState)
                    SetDynamicStatusBar()
                }

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

    private fun requestPermissions() {
        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { /* Not needed */ }

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val canEnableBluetooth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissions[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else true

            if (canEnableBluetooth) {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            )
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}

/**
 * Changes the status bar text color to white or black depending on
 * the given theme. This is useful to make the text clear with custom
 * background color.
 */
@Composable
private fun ComponentActivity.SetDynamicStatusBar() {
    val systemBarStyle by remember {
        val defaultSystemBarColor = android.graphics.Color.TRANSPARENT
        mutableStateOf(
            SystemBarStyle.auto(
                lightScrim = defaultSystemBarColor,
                darkScrim = defaultSystemBarColor,
                detectDarkMode = {r ->
                    MainActivity.darkStatusBar.get() ?: (r.configuration.uiMode == Configuration.UI_MODE_NIGHT_YES)
                }
            )
        )
    }

    LaunchedEffect(systemBarStyle) {
        enableEdgeToEdge(
            statusBarStyle = systemBarStyle,
            navigationBarStyle = systemBarStyle
        )
    }
}