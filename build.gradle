// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    ext.kotlin_version = "1.9.10"
    repositories {
        google()
        mavenCentral()
        maven {
            url "https://plugins.gradle.org/m2/"
        }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:7.4.2'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        classpath "org.jlleitschuh.gradle:ktlint-gradle:11.1.0"

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register('clean', Delete) {
    delete rootProject.buildDir
}

subprojects {
    // Gradle Plugin for ktlint
    apply plugin: "org.jlleitschuh.gradle.ktlint"

    ktlint {
        android = true
        ignoreFailures = true
        reporters {
            reporter "plain"
            reporter "checkstyle"
        }
        filter {
            include("src/**/*.kt")
            include("*.kts")
        }
    }
}
