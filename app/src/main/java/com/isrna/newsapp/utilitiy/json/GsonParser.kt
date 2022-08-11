package com.isrna.newsapp.utilitiy.json

import com.google.gson.Gson
import com.isrna.newsapp.utilitiy.json.interfaces.JsonParser
import java.lang.reflect.Type

/**
 * Implementation of Gson for parsing Json
 * @param gson Gson Instance
 */
class GsonParser(
    private val gson: Gson
) : JsonParser {
    override fun <T> toJson(data: T, type: Type): String? = gson.toJson(data, type)
    override fun <T> fromJson(jsonData: String, type: Type): T? = gson.fromJson(jsonData, type)
}