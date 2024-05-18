plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)

    alias(libs.plugins.runmate.android.library)
}

android {
    namespace = "com.example.run.data"
}

dependencies {

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.google.android.gms.play.services.location)
    implementation(libs.androidx.work)
    implementation(libs.koin.android.workmanager)
    implementation(libs.kotlinx.serialization.json)


    implementation(projects.run.domain)
    implementation(projects.core.domain)
    implementation(projects.core.database)
}