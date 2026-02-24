plugins {
    id("diet.android.library")
    id("diet.android.hilt")
}

android {
    namespace = "com.example.diet.core.data"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:domain"))
    implementation(project(":core:database"))

    implementation(libs.kotlinx.coroutines.core)
}
