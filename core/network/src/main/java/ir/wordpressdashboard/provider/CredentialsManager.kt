package ir.wordpressdashboard.provider

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
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

    val isDebugBuild: Boolean =
        context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

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

    @get:JvmName("getWpUsernameProp")
    var wpUsername: String
        get() = prefs.getString(KEY_WP_USERNAME, "") ?: ""
        set(value) = prefs.edit { putString(KEY_WP_USERNAME, value) }

    @get:JvmName("getWpAppPasswordProp")
    var wpAppPassword: String
        get() = prefs.getString(KEY_WP_APP_PASSWORD, "") ?: ""
        set(value) = prefs.edit { putString(KEY_WP_APP_PASSWORD, value) }

    override fun saveCredentials(baseUrl: String, consumerKey: String, secretKey: String) {
        this.baseUrl = baseUrl
        this.consumerKey = consumerKey
        this.secretKey = secretKey
    }

    override fun saveWpCredentials(username: String, appPassword: String) {
        this.wpUsername = username
        this.wpAppPassword = appPassword
    }

    override fun getBaseUrl(): String = prefs.getString(KEY_BASE_URL, DEFAULT_BASE_URL) ?: DEFAULT_BASE_URL
    override fun getConsumerKey(): String = prefs.getString(KEY_CONSUMER_KEY, "") ?: ""
    override fun getSecretKey(): String = prefs.getString(KEY_SECRET_KEY, "") ?: ""
    override fun getWpUsername(): String = prefs.getString(KEY_WP_USERNAME, "") ?: ""
    override fun getWpAppPassword(): String = prefs.getString(KEY_WP_APP_PASSWORD, "") ?: ""
    override fun isConfigured(): Boolean =
        getBaseUrl().isNotEmpty() && getWpUsername().isNotEmpty() && getWpAppPassword().isNotEmpty()
    override fun hasWpCredentials(): Boolean =
        getWpUsername().isNotEmpty() && getWpAppPassword().isNotEmpty()

    companion object {
        private const val KEY_BASE_URL        = "base_url"
        private const val KEY_CONSUMER_KEY    = "consumer_key"
        private const val KEY_SECRET_KEY      = "secret_key"
        private const val KEY_WP_USERNAME     = "wp_username"
        private const val KEY_WP_APP_PASSWORD = "wp_app_password"
        const val DEFAULT_BASE_URL = "https://digikhune.com/wp-json/wc/v3/"
    }
}
