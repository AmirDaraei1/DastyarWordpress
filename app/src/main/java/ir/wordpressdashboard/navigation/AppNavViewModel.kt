package ir.wordpressdashboard.navigation

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.wordpressdashboard.repository.CredentialsRepository
import javax.inject.Inject

@HiltViewModel
class AppNavViewModel @Inject constructor(
    val credentialsRepository: CredentialsRepository
) : ViewModel() {

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
