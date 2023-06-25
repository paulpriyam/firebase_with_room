package com.example.firebaseWithRoom.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
            .addMigrations(MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .build()

    private val MIGRATION_1_2: Migration = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add the isSynced column to the original table
            database.execSQL("ALTER TABLE quote_table ADD COLUMN isSynced INTEGER")

            // Create a temporary table with the updated structure
//            database.execSQL("CREATE TABLE quote_table_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL, description TEXT NOT NULL, createdAt INTEGER NOT NULL, modifiedAt INTEGER NOT NULL, isSynced INTEGER  DEFAULT 0)")
//
//            // Copy the data from the old table to the temporary table
//            database.execSQL("INSERT INTO quote_table_new (id, title, description, createdAt, modifiedAt, isSynced) SELECT id, title, description, createdAt, modifiedAt, isSynced FROM quote_table")
//
//            // Drop the old table
//            database.execSQL("DROP TABLE quote_table")
//
//            // Rename the temporary table to the original table name
//            database.execSQL("ALTER TABLE quote_table_new RENAME TO quote_table")
        }
    }

    @Provides
    @Singleton
    fun provideQuoteDao(quoteDatabase: QuoteDatabase) = quoteDatabase.quoteDao()

    @Provides
    @Singleton
    fun provideQuoteRepository(quoteDao: QuoteDao) = QuoteRepository(quoteDao)
}