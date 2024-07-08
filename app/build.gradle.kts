plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.ksp.plugin)
    alias(libs.plugins.hilt.plugin)
    alias(libs.plugins.safeargs.plugin)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.mehdisekoba.potea"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mehdisekoba.potea"
        minSdk = 24
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
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }

}

dependencies {
    // android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.places)
    implementation(libs.androidx.core.animation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Navigation
    implementation(libs.navigation.ui)
    implementation(libs.navigation.fragment)
    // Dagger - Hilt
    implementation(libs.hilt)
    ksp(libs.hiltcompiler)
    // DataStore
    implementation(libs.androidx.datastore.preferences)
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    // OkHTTP client
    implementation(libs.okhttp)
    implementation(libs.interceptor)
    // Coroutines
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)
    // Lifecycle
    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.livedata)
    implementation(libs.lifecycle.viewmodel)
    // Image Loading
    implementation(libs.coil)
    // Gson
    implementation(libs.google.gson)
    // Calligraphy
    implementation(libs.calligraphy)
    implementation(libs.viewpump)
    // shimmer
    implementation(libs.androidveil)

    // lottie
    implementation(libs.lottie)
    // chart
    implementation(libs.aachartcore.kotlin)

    // pin view
    implementation(libs.pinview)
    // image picker
    implementation(libs.ssimagepicker)
    // indicator
    implementation(libs.dotsindicator)
    // size
    implementation(libs.dynamicsizes)
    // permissions
    implementation(libs.permissionx)
    // chucker
    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.no.op)
    // other
    implementation(libs.ticketview)
    implementation(libs.readmore.textview)
    implementation(libs.kenburnsview)
    implementation (libs.format.watcher)
    implementation (libs.multisearchview)

}
