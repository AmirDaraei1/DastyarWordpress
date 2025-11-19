package ir.wordpressdashboard.feature.qrcode

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object QRCode

fun NavController.navigateToQRCode(navOptions: NavOptions? = null) {
    navigate(QRCode, navOptions)
}

fun NavGraphBuilder.qrCodeScreen(navigateToEnterShopAddress: () -> Unit) {
    composable<QRCode> {
        QRCodeRoute(navigateToEnterShopAddress)
    }
}
