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
import androidx.navigation.NavController
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

        /** Name of the home page to avoid string duplication */
        const val HOME_PAGE = "HomePage"
    }

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
            val connexionCS108 = setUpCs108Connection()
            navigateToLogin.observeAsState(false).value.let {
                if (it) {
                    navController.requestLogin()
                    navigateToLogin.value = false
                }
            }

            NavHost(navController = navController, startDestination = getStartPage(scanViewModel)) {
                composable(HOME_PAGE) {
                    HomePage(connexionCS108, navController, accountDao, structureViewModel)
                    SetDynamicStatusBar()
                }
                composable("SettingsPage") { SettingsPage(navController) }
                composable("ScanPage?structureId={structureId}") { backStackEntry ->
                    val structureId = backStackEntry.arguments?.getString("structureId")?.toLong() ?: 1L
                    ScanPage(applicationContext, scanViewModel, structureId, connexionCS108, navController)
                    SetDynamicStatusBar()
                }
                composable("LoginPage?backRoute={backRoute}") { backStackEntry ->
                    val route = backStackEntry.arguments?.getString("backRoute")?:HOME_PAGE
                    ConnexionCard(navController, route, accountDao, structureViewModel)
                    SetDynamicStatusBar()
                }
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

    /**
     * Alias to display a toast with short duration
     * @param text text to display in the toast
     */
    private fun toastShort(text: String) = Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()

    /**
     * Creates a new connector and initializes it with connection and
     * ready callbacks
     * @return the initialized connector
     */
    private fun setUpCs108Connection(): Cs108Connector {
        val cs108Connection = Cs108Connector(applicationContext)
        cs108Connection.onBleConnected { success ->
            runOnUiThread {
                if (!success) toastShort("Echec d'appairage Bluetooth")
            }
        }
        cs108Connection.onReady {
            runOnUiThread { toastShort("Interrogateur inititialisé!") }
        }
        return cs108Connection
    }

    /**
     * Calculates the first page to display to the user when it opens
     * the app. Can be the homepage, connection page or even scan page
     * if a scan is on-going.
     * @return the route to the start page
     */
    private fun getStartPage(scanViewModel: ScanViewModel): String {
        if (!RetrofitInstance.isInitialized()) {
            return "LoginPage?backRoute=$HOME_PAGE" // URL not configured yet
        }
        val unfinished = db.scanDao().getUnfinishedScan()
        if (unfinished != null) {
            scanViewModel.setStructure(unfinished.structureId, unfinished.id)
            return "ScanPage?structureId=${unfinished.structureId}"
        } else if (db.accountDao().get()?.token == null) {
            return "LoginPage?backRoute=$HOME_PAGE" // User not logged in
        }
        return HOME_PAGE

    }
}

/**
 * Navigates to the given route and clear the back stack to prevent
 * from going back with the device back button.
 * @param route the page to navigate to
 */
fun NavController.navigateNoReturn(route: String) {
    navigate(route) {
        popUpTo(0) { inclusive = true } // Prevent going back
        launchSingleTop = true
    }
}

/**
 * Navigates to the login page, preventing from going back and setting
 * the current page as the next page to display after login success.
 */
fun NavController.requestLogin() {
    navigate("LoginPage?backRoute=${currentBackStackEntry?.destination?.route?:MainActivity.HOME_PAGE}") {
        popUpTo(0) { inclusive = true } // Prevent going back
        launchSingleTop = true
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