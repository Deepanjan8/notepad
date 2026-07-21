package com.deepanjanxyz.notepad.core.database

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object DatabaseEncryption {

    private const val PREF_NAME = "elite_memo_sec_prefs"

    fun getEncryptedPrefs(context: Context) = runCatching {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        EncryptedSharedPreferences.create(
            PREF_NAME,
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SKEY_SEQUENCE,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }.getOrElse {
        context.getSharedPreferences("elite_memo_sec_prefs_fallback", Context.MODE_PRIVATE)
    }
}
