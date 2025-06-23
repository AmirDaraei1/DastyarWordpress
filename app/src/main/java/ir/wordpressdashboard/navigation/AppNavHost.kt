package ir.wordpressdashboard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import ir.wordpressdashboard.ui.homeScreen
import ir.wordpressdashboard.ui.navigateToHome
import ir.wordpressdashboard.feature.introduction.introductionScreen
import ir.wordpressdashboard.feature.introduction.navigateToIntroduction
import ir.wordpressdashboard.feature.login.loginScreen
import ir.wordpressdashboard.feature.login.navigateToLogin
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
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        splashScreen (navigateToIntroduction = navController::navigateToIntroduction)
        introductionScreen (navigateToLogin = navController::navigateToLogin)
        enterShopAddressNavigation (navigateToEnterShopAddress = navController::navigateToEnterShopAddress)
        loginScreen(navigateToHome = navController::navigateToHome, navigateToEnterShopAddressScreen = navController::navigateToEnterShopAddress)
        homeScreen()
    }
}
