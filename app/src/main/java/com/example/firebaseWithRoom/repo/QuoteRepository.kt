package com.example.firebaseWithRoom.repo

import com.example.firebaseWithRoom.database.QuoteDao
import com.example.firebaseWithRoom.model.Quote
import javax.inject.Inject

class QuoteRepository @Inject constructor(private val quoteDao: QuoteDao) {

    suspend fun insertQuote(quote: Quote) = quoteDao.insertQuote(quote)

    suspend fun deleteAllQuotes() = quoteDao.deleteAllQuotes()

    fun getAllQuotes() = quoteDao.getAllQuotes()
}