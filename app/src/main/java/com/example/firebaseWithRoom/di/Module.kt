package com.example.firebaseWithRoom.di

import android.content.Context
import androidx.room.Room
import com.example.firebaseWithRoom.database.QuoteDao
import com.example.firebaseWithRoom.database.QuoteDatabase
import com.example.firebaseWithRoom.repo.QuoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun provideQuoteDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, QuoteDatabase::class.java, "Quote-db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideQuoteDao(quoteDatabase: QuoteDatabase) = quoteDatabase.quoteDao()

    @Provides
    @Singleton
    fun provideQuoteRepository(quoteDao: QuoteDao) = QuoteRepository(quoteDao)
}