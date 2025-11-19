package ir.wordpressdashboard.feature.keys

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object ConSecKeys

fun NavController.navigateToConSecKeys(navOptions: NavOptions? = null) {
    navigate(ConSecKeys, navOptions)
}

fun NavGraphBuilder.conSecKeysScreen(navigateToEnterShopAddress: () -> Unit) {
    composable<ConSecKeys> {
        ConSecKeysRoute(navigateToEnterShopAddress)
    }
}
