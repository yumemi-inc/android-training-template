@file:Suppress("MemberVisibilityCanBePrivate")

package jp.co.yumemi.api

import android.content.Context
import android.os.NetworkOnMainThreadException
import com.squareup.moshi.Moshi
import jp.co.yumemi.api.model.DateAdapter
import jp.co.yumemi.api.model.ForecastPoint
import jp.co.yumemi.api.model.ForecastResponse
import jp.co.yumemi.api.model.Weather
import jp.co.yumemi.api.model.WeatherRequest
import jp.co.yumemi.api.model.WeatherResponse
import jp.co.yumemi.api.openweathermap.WeatherService
import jp.co.yumemi.api.openweathermap.model.City
import kotlinx.coroutines.delay
import java.util.Date
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

class YumemiWeather internal constructor(
    private val context: Context,
    private val random: Random,
    private val api: WeatherService,
) {

    constructor(context: Context) : this(
        context,
        Random.Default,
        WeatherService.create(),
    )

    private val moshi = Moshi.Builder()
        .add(Date::class.java, DateAdapter())
        .build()

    fun fetchSimpleWeather(): String = Weather.values().random(random).name.lowercase()

    fun fetchThrowsWeather(): String {
        throwsRandom()
        return fetchSimpleWeather()
    }

    private fun throwsRandom() {
        if ((0..4).random(random) == 4) {
            throw UnknownException()
        }
    }

    suspend fun fetchWeatherAsync(): String = waitAtLeast(1_500) {
        fetchSimpleWeather()
    }

    fun fetchJsonWeather(json: String): String {
        val requestAdapter = moshi.adapter(WeatherRequest::class.java)
        val request = requestAdapter.fromJson(json) ?: throw IllegalArgumentException()

        val responseAdapter = moshi.adapter(WeatherResponse::class.java)

        val maxTemp = (10..40).random(random)
        val minTemp = (-40..maxTemp).random(random)
        val response = WeatherResponse(
            weather = fetchThrowsWeather(),
            maxTemp = maxTemp,
            minTemp = minTemp,
            date = request.date,
            area = request.area,
        )
        return responseAdapter.toJson(response)
    }

    suspend fun fetchJsonWeatherAsync(json: String): String = waitAtLeast(1_500) {

        val requestAdapter = moshi.adapter(WeatherRequest::class.java)
        val request = requestAdapter.fromJson(json) ?: throw IllegalArgumentException()

        // OpenWeatherMapでは日本語表示の都市名では検索できない
        // あらかじめ用意した都市名だけを許容して都市IDでAPI呼び出す
        val city = City.values.find { it.name == request.area }
            ?: throw IllegalArgumentException("Invalid area requested: ${request.area}")
        val result = api.fetchCurrentWeather(city.id)
        val response = WeatherResponse(
            weather = result.weather.representAsWeather().name.lowercase(),
            maxTemp = result.main.maxTemperature.roundToInt(),
            minTemp = result.main.minTemperature.roundToInt(),
            date = result.date,
            // OpenWeatherMapでは言語をlang=jaに指定すると日本語で都市名が返ってくるが
            // 都道府県・市町村の接尾語がバラバラ・一部ローマ字など表記が揃っていないので無視
            area = request.area,
        )
        val responseAdapter = moshi.adapter(WeatherResponse::class.java)
        responseAdapter.toJson(response)
    }

    suspend fun fetchJsonForecastAsync(json: String): String = waitAtLeast(1_500) {

        val requestAdapter = moshi.adapter(WeatherRequest::class.java)
        val request = requestAdapter.fromJson(json) ?: throw IllegalArgumentException()

        val city = City.values.find { it.name == request.area }
            ?: throw IllegalArgumentException("Invalid area requested: ${request.area}")
        val result = api.fetchWeatherForecast(city.id)
        val forecast = ForecastResponse(
            area = request.area,
            list = result.list.map {
                ForecastPoint(
                    weather = it.weather.representAsWeather().name.lowercase(),
                    temperature = it.main.temperature.roundToInt(),
                    date = it.date,
                )
            }
        )

        val forecastAdapter = moshi.adapter(ForecastResponse::class.java)
        forecastAdapter.toJson(forecast)
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun <T> waitAtLeast(milliseconds: Long, computation: suspend () -> T): T {
        if (context.mainLooper.isCurrentThread) {
            throw NetworkOnMainThreadException()
        }
        val measure = measureTimedValue {
            runCatching {
                throwsRandom()
                computation()
            }
        }
        val duration = measure.duration.toLong(DurationUnit.MILLISECONDS)
        if (duration < milliseconds) {
            delay(milliseconds - duration)
        }
        return measure.value.getOrThrow()
    }
}

class UnknownException : Throwable()
