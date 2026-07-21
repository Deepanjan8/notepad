package com.deepanjanxyz.notepad.core.security

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityManager @Inject constructor(
    private val encryptedPreferences: EncryptedPreferences
) {
    private val _isUnlocked = MutableStateFlow(!encryptedPreferences.useDeviceLock)
    val isUnlocked: StateFlow<Boolean> = _isUnlocked.asStateFlow()

    fun isDeviceLockEnabled(): Boolean {
        return encryptedPreferences.useDeviceLock
    }

    fun setDeviceLockEnabled(enabled: Boolean) {
        encryptedPreferences.useDeviceLock = enabled
        if (!enabled) {
            _isUnlocked.value = true
        }
    }

    fun unlock() {
        _isUnlocked.value = true
    }

    fun lock() {
        if (encryptedPreferences.useDeviceLock) {
            _isUnlocked.value = false
        }
    }

    fun onAppResume() {
        if (encryptedPreferences.useDeviceLock) {
            _isUnlocked.value = false
        } else {
            _isUnlocked.value = true
        }
    }
}
