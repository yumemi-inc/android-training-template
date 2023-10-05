package jp.co.yumemi.api

import android.content.Context
import android.os.NetworkOnMainThreadException
import com.google.common.truth.Truth
import com.squareup.moshi.Moshi
import io.mockk.every
import io.mockk.mockk
import jp.co.yumemi.api.model.DateAdapter
import jp.co.yumemi.api.model.WeatherResponse
import jp.co.yumemi.api.openweathermap.WeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.Date
import kotlin.random.Random

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class YumemiWeatherTest {

    private val random = mockk<Random>()
    private val server = MockWebServer()

    private lateinit var context: Context
    private lateinit var yumemiWeather: YumemiWeather

    private val weathers = listOf("sunny", "cloudy", "rainy")

    private lateinit var moshi: Moshi

    private fun testRequest(area: String = "東京") = """
        {
            "area": "$area",
            "date": "2022-12-01T12:00:00"
        }
    """.trimIndent()

    @Before
    fun setup() {
        server.start()
        context = RuntimeEnvironment.getApplication()
        val api = WeatherService.create(server.url("").toString(), "")
        yumemiWeather = YumemiWeather(context, random, api)
        moshi = Moshi.Builder()
            .add(Date::class.java, DateAdapter())
            .build()
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun fetchSimpleWeather() {
        weathers.forEachIndexed { index, expected ->
            every { random.nextInt(any()) } returns index
            val value = yumemiWeather.fetchSimpleWeather()
            Truth.assertThat(value).isEqualTo(expected)
        }
    }

    @Test
    fun fetchThrowsWeather() {
        // throwsRandomで例外を投げない
        every { random.nextInt(any(), any()) } returns 0
        // ランダムな天気で0番目を返す
        every { random.nextInt(any()) } returns 0
        val value = yumemiWeather.fetchThrowsWeather()
        Truth.assertThat(value).isEqualTo("sunny")
    }

    @Test(expected = UnknownException::class)
    fun fetchThrowsWeather_throwError() {
        every { random.nextInt(any(), any()) } returns 4
        yumemiWeather.fetchThrowsWeather()
    }

    @Test
    fun fetchJsonWeather() {
        every { random.nextInt(any(), any()) } returns 0
        every { random.nextInt(any()) } returns 0
        val value = yumemiWeather.fetchJsonWeather(testRequest())
        val responseAdapter = moshi.adapter(WeatherResponse::class.java)
        val response = responseAdapter.fromJson(value)!!
        Truth.assertThat(response.maxTemp).isEqualTo(0)
        Truth.assertThat(response.minTemp).isEqualTo(0)
        Truth.assertThat(response.weather).isEqualTo("sunny")
    }

    @Test(expected = NetworkOnMainThreadException::class)
    fun fetchJsonWeatherAsync_mainThreadError() = runTest {
        yumemiWeather.fetchJsonWeatherAsync(testRequest())
    }

    @Test(expected = IllegalArgumentException::class)
    fun fetchJsonWeatherAsync_InvalidAreaError() = runTest {
        every { random.nextInt(any(), any()) } returns 0
        withContext(Dispatchers.IO) {
            yumemiWeather.fetchJsonWeatherAsync(testRequest("tokyo"))
        }
    }

    @Test
    fun fetchJsonWeatherAsync() = runTest {
        every { random.nextInt(any(), any()) } returns 0

        val json = context.assets.open("current_weather_tokyo.json").use {
            it.readBytes().toString(Charsets.UTF_8)
        }
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(json)
        )

        val value = withContext(Dispatchers.IO) {
            yumemiWeather.fetchJsonWeatherAsync(testRequest())
        }
        val responseAdapter = moshi.adapter(WeatherResponse::class.java)
        val response = responseAdapter.fromJson(value)!!
        Truth.assertThat(response.weather).isEqualTo("cloudy")
        Truth.assertThat(response.area).isEqualTo("東京")
        Truth.assertThat(response.maxTemp).isEqualTo(12)
        Truth.assertThat(response.minTemp).isEqualTo(9)
    }
}
