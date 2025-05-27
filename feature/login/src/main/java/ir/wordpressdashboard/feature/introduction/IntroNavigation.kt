package ir.wordpressdashboard.feature.introduction

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object Introduction

fun NavController.navigateToIntroduction(navOptions: NavOptions? = null) {
    navigate(Introduction, navOptions)
}

fun NavGraphBuilder.introductionScreen(navigateToLogin: () -> Unit) {
    composable<Introduction> {
        IntroductionRoute(navigateToLogin)
    }
}