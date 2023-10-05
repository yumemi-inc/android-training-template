package jp.co.yumemi.api.model

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal class DateAdapter : JsonAdapter<Date>() {

    private val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.JAPAN)

    @Synchronized
    override fun fromJson(reader: JsonReader): Date {
        val string = reader.nextString()
        return requireNotNull(format.parse(string))
    }

    @Synchronized
    override fun toJson(writer: JsonWriter, value: Date?) {
        requireNotNull(value)
        writer.value(format.format(value))
    }
}
