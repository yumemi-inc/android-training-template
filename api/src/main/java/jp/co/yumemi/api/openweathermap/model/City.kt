package jp.co.yumemi.api.openweathermap.model

import com.squareup.moshi.JsonClass

/**
 * OpenWeatherMapで指定する都市
 *
 * ほとんどのAPIでは都市名・都市IDでも地点を指定することができます
 * URLクエリに以下の値を追加します
 * - 都市名(日本語未対応) `q=${city_name}`
 * - 都市ID `id=${city_id}`
 */
@JsonClass(generateAdapter = true)
internal data class City(
    val name: String,
    val id: Int,
    val country: String,
) {
    companion object {
        /**
         * OpenWeatherMapで有効な都市名・IDの一例
         *
         * その他の都市に関しては[こちらのTool](https://openweathermap.org/find)から検索できます
         */
        val values = listOf(
            City(name = "札幌", id = 2128295, country = "JP"),
            City(name = "釧路", id = 2129376, country = "JP"),
            City(name = "仙台", id = 2111149, country = "JP"),
            City(name = "新潟", id = 1855431, country = "JP"),
            City(name = "東京", id = 1850144, country = "JP"),
            City(name = "名古屋", id = 1856057, country = "JP"),
            City(name = "金沢", id = 1860243, country = "JP"),
            City(name = "大阪", id = 1853909, country = "JP"),
            City(name = "広島", id = 1862415, country = "JP"),
            City(name = "高知", id = 1859146, country = "JP"),
            City(name = "福岡", id = 1863967, country = "JP"),
            City(name = "鹿児島", id = 1860827, country = "JP"),
            City(name = "那覇", id = 1856035, country = "JP"),
            City(name = "New York", id = 5128581, country = "US"),
            City(name = "London", id = 2643743, country = "GB"),
        )
    }
}
