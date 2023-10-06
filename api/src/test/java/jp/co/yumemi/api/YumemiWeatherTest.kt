package jp.co.yumemi.api

import android.os.NetworkOnMainThreadException
import com.google.common.truth.Truth
import com.squareup.moshi.Moshi
import io.mockk.every
import io.mockk.mockk
import jp.co.yumemi.api.model.DateAdapter
import jp.co.yumemi.api.model.WeatherResponse
import kotlinx.coroutines.test.runTest
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
    private lateinit var yumemiWeather: YumemiWeather

    private val weathers = listOf("sunny", "cloudy", "rainy")

    private lateinit var moshi: Moshi

    private val testArea = "tokyo"
    private val testDate = "2022-12-01T12:00:00"
    private val testRequest = """
        {
            "area": "$testArea",
            "date": "$testDate"
        }
    """.trimIndent()

    @Before
    fun setup() {
        val context = RuntimeEnvironment.getApplication()
        yumemiWeather = YumemiWeather(context, random)
        moshi = Moshi.Builder()
            .add(Date::class.java, DateAdapter())
            .build()
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
        every { random.nextInt(any(), any()) } returns 0
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
        val value = yumemiWeather.fetchJsonWeather(testRequest)
        val responseAdapter = moshi.adapter(WeatherResponse::class.java)
        val response = responseAdapter.fromJson(value)!!
        Truth.assertThat(response.maxTemp).isEqualTo(0)
        Truth.assertThat(response.minTemp).isEqualTo(0)
        Truth.assertThat(response.weather).isEqualTo("sunny")
    }

    @Test(expected = NetworkOnMainThreadException::class)
    fun fetchJsonWeatherAsync() = runTest {
        yumemiWeather.fetchJsonWeatherAsync(testRequest)
    }
}
