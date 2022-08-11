package com.isrna.newsapp.core.data.local.database

import androidx.room.ProvidedTypeConverter
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.isrna.newsapp.core.data.local.Article
import com.isrna.newsapp.utilitiy.json.interfaces.JsonParser

/**
 * JsonConverter for use in [NewsAppDatabase]:[RoomDatabase]
 * @param jsonParser [JsonParser]
 */
@ProvidedTypeConverter
class JsonConverters(
    private val jsonParser: JsonParser
    ) {

    @TypeConverter
    fun fromArticlesJson(json: String): List<Article> {
        return jsonParser.fromJson<ArrayList<Article>>(
            json,
            object : TypeToken<ArrayList<Article>>(){}.type
        ) ?: emptyList()
    }

    @TypeConverter
    fun toArticlesJson(articles: List<Article>): String {
        return jsonParser.toJson(
            articles,
            object : TypeToken<ArrayList<Article>>(){}.type
        ) ?: "[]"
    }
}