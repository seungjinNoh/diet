plugins {
    id("diet.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.diet.core.navigation"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)
}
