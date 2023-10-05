package jp.co.yumemi.api.openweathermap

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 全ての通信に共通のQueryを追加する
 *
 * - `appid`: API key
 * - `lang`: 言語 `ja`固定
 * - `units`: 単位 `metric`固定（℃・m/s単位）
 *
 * [API Docs](https://openweathermap.org/current)
 */
internal class QueryInterceptor(
    private val apiKey: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url()
            .newBuilder()
            .addQueryParameter("appid", apiKey)
            .addQueryParameter("lang", "ja")
            .addQueryParameter("units", "metric")
            .build()
        val newRequest = request.newBuilder()
            .url(url)
            .build()
        return chain.proceed(newRequest)
    }
}
