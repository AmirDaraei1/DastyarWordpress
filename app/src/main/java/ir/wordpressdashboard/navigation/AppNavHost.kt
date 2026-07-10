package ir.wordpressdashboard.navigation

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import ir.wordpressdashboard.feature.language.LanguageSelect
import ir.wordpressdashboard.feature.language.languageSelectScreen
import ir.wordpressdashboard.ui.navigation.homeScreen
import ir.wordpressdashboard.ui.navigation.navigateToHome
import ir.wordpressdashboard.feature.introduction.introductionScreen
import ir.wordpressdashboard.feature.introduction.navigateToIntroduction
import ir.wordpressdashboard.feature.siteraddress.enterShopAddressNavigation
import ir.wordpressdashboard.feature.siteraddress.navigateToEnterShopAddress
import ir.wordpressdashboard.feature.splash.navigateToSplash
import ir.wordpressdashboard.feature.splash.Splash
import ir.wordpressdashboard.feature.splash.splashScreen
import ir.wordpressdashboard.i18n.AppLanguage
import ir.wordpressdashboard.i18n.LocalStrings

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val viewModel: AppNavViewModel = hiltViewModel()
    val layoutDirection =
        if (viewModel.currentLanguage == AppLanguage.PERSIAN) LayoutDirection.Rtl else LayoutDirection.Ltr
    val view = LocalView.current

    SideEffect {
        view.layoutDirection =
            if (layoutDirection == LayoutDirection.Rtl) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
    }

    CompositionLocalProvider(
        LocalStrings provides viewModel.currentStrings,
        LocalLayoutDirection provides layoutDirection,
    ) {
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = if (viewModel.hasLanguageBeenSelected()) Splash::class else LanguageSelect::class,
        ) {
            languageSelectScreen { isEnglish ->
                viewModel.selectLanguage(if (isEnglish) AppLanguage.ENGLISH else AppLanguage.PERSIAN)
                navController.navigateToSplash()
            }
            splashScreen(
                navigateToIntroduction = navController::navigateToIntroduction,
                navigateToHome = { navController.navigateToHome() },
            )
            introductionScreen(navigateToLogin = navController::navigateToEnterShopAddress)

            enterShopAddressNavigation(navigateToHome = { credentials ->
                viewModel.saveSiteAddress(credentials.address)
                viewModel.saveWpCredentials(credentials.wpUsername, credentials.wpAppPassword)
                navController.navigateToHome()
            })
            homeScreen()
        }
    }
}