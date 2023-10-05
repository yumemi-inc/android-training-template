package jp.co.yumemi.api.openweathermap

import com.squareup.moshi.Moshi
import jp.co.yumemi.api.BuildConfig
import jp.co.yumemi.api.openweathermap.model.CurrentWeather
import jp.co.yumemi.api.openweathermap.model.UnixTimestampAdapter
import jp.co.yumemi.api.openweathermap.model.WeatherForecast
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Date

internal interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun fetchCurrentWeather(@Query("id") cityId: Int): CurrentWeather

    @GET("data/2.5/forecast")
    suspend fun fetchWeatherForecast(@Query("id") cityId: Int): WeatherForecast

    companion object {
        fun create(
            baseUrl: String = "https://api.openweathermap.org/",
            apiKey: String = BuildConfig.API_KEY,
        ): WeatherService {
            val client = OkHttpClient.Builder()
                .addNetworkInterceptor(QueryInterceptor(apiKey))
                .build()
            val moshi = Moshi.Builder()
                .add(Date::class.java, UnixTimestampAdapter())
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
            return retrofit.create(WeatherService::class.java)
        }
    }
}

