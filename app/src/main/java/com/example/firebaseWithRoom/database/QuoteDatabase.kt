package com.example.firebaseWithRoom.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.firebaseWithRoom.model.Quote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

@Database(entities = [Quote::class], version = 3)
abstract class QuoteDatabase : RoomDatabase() {

    abstract fun quoteDao(): QuoteDao
}