package com.isrna.newsapp.core.data.local.database.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["keyword"], unique = true)])
data class SearchKeywordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val keyword: String
    ) {
    override fun toString(): String {
        return keyword
    }
}
