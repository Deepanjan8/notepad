package com.deepanjanxyz.notepad.features.settings

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.deepanjanxyz.notepad.core.security.BiometricPromptHelper
import com.deepanjanxyz.notepad.core.security.SecurityManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val securityManager: SecurityManager
) : ViewModel() {

    private val _useDeviceLock = MutableStateFlow(securityManager.isDeviceLockEnabled())
    val useDeviceLock: StateFlow<Boolean> = _useDeviceLock.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(
        if (securityManager.isDeviceLockEnabled()) "Biometric lock activated" else "Biometric lock deactivated"
    )
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    fun toggleDeviceLock(activity: FragmentActivity, enabled: Boolean) {
        if (enabled) {
            // Verify biometric before enabling lock
            if (BiometricPromptHelper.canAuthenticate(activity)) {
                BiometricPromptHelper.showBiometricPrompt(
                    activity = activity,
                    title = "Confirm Device Lock",
                    subtitle = "Authenticate to enable device lock for Elite Memo Pro",
                    onSuccess = {
                        securityManager.setDeviceLockEnabled(true)
                        _useDeviceLock.value = true
                        _errorMessage.value = null
                        _statusMessage.value = "Biometric lock activated"
                    },
                    onError = { error ->
                        _errorMessage.value = "Biometric authentication failed or cancelled"
                        _statusMessage.value = "Biometric authentication failed or cancelled"
                    }
                )
            } else {
                _errorMessage.value = "Device lock is not available or not set up on this device."
            }
        } else {
            // Verify biometric before disabling lock
            if (BiometricPromptHelper.canAuthenticate(activity)) {
                BiometricPromptHelper.showBiometricPrompt(
                    activity = activity,
                    title = "Disable Device Lock",
                    subtitle = "Authenticate to turn off device lock",
                    onSuccess = {
                        securityManager.setDeviceLockEnabled(false)
                        _useDeviceLock.value = false
                        _errorMessage.value = null
                        _statusMessage.value = "Biometric lock deactivated"
                    },
                    onError = { error ->
                        _errorMessage.value = "Biometric authentication failed or cancelled"
                        _statusMessage.value = "Biometric authentication failed or cancelled"
                    }
                )
            } else {
                securityManager.setDeviceLockEnabled(false)
                _useDeviceLock.value = false
                _statusMessage.value = "Biometric lock deactivated"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
