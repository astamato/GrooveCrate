package com.example.myapplication.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class AuthManager(context: Context) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCredentials(username: String, token: String) {
        prefs.edit()
            .putString("discogs_username", username)
            .putString("discogs_token", token)
            .apply()
    }

    fun getUsername(): String? = prefs.getString("discogs_username", null)
    fun getToken(): String? = prefs.getString("discogs_token", null)

    fun hasCredentials(): Boolean {
        return getUsername() != null && getToken() != null
    }

    fun clearCredentials() {
        prefs.edit().clear().apply()
    }
}
