package ir.wordpressdashboard.usecase

import ir.wordpressdashboard.repository.CredentialsRepository
import javax.inject.Inject

class SaveCredentialsUseCase @Inject constructor(
    private val credentialsRepository: CredentialsRepository
) {
    operator fun invoke(baseUrl: String, consumerKey: String, secretKey: String) {
        credentialsRepository.saveCredentials(
            baseUrl = baseUrl.trimEnd('/') + "/",
            consumerKey = consumerKey,
            secretKey = secretKey
        )
    }
}
