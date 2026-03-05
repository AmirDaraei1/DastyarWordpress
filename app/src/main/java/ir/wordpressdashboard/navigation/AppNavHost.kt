package ir.wordpressdashboard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import ir.wordpressdashboard.ui.homeScreen
import ir.wordpressdashboard.ui.navigateToHome
import ir.wordpressdashboard.feature.introduction.introductionScreen
import ir.wordpressdashboard.feature.introduction.navigateToIntroduction
import ir.wordpressdashboard.feature.keys.conSecKeysScreen
import ir.wordpressdashboard.feature.keys.navigateToConSecKeys
import ir.wordpressdashboard.feature.login.loginScreen
import ir.wordpressdashboard.feature.login.navigateToLogin
import ir.wordpressdashboard.feature.qrcode.navigateToQRCode
import ir.wordpressdashboard.feature.qrcode.qrCodeScreen
import ir.wordpressdashboard.feature.siteraddress.enterShopAddressNavigation
import ir.wordpressdashboard.feature.siteraddress.navigateToEnterShopAddress
import ir.wordpressdashboard.feature.splash.Splash
import ir.wordpressdashboard.feature.splash.splashScreen
import kotlin.reflect.KClass

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    startDestination: KClass<*> = Splash::class
) {
    val navController = rememberNavController()
    val viewModel: AppNavViewModel = hiltViewModel()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        splashScreen(
            navigateToIntroduction = navController::navigateToIntroduction,
            navigateToHome = { navController.navigateToHome() }
        )
        introductionScreen(navigateToLogin = navController::navigateToLogin)
        loginScreen(
            navigateToConSecKeys = navController::navigateToConSecKeys,
            navigateToQRCode = navController::navigateToQRCode
        )
        conSecKeysScreen(navigateToEnterShopAddress = navController::navigateToEnterShopAddress)
        qrCodeScreen(navigateToEnterShopAddress = navController::navigateToEnterShopAddress)
        enterShopAddressNavigation(navigateToHome = { address ->
            viewModel.saveSiteAddress(address)
            navController.navigateToHome()
        })
        homeScreen()
    }
}