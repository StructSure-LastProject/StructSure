import android.content.Context
import android.util.Log
import androidx.navigation.NavController
import fr.uge.structsure.MainActivity.Companion.db
import fr.uge.structsure.connexionPage.data.AccountEntity
import fr.uge.structsure.navigateNoReturn
import fr.uge.structsure.retrofit.LoginApi
import fr.uge.structsure.retrofit.RetrofitInstance
import fr.uge.structsure.retrofit.response.Datamodel
import fr.uge.structsure.retrofit.response.UserAuthResponse
import fr.uge.structsure.structuresPage.domain.StructureViewModel
import fr.uge.structsure.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Optional

private const val TAG = "AuthenticationAPI"

suspend fun auth(login:String,
                 password:String,
                 context: Context,
                 navController: NavController,
                 backRoute: String,
                 structureViewModel: StructureViewModel? = null ): String {
    if (login.isEmpty()){
        return "Veuillez renseigner votre identifiant"
    } else if (password.isEmpty()) {
        return "Veuillez renseigner votre mot de passe"
    }

    val response = getFromApi(login, password)
    if (response.isPresent){ //si la paire (login,password) est dans la bdd
        val account = response.get()
        val entity = AccountEntity(account.login, account.token, account.type, account.firstName, account.lastName, account.role)
        structureViewModel?.tryUploadScans(true, false, login)
        clearDataIfNewUser(context, account.login)
        val out = db.accountDao().upsertAccount(entity)
        // TODO clear all data from the DB if dap.upsertAccount return false
        navController.navigateNoReturn(backRoute)
        return ""
    }

    return "Identifiant et/ou mot de passe incorrect"
}

/**
 * Wipes out all the data from the previous session if the given login
 * is different from the last one.
 * @param context used to perform local storage files operations
 * @param login the new login of the current user
 */
private fun clearDataIfNewUser(context: Context, login: String) {
    val lastUser = db.accountDao().getLogin()
    if (login != lastUser) {
        db.scanEditsDao().clear()
        db.scanDao().clear()
        db.resultDao().clear()
        db.sensorDao().clear()
        db.planDao().clear()
        db.structureDao().clear()
        FileUtils.deletePlans(context)
    }
}

private fun getApiInterface(): LoginApi {
    if (!RetrofitInstance.isInitialized()) {
        throw IllegalStateException("Retrofit n'a pas été initialisé. Configurez l'adresse du serveur.")
    }
    return RetrofitInstance.loginApi
}


private suspend fun getFromApi(login: String, password: String): Optional<UserAuthResponse> {
    if(!RetrofitInstance.isInitialized()){
        return Optional.empty()
    }
    return withContext(Dispatchers.IO) {
        val apiInterface = getApiInterface()
        val call = apiInterface.userAuth(Datamodel(login, password))

        try {
            val response = call.execute()  // Synchronous call

            if (!response.isSuccessful) {
                Log.w(TAG, "API call for login failed with code ${response.code()} - ${response.message()}")
            } else if (response.body() == null) {
                Log.w(TAG, "Invalid API response for login: body is null")
            } else {
                return@withContext Optional.of(response.body()!!)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Exception: ${e.message}")
        }
        return@withContext Optional.empty()
    }
}

