package jp.co.yumemi.api.model

import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
internal data class WeatherRequest(
    val area: String,
    val date: Date
)
