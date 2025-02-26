package fr.uge.structsure.structuresPage.domain


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ConnectivityViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConnectivityViewModel::class.java)) {
            return ConnectivityViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



class ConnectivityViewModel(private val context: Context) : ViewModel() {
    //var isConnected = mutableStateOf(false)
    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> = _isConnected

    init {
        startConnectivityCheck()
    }

    private fun startConnectivityCheck() {
        viewModelScope.launch {
            while (true) {
                checkConnectivity()
                delay(5000) // Delay for 5 seconds
            }
        }
    }

    private fun checkConnectivity() {
        viewModelScope.launch {
            _isConnected.postValue(isNetworkAvailable())
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork?.let {
                connectivityManager.getNetworkCapabilities(it)
            }
            networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }
}