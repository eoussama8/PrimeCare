plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)

}

android {
    namespace = "com.example.primecare"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.primecare"
        minSdk = 24
        targetSdk = 35
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

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.firestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("io.coil-kt:coil-compose:2.6.0")
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.3")

    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.8")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.8.9")


    // ExoPlayer
    implementation("androidx.media3:media3-exoplayer:1.3.1")
    implementation("androidx.media3:media3-ui:1.3.1")

    // Hilt
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")


    implementation("com.google.android.gms:play-services-auth:21.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")


    // Jetpack Compose
    implementation ("androidx.compose.ui:ui:1.4.0") // or the latest version you are using
    implementation ("androidx.compose.material3:material3:1.0.0") // or the latest version

    // LiveData and Compose integration
    implementation ("androidx.compose.runtime:runtime-livedata:1.4.0") // Make sure this is included

    // ViewModel and LiveData dependencies
    implementation ("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.0")

    implementation("com.google.accompanist:accompanist-swiperefresh:0.33.2-alpha")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation ("androidx.compose.material:material-icons-core:1.6.0")
    implementation ("androidx.compose.material:material-icons-extended:1.6.0")

    // For auto-mirrored icons (like back button that changes direction in RTL)
    implementation ("androidx.compose.material3:material3:1.2.0")

    implementation ("com.github.bumptech.glide:compose:1.0.0-beta01")
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.36.0")
}