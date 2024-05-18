plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)

    alias(libs.plugins.runmate.android.library)
}

android {
    namespace = "com.example.run.network"
}

dependencies {

    implementation(projects.core.domain)
    implementation(projects.core.data)
}