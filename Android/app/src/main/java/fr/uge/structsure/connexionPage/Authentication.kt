import android.util.Log
import androidx.navigation.NavController
import fr.uge.structsure.retrofit.LoginApi
import fr.uge.structsure.retrofit.RetrofitInstance
import fr.uge.structsure.retrofit.response.Datamodel
import fr.uge.structsure.retrofit.response.UserAuthResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Optional


suspend fun auth(login:String, password:String, navController: NavController): String {
    if (login.isEmpty()){
        return "Veuillez renseigner votre identifiant"
    } else if (password.isEmpty()){
        return "Veuillez renseigner votre mot de passe"
    }

    val response = getFromApi(login, password)
    if (response.isPresent){ //si la paire (login,password) est dans la bdd
        val token = response.get().token
        println(token)
        navController.navigate("HomePage");
        return ""
    }

    return "Identifiant et/ou mot de passe incorrect"
}

private fun getApiInterface(): LoginApi {
    return RetrofitInstance.loginApi
}

private suspend fun getFromApi(login: String, password: String): Optional<UserAuthResponse> {
    return withContext(Dispatchers.IO) {
        val apiInterface = getApiInterface()
        val call = apiInterface.userAuth(Datamodel(login, password))

        try {
            val response = call.execute()  // Synchronous call

            if (response.isSuccessful && response.body() != null) {
                return@withContext Optional.of(response.body()!!)
            } else {
                Log.d("ERROR API", "Response not successful or body is null")
                return@withContext Optional.empty()   // Return null if the response is not successful
            }

        } catch (e: Exception) {
            Log.d("ERROR API", "Exception: ${e.message}")
            return@withContext Optional.empty()  // Return null in case of an exception
        }
    }
}

