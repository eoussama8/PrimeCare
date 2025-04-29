
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    id ("org.jetbrains.kotlin.plugin.serialization") version("1.8.0") // Use the appropriate version of Kotlin
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false

}