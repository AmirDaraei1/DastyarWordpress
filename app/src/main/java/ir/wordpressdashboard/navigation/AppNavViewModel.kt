package ir.wordpressdashboard.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.wordpressdashboard.i18n.AppLanguage
import ir.wordpressdashboard.i18n.AppStrings
import ir.wordpressdashboard.i18n.LanguageManager
import ir.wordpressdashboard.repository.CredentialsRepository
import javax.inject.Inject

@HiltViewModel
class AppNavViewModel @Inject constructor(
    private val credentialsRepository: CredentialsRepository,
    private val languageManager: LanguageManager,
) : ViewModel() {
    var currentLanguage by mutableStateOf(languageManager.getSelectedLanguage())
        private set

    val currentStrings: AppStrings
        get() = languageManager.getStrings(currentLanguage)

    fun hasLanguageBeenSelected(): Boolean = languageManager.hasLanguageBeenSelected()

    fun selectLanguage(language: AppLanguage) {
        languageManager.saveLanguage(language)
        currentLanguage = language
    }

    fun saveSiteAddress(address: String) {
        if (address.isNotBlank()) {
            val normalizedUrl = address.trimEnd('/') + "/wp-json/wc/v3/"
            credentialsRepository.saveCredentials(
                baseUrl = normalizedUrl,
                consumerKey = credentialsRepository.getConsumerKey(),
                secretKey = credentialsRepository.getSecretKey()
            )
        }
    }

    fun saveWpCredentials(username: String, appPassword: String) {
        if (username.isNotBlank() && appPassword.isNotBlank()) {
            credentialsRepository.saveWpCredentials(username, appPassword)
        }
    }
}
