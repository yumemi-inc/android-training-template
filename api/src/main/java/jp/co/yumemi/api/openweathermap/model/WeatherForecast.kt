package jp.co.yumemi.api.openweathermap.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
internal data class WeatherForecast(
    val list: List<ForecastItem>,
    val city: City
)

@JsonClass(generateAdapter = true)
internal data class ForecastItem(
    @Json(name = "weather")
    val weatherList: List<WeatherCondition>,
    val main: MainElements,
    @Json(name = "dt")
    val date: Date,
) {
    val weather: WeatherCondition
        get() = weatherList[0]
}
