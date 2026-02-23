plugins {
    id("diet.android.library")
    id("diet.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.diet.core.database"
}

dependencies {
    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Core Model
    implementation(project(":core:model"))
}
