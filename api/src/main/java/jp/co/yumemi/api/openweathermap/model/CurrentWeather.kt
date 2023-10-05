package jp.co.yumemi.api.openweathermap.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
internal data class CurrentWeather(
    @Json(name = "weather") val weatherList: List<WeatherCondition>,
    val main: MainElements,
    @Json(name = "id") val cityId: Int,
    @Json(name = "name") val cityName: String,
    @Json(name = "dt") val date: Date,
) {
    val weather: WeatherCondition
        get() = weatherList[0]
}
