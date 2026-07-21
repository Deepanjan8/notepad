package com.deepanjanxyz.notepad.core.database

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom
import java.util.Base64

object DatabaseEncryption {

    private const val PREF_NAME = "elite_memo_sec_prefs"
    private const val KEY_DB_PASSPHRASE = "db_passphrase"

    fun getPassphrase(context: Context): ByteArray {
        return runCatching {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val prefs = EncryptedSharedPreferences.create(
                context,
                PREF_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SKEY_SEQUENCE,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            var passphraseBase64 = prefs.getString(KEY_DB_PASSPHRASE, null)
            if (passphraseBase64 == null) {
                val randomBytes = ByteArray(32)
                SecureRandom().nextBytes(randomBytes)
                passphraseBase64 = Base64.getEncoder().encodeToString(randomBytes)
                prefs.edit().putString(KEY_DB_PASSPHRASE, passphraseBase64).apply()
            }
            Base64.getDecoder().decode(passphraseBase64)
        }.getOrElse {
            "EliteMemoProSecretKeyDefensiveFallback2026".toByteArray()
        }
    }

    fun createSupportFactory(context: Context): SupportFactory {
        val passphrase = getPassphrase(context)
        return SupportFactory(passphrase)
    }
}
