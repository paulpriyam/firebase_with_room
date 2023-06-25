package com.example.firebaseWithRoom.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.firebaseWithRoom.model.Quote

@Dao
interface QuoteDao {

    @Insert
    suspend fun insertQuote(quote: Quote)

    @Query("DELETE FROM quote_table")
    suspend fun deleteAllQuotes()

    @Query("SELECT * FROM quote_table")
    fun getAllQuotes(): List<Quote>
}