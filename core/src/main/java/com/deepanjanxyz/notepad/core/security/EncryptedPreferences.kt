package com.deepanjanxyz.notepad.core.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptedPreferences @Inject constructor(
    private val context: Context
) {
    private val sharedPreferences: SharedPreferences by lazy {
        runCatching {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                "elite_memo_app_settings",
                masterKey.keyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SKEY_SEQUENCE,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }.getOrElse {
            context.getSharedPreferences("elite_memo_app_settings_fallback", Context.MODE_PRIVATE)
        }
    }

    var useDeviceLock: Boolean
        get() = sharedPreferences.getBoolean(KEY_USE_DEVICE_LOCK, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_USE_DEVICE_LOCK, value).apply()

    var isDarkMode: Boolean
        get() = sharedPreferences.getBoolean(KEY_DARK_MODE, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_DARK_MODE, value).apply()

    companion object {
        private const val KEY_USE_DEVICE_LOCK = "use_device_lock"
        private const val KEY_DARK_MODE = "dark_mode"
    }
}
