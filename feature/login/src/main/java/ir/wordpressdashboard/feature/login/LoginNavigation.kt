package ir.wordpressdashboard.feature.login

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object Login

fun NavController.navigateToLogin(navOptions: NavOptions? = null) {
    navigate(Login, navOptions)
}

fun NavGraphBuilder.loginScreen(navigateToConSecKeys: () -> Unit, navigateToQRCode: () -> Unit) {
    composable<Login> {
        LoginRoute(navigateToConSecKeys, navigateToQRCode)
    }
}
