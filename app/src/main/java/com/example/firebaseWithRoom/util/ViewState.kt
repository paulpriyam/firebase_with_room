package com.example.firebaseWithRoom.util

sealed class ViewState {
    data class ERROR(val msg: String) : ViewState()
    object SUCCESS : ViewState()
    object CANCEL : ViewState()
}
