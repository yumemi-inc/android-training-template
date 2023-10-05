package jp.co.yumemi.api.openweathermap.model

import com.squareup.moshi.JsonClass
import jp.co.yumemi.api.model.Weather

/**
 * [API docs](https://openweathermap.org/weather-conditions)
 */
@JsonClass(generateAdapter = true)
internal data class WeatherCondition(
    val id: Int,
    val main: String,
    val description: String,
) {
    fun representAsWeather() = when (id / 100) {
        2 -> Weather.Rainy
        3 -> Weather.Rainy
        5 -> Weather.Rainy
        6 -> Weather.Snow
        7 -> Weather.Cloudy
        8 -> when (id % 10) {
            in 0..1 -> Weather.Sunny
            in 2..4 -> Weather.Cloudy
            else -> throw IllegalArgumentException("unknown weather condition 80X id: $id")
        }
        else -> throw IllegalArgumentException("unknown weather condition id: $id")
    }
}
