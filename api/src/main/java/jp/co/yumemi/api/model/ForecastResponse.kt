package jp.co.yumemi.api.model

import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
internal data class ForecastResponse(
    val area: String,
    val list: List<ForecastPoint>
)

@JsonClass(generateAdapter = true)
internal data class ForecastPoint(
    val weather: String,
    val temperature: Int,
    val date: Date,
)
