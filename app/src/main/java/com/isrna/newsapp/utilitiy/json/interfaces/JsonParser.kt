package com.isrna.newsapp.utilitiy.json.interfaces

import java.lang.reflect.Type

/**
 * Interface used for Json Parsing in Retrofit
 */
interface JsonParser {
    fun <T> toJson(data: T, type: Type): String?
    fun <T> fromJson(jsonData: String, type: Type): T?
}