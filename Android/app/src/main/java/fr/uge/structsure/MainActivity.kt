package fr.uge.structsure

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
import androidx.compose.ui.platform.LocalContext
import com.csl.cs108library4a.Cs108Library4A
import fr.uge.structsure.bluetooth.cs108.Connexion
import fr.uge.structsure.bluetoothConnection.presentation.BluetoothPage
import fr.uge.structsure.ui.theme.StructSureTheme

class MainActivity : ComponentActivity() {
    companion object {
        lateinit var csLibrary4A: Cs108Library4A
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
        csLibrary4A = Cs108Library4A(this, TextView(this))

        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { /* Not needed */ }

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val canEnableBluetooth = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                perms[android.Manifest.permission.BLUETOOTH_CONNECT] == true
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
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                )
            )
        }

        setContent {
            StructSureTheme {
                val connexion = Connexion(LocalContext.current)
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    BluetoothPage(connexion)
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
}
