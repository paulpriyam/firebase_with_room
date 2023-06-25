package com.example.firebaseWithRoom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebaseWithRoom.model.Quote
import com.example.firebaseWithRoom.repo.QuoteRepository
import com.example.firebaseWithRoom.util.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class QuoteViewmodel @Inject constructor(private val quoteRepository: QuoteRepository) :
    ViewModel() {

    private var _quotes = MutableLiveData<List<Quote>>().apply {
        value = arrayListOf()
    }
    val quotes: LiveData<List<Quote>> get() = _quotes

    private var _quotesToSync = MutableLiveData<List<Quote>>().apply {
        value = arrayListOf()
    }
    val quotesToSync: LiveData<List<Quote>> get() = _quotesToSync

    private var _quoteInserted = MutableLiveData<Boolean>().apply {
        value = false
    }
    val quoteInserted: LiveData<Boolean> get() = _quoteInserted

    var _quotesSyncedState = MutableLiveData<ViewState>().apply {
        value = ViewState.CANCEL
    }

    val quoteSyncedState: LiveData<ViewState> get() = _quotesSyncedState
    fun getAllQuotes() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = quoteRepository.getAllQuotes()
            withContext(Dispatchers.Main) {
                _quotes.postValue(response)
            }
        }
    }

    fun getAllQuotesToSync() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val response = quoteRepository.getAllQuotesToSync()
                withContext(Dispatchers.Main) {
                    _quotesToSync.postValue(response)
                }
            }
        } catch (e: Exception) {
            _quotesToSync.postValue(arrayListOf())
        }
    }

    fun deleteAllQuotes() {
        viewModelScope.launch(Dispatchers.IO) {
            quoteRepository.deleteAllQuotes()
        }
    }

    fun insertQuote(quote: Quote) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                quoteRepository.insertQuote(quote)
                withContext(Dispatchers.Main) {
                    _quoteInserted.postValue(true)
                }
            }
        } catch (e: Exception) {
            _quoteInserted.postValue(false)
        }
    }

    fun updateSyncFlag() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                quoteRepository.updateSyncFlagToOne()
                _quotesSyncedState.postValue(ViewState.SUCCESS)
            }
        } catch (e: Exception) {
            _quotesSyncedState.postValue(ViewState.ERROR(e.message.toString()))
        }
    }
}