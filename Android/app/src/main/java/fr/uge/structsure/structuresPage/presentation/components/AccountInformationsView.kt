package fr.uge.structsure.structuresPage.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fr.uge.structsure.R
import fr.uge.structsure.components.Button
import fr.uge.structsure.connexionPage.data.AccountDao
import fr.uge.structsure.requestLogin
import fr.uge.structsure.ui.theme.Black
import fr.uge.structsure.ui.theme.White

@Composable
fun AccountInformationsView(dao: AccountDao, navController: NavController) {
    val account by remember { mutableStateOf(dao.get()!!) }
    Box(
        modifier = Modifier
            .background(color = Black, shape = RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 15.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            UserInformations(
                name = account.firstName.capitalize(Locale.current) + " " + account.lastName.toUpperCase(Locale.current),
                id = account.login
            )
            Box(modifier = Modifier.align(Alignment.End)) {
                Button(
                    id = R.drawable.log_out,
                    background = White,
                    description = "Log out button that disconnect users who click on it.",
                    onClick = {
                        dao.disconnect(dao.get()!!.login)
                        navController.requestLogin()
                    }
                )
            }
        }
    }
}