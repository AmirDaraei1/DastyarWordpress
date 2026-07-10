package ir.wordpressdashboard.i18n

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

enum class AppLanguage { ENGLISH, PERSIAN }

@Singleton
class LanguageManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("language_prefs", Context.MODE_PRIVATE)

    fun hasLanguageBeenSelected(): Boolean = prefs.contains(KEY_LANGUAGE)

    fun getSelectedLanguage(): AppLanguage {
        val lang = prefs.getString(KEY_LANGUAGE, AppLanguage.ENGLISH.name) ?: AppLanguage.ENGLISH.name
        return AppLanguage.valueOf(lang)
    }

    fun saveLanguage(language: AppLanguage) {
        prefs.edit().putString(KEY_LANGUAGE, language.name).apply()
    }

    fun getStrings(language: AppLanguage = getSelectedLanguage()): AppStrings = when (language) {
        AppLanguage.ENGLISH -> EnglishStrings
        AppLanguage.PERSIAN -> PersianStrings
    }

    companion object {
        private const val KEY_LANGUAGE = "selected_language"
    }
}
