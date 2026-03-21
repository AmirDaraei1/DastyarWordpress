package ir.wordpressdashboard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import ir.wordpressdashboard.ui.navigation.homeScreen
import ir.wordpressdashboard.ui.navigation.navigateToHome
import ir.wordpressdashboard.feature.introduction.introductionScreen
import ir.wordpressdashboard.feature.introduction.navigateToIntroduction
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
        // Introduction مستقیم به صفحه رمز 16 رقمی می‌رود
        introductionScreen(navigateToLogin = navController::navigateToEnterShopAddress)

        enterShopAddressNavigation(navigateToHome = { credentials ->
            viewModel.saveSiteAddress(credentials.address)
            viewModel.saveWpCredentials(credentials.wpUsername, credentials.wpAppPassword)
            navController.navigateToHome()
        })
        homeScreen()
    }
}