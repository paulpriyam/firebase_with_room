package com.example.firebaseWithRoom.util

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {

    fun observe(): Flow<NetworkState>

    enum class NetworkState {
        AVAILABLE, UNAVAILABLE,LOST,LOSING
    }
}