# Proguard rules for Elite Memo Pro (SQLCipher, Room, Hilt, Security)
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
-dontwarn net.sqlcipher.**
-keep class androidx.security.crypto.** { *; }
