package ir.wordpressdashboard.repository

interface CredentialsRepository {
    fun saveCredentials(baseUrl: String, consumerKey: String, secretKey: String)
    fun getBaseUrl(): String
    fun getConsumerKey(): String
    fun getSecretKey(): String
    fun isConfigured(): Boolean

    // WordPress Application Password — برای wp/v2/media و سایر WP endpoints
    fun saveWpCredentials(username: String, appPassword: String)
    fun getWpUsername(): String
    fun getWpAppPassword(): String
    fun hasWpCredentials(): Boolean
}
