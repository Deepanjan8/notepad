# Elite Memo Pro

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Android Security](https://img.shields.io/badge/Privacy-100%25%20Offline-brightgreen.svg)]()
[![Java Target](https://img.shields.io/badge/Java-21-blue.svg)]()
[![Compile SDK](https://img.shields.io/badge/Compile%20SDK-37-orange.svg)]()

**Elite Memo Pro** is a privacy-first, offline-only Markdown notepad Android application built with Clean Architecture, Jetpack Compose, Material 3, SQLCipher encrypted local database storage, and Biometric authentication.

---

## 🔒 Privacy & Security Guarantee

- **Zero Internet Permission**: The app contains **NO** network permissions (`android.permission.INTERNET`) in any `AndroidManifest.xml`. It is 100% offline.
- **Local Encrypted Storage**: Note titles, contents, timestamps, and metadata are encrypted on disk via **SQLCipher (256-bit AES)** and hardware-backed keys in Android KeyStore.
- **Optional Biometric Lock**: Toggle "Use Device Lock" in Settings to require Fingerprint, Face, or Device PIN/Pattern authentication on launch.

---

## 🚀 Technology Stack & Architecture

- **Architecture**: Modular Clean Architecture (`:app`, `:core`, `:features`)
- **UI**: Jetpack Compose + Material 3 + Adaptive Edge-to-Edge Layouts
- **Language**: Kotlin 2.3.21 / Java 21
- **Database**: Room Database + SQLCipher (`net.zetetic:sqlcipher-android`)
- **Security**: Jetpack Security EncryptedSharedPreferences + Biometric Prompt API
- **Dependency Injection**: Hilt
- **Asynchrony**: Coroutines + Flow

---

## 🛠️ Repository Structure

```
.
├── .github/workflows/android_build.yml # Remote GitHub Actions CI/CD Pipeline
├── app/                                 # Entry point & Application assembly
├── core/                                # Models, Database, Security, UI Components
├── features/                            # Notes, Editor, Settings & Lock Screens
├── gradle/                              # Version Catalog & Wrapper
├── build.gradle.kts                     # Root build script
├── settings.gradle.kts                  # Multi-module gradle configuration
└── LICENSE                              # MIT License
```

---

## 📦 Version Information

- **Application ID**: `com.deepanjanxyz.notepad`
- **Version Code**: `5`
- **Version Name**: `1.0.5`
- **Min SDK**: `26` (Android 8.0)
- **Compile SDK**: `37`
- **Target SDK**: `37`

---

## 📄 License

Distributed under the **MIT License**. Copyright (c) 2026 Deepanjan. See [`LICENSE`](LICENSE) for details.
