package jp.co.yumemi.api.openweathermap.model

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.util.Date

internal class UnixTimestampAdapter : JsonAdapter<Date>() {
    override fun fromJson(reader: JsonReader): Date {
        val timestamp = reader.nextLong()
        return Date(timestamp * 1000L)
    }

    override fun toJson(writer: JsonWriter, value: Date?) {
        requireNotNull(value)
        writer.value(value.time / 1000L)
    }
}
