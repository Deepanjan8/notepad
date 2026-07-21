package com.deepanjanxyz.notepad.core.model

data class SecuritySettings(
    val useDeviceLock: Boolean = false,
    val isUnlocked: Boolean = true,
    val darkMode: Boolean = true
)
