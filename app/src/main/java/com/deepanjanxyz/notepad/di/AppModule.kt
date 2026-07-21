package com.deepanjanxyz.notepad.di

import android.content.Context
import com.deepanjanxyz.notepad.core.database.AppDatabase
import com.deepanjanxyz.notepad.core.database.CategoryDao
import com.deepanjanxyz.notepad.core.database.NoteDao
import com.deepanjanxyz.notepad.core.security.EncryptedPreferences
import com.deepanjanxyz.notepad.core.security.SecurityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    @Provides
    @Singleton
    fun provideEncryptedPreferences(@ApplicationContext context: Context): EncryptedPreferences {
        return EncryptedPreferences(context)
    }

    @Provides
    @Singleton
    fun provideSecurityManager(encryptedPreferences: EncryptedPreferences): SecurityManager {
        return SecurityManager(encryptedPreferences)
    }
}
