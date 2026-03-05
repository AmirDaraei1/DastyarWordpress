package ir.wordpressdashboard.feature.splash

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.wordpressdashboard.repository.CredentialsRepository
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val credentialsRepository: CredentialsRepository
) : ViewModel() {

    fun isAlreadyConfigured(): Boolean = credentialsRepository.isConfigured()
}
