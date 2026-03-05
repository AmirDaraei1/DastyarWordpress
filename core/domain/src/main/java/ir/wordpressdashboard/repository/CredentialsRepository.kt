package ir.wordpressdashboard.repository

interface CredentialsRepository {
    fun saveCredentials(baseUrl: String, consumerKey: String, secretKey: String)
    fun getBaseUrl(): String
    fun getConsumerKey(): String
    fun getSecretKey(): String
    fun isConfigured(): Boolean
}
