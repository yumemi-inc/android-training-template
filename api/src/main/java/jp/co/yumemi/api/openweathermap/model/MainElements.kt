package jp.co.yumemi.api.openweathermap.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * [API docs](https://openweathermap.org/current)
 */
@JsonClass(generateAdapter = true)
internal data class MainElements(
    @Json(name = "temp")
    val temperature: Double,
    @Json(name = "feels_like")
    val sensibleTemperature: Double,
    @Json(name = "temp_min")
    val minTemperature: Double,
    @Json(name = "temp_max")
    val maxTemperature: Double,
    val pressure: Double,
    val humidity: Double
)
