import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.plugin.serialization")
}

val localPropertiesFile = rootProject.file("local.properties")!!
val localProperties = Properties().apply {
    if (localPropertiesFile.exists()) {
        load(FileInputStream(localPropertiesFile))
    }
}

android {
    namespace = "in2000.gruppe8"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "in2000.gruppe8"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //Was used for local API keys
        buildConfigField(
            "String",
            "frostID",
            "\"${localProperties["frostID"] ?: ""}\""
        )

        buildConfigField(
            "String",
            "frostSecret",
            "\"${localProperties["frostSecret"] ?: ""}\""
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

val ktorVersion = "3.4.1"

dependencies {
    implementation(libs.androidx.compose.ui.text)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.datastore.preferences) // Dependency for datastore: persistent storage using key-value format

    implementation(libs.android.ndk27)  //dependecy for mapbox
    implementation(libs.maps.compose.ndk27) //dependency for jetpack and mapbox
    implementation(libs.androidx.navigation.compose)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.volley)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.auth)
    implementation(libs.androidx.navigation.runtime.ktx)

    implementation(platform(libs.charts.bom))
    implementation(libs.charts.line)
    implementation(libs.charts.pie)
    implementation(libs.charts.bar)
    implementation(libs.charts.stacked.bar)
    implementation(libs.charts.stacked.area)
    implementation(libs.charts.radar)
    implementation(libs.androidx.compose.foundation)

    //coil-library
    implementation(libs.coil.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.foundation)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.androidx.appcompat)
}