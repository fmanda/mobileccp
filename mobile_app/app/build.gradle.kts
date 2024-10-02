plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp") version "2.0.0-1.0.22"
    kotlin("plugin.serialization") version "2.0.10"
}

android {
    namespace = "com.ts.mobileccp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ts.mobileccp"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.circleimageview)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    implementation(libs.material)
    implementation(libs.play.services.location)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)



}