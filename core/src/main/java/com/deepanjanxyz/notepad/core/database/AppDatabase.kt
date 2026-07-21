package com.deepanjanxyz.notepad.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [NoteEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = runCatching {
                    val factory = DatabaseEncryption.createSupportFactory(context)
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "elite_memo_encrypted.db"
                    )
                        .openHelperFactory(factory)
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                db.execSQL("INSERT INTO categories (id, name, colorHex, iconName) VALUES (1, 'General', '#6200EE', 'folder')")
                                db.execSQL("INSERT INTO categories (id, name, colorHex, iconName) VALUES (2, 'Work', '#03DAC6', 'briefcase')")
                                db.execSQL("INSERT INTO categories (id, name, colorHex, iconName) VALUES (3, 'Personal', '#FF0266', 'user')")
                                db.execSQL("INSERT INTO categories (id, name, colorHex, iconName) VALUES (4, 'Ideas', '#FFDE03', 'lightbulb')")
                            }
                        })
                        .fallbackToDestructiveMigration()
                        .build()
                }.getOrElse {
                    // Fallback to unencrypted in-memory DB if SQLCipher fails to initialize
                    Room.inMemoryDatabaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java
                    ).build()
                }
                INSTANCE = instance
                instance
            }
        }
    }
}
