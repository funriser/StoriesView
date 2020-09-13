package com.funrisestudio.stories

object Versions {

    const val kotlinVersion = "1.4.10"

    const val glideVersion = "4.11.0"

    object Plugins {
        const val androidGradle = "4.2.0-alpha10"
        const val kotlinGradle = kotlinVersion
        const val maven = "2.1"
    }
    object AndroidX {
        const val core = "1.3.1"
        const val appCompat = "1.2.0"
        const val material = "1.2.1"
    }
}

object Plugins {
    const val androidGradle = "com.android.tools.build:gradle:${Versions.Plugins.androidGradle}"
    const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Plugins.kotlinGradle}"
    const val maven = "com.github.dcendents:android-maven-gradle-plugin:${Versions.Plugins.maven}"
}

object Libs {
    const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"

    const val glide = "com.github.bumptech.glide:glide:${Versions.glideVersion}"
    const val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glideVersion}"

    object AndroidX {
        const val core = "androidx.core:core-ktx:${Versions.AndroidX.core}"
        const val appCompat = "androidx.appcompat:appcompat:${Versions.AndroidX.appCompat}"
        const val material = "com.google.android.material:material:${Versions.AndroidX.material}"
    }

}

object Modules {
    const val stories = ":stories"
}