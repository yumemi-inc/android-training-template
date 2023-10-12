@file:Suppress("UnstableApiUsage")

import java.io.FileInputStream
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.9.10-1.0.13"
}

// このAPI実装ではOpenWeatherMapを利用します
// 1 こちらのサイトからAPI keyを取得します https://home.openweathermap.org/api_keys
// 2 ./apikey.properties にAPI keyを記載します
//     api_key=${your_api_key}
val apiKey: String = project.file("apikey.properties").let {
    if (it.exists()) {
        val input = FileInputStream(it)
        val props = Properties()
        props.load(input)
        props.getProperty("api_key", "")
    } else ""
}

android {
    namespace = "jp.co.yumemi.api"
    compileSdk = 33

    defaultConfig {
        minSdk = 27

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "API_KEY", "\"$apiKey\"")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // Moshi
    implementation("com.squareup.moshi:moshi:1.13.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.13.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // Test
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.1.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.robolectric:robolectric:4.10.3")
    testImplementation("io.mockk:mockk:1.13.7")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")
}
