package com.namma.santheledger.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    private const val PREF_NAME = "language_pref"
    private const val KEY_LANGUAGE = "selected_language"

    data class AppLanguage(
        val code: String,
        val name: String,
        val nativeName: String
    )

    val supportedLanguages = listOf(
        AppLanguage("en", "English", "English"),
        AppLanguage("kn", "Kannada", "ಕನ್ನಡ"),
        AppLanguage("hi", "Hindi", "हिन्दी"),
        AppLanguage("ta", "Tamil", "தமிழ்"),
        AppLanguage("te", "Telugu", "తెలుగు"),
        AppLanguage("mr", "Marathi", "मराठी")
    )

    fun getSelectedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun setSelectedLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()
    }

    fun applyLocale(context: Context): Context {
        val langCode = getSelectedLanguage(context)
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    fun getLanguageName(code: String): String {
        return supportedLanguages.find { it.code == code }?.nativeName ?: "English"
    }
}
