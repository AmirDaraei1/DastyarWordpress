package ir.wordpressdashboard.provider

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.wordpressdashboard.repository.CredentialsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CredentialsManager @Inject constructor(
    @ApplicationContext private val context: Context
) : CredentialsRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("wp_credentials", Context.MODE_PRIVATE)

    @get:JvmName("getBaseUrlProp")
    var baseUrl: String
        get() = prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
        set(value) = prefs.edit { putString(KEY_BASE_URL, value) }

    @get:JvmName("getConsumerKeyProp")
    var consumerKey: String
        get() = prefs.getString(KEY_CONSUMER_KEY, "") ?: ""
        set(value) = prefs.edit { putString(KEY_CONSUMER_KEY, value) }

    @get:JvmName("getSecretKeyProp")
    var secretKey: String
        get() = prefs.getString(KEY_SECRET_KEY, "") ?: ""
        set(value) = prefs.edit { putString(KEY_SECRET_KEY, value) }

    override fun saveCredentials(baseUrl: String, consumerKey: String, secretKey: String) {
        this.baseUrl = baseUrl
        this.consumerKey = consumerKey
        this.secretKey = secretKey
    }

    override fun getBaseUrl(): String = prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
    override fun getConsumerKey(): String = prefs.getString(KEY_CONSUMER_KEY, "") ?: ""
    override fun getSecretKey(): String = prefs.getString(KEY_SECRET_KEY, "") ?: ""
    override fun isConfigured(): Boolean =
        getConsumerKey().isNotEmpty() && getSecretKey().isNotEmpty() && getBaseUrl().isNotEmpty()

    companion object {
        private const val KEY_BASE_URL = "base_url"
        private const val KEY_CONSUMER_KEY = "consumer_key"
        private const val KEY_SECRET_KEY = "secret_key"
        const val DEFAULT_BASE_URL = "https://digikhune.com/wp-json/wc/v3/"
    }
}
