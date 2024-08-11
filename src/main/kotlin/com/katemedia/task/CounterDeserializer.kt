package com.katemedia.task

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.serialization.*
import io.ktor.util.reflect.*
import io.ktor.utils.io.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.json.*


class CounterDeserializer(private val json: Json) : ContentConverter {
    override suspend fun deserialize(
        charset: Charset,
        typeInfo: TypeInfo,
        content: ByteReadChannel
    ): Any? {
        val text = content.readRemaining().readText(charset);
        val jsonElement = json.parseToJsonElement(text)
        if (typeInfo.type == Dictionary::class) {
            if (jsonElement is JsonObject) {
                if (jsonElement.keys.size > 1) {
                    throw RuntimeException("Cannot be parsed")
                }
                var dictionary: Dictionary? = null;

                for ((key, value) in jsonElement) {
                    val counter = value.jsonPrimitive.intOrNull ?: 0
                    var name = key;
                    dictionary = Dictionary(name, counter)
                }
                return dictionary;
            }
        }
        throw RuntimeException("Cannot be parsed")
    }

    override suspend fun serializeNullable(
        contentType: ContentType,
        charset: Charset,
        typeInfo: TypeInfo,
        value: Any?
    ): OutgoingContent? {
        if (value is Dictionary && typeInfo.type == Dictionary::class) {
            val jsonString = json.encodeToString(Dictionary.serializer(), value)
            return TextContent(jsonString, contentType.withCharset(charset))
        }
        return null
    }
}