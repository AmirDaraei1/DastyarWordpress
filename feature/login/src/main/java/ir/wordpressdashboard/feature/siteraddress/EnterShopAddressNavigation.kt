package ir.wordpressdashboard.feature.siteraddress

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object EnterShopAddressNavigation

fun NavController.navigateToEnterShopAddress(navOptions: NavOptions? = null) {
    navigate(EnterShopAddressNavigation, navOptions)
}

fun NavGraphBuilder.enterShopAddressNavigation(navigateToEnterShopAddress: () -> Unit) {
    composable<EnterShopAddressNavigation> {
        EnterShopAddressRoute (navigateToEnterShopAddress)
    }
}