plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")
    //id("com.google.gms.google-services")
}

android {
    namespace = "com.example.foodiefling"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.foodiefling"
        minSdk = 26
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
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.material.v1120)
    implementation(libs.javamail)
    implementation(libs.activation)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.gsonConverter)
    implementation(libs.roundedimageview)
    implementation(platform(libs.firebase.bom)) // Firebase BOM to manage versions
    implementation(libs.firebase.analytics)    // Firebase Analytics
    implementation(libs.firebase.auth)         // Firebase Authentication
    implementation(libs.firebase.storage)  // Firebase Storage
    implementation(libs.poi)
    implementation(libs.poi.ooxml)
    implementation(libs.commons.collections4)
    implementation(libs.viewpager2)
    implementation(libs.glide)
    implementation(libs.okhttp)
}