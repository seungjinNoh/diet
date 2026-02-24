plugins {
    id("diet.android.library")
}

android {
    namespace = "com.example.diet.core.domain"
}

dependencies {
    implementation(project(":core:model"))
    implementation(libs.kotlinx.coroutines.core)
}
