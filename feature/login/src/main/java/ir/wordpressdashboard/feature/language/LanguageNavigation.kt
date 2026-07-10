package ir.wordpressdashboard.feature.language

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
object LanguageSelect

fun NavController.navigateToLanguageSelect() = navigate(LanguageSelect)

fun NavGraphBuilder.languageSelectScreen(
    onLanguageSelected: (isEnglish: Boolean) -> Unit,
) {
    composable<LanguageSelect> {
        LanguageSelectScreen(onLanguageSelected = onLanguageSelected)
    }
}
