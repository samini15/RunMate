plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)

    alias(libs.plugins.runmate.android.library)
    alias(libs.plugins.runmate.android.room)
}

android {
    namespace = "com.example.core.database"
}

dependencies {

    implementation(libs.org.mongodb.bson)

    implementation(libs.bundles.koin)

    implementation(projects.core.domain)
}