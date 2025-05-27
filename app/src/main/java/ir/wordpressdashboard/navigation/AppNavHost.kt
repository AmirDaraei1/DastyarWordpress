package ir.wordpressdashboard.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import ir.wordpressdashboard.feature.home.homeScreen
import ir.wordpressdashboard.feature.home.navigateToHome
import ir.wordpressdashboard.feature.introduction.IntroductionScreen
import ir.wordpressdashboard.feature.introduction.introductionScreen
import ir.wordpressdashboard.feature.introduction.navigateToIntroduction
import ir.wordpressdashboard.feature.login.Login
import ir.wordpressdashboard.feature.login.loginScreen
import ir.wordpressdashboard.feature.login.navigateToLogin
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
        loginScreen(navigateToHome = navController::navigateToHome)
        homeScreen()
    }
}
