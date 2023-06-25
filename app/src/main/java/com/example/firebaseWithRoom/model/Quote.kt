package com.example.firebaseWithRoom.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quote_table")
data class Quote(
    var title: String = "",
    var description: String = "",
    var createdAt: Long = 0L,
    var modifiedAt: Long = 0L
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
