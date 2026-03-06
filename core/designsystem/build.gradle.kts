plugins {
    id("diet.android.library")
    id("diet.android.library.compose")
}

android {
    namespace = "com.example.diet.core.designsystem"
}

dependencies {
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.ui)
    api(libs.androidx.material3)
}
