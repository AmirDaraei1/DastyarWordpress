package ir.wordpressdashboard.feature.splash

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object Splash

fun NavController.navigateToSplash(navOptions: NavOptions? = null) {
    navigate(Splash, navOptions)
}

fun NavGraphBuilder.splashScreen(navigateToIntroduction: () -> Unit) {
    composable<Splash> {
        SplashRoute(navigateToIntroduction)
    }
}