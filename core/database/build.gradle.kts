plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)

    alias(libs.plugins.runmate.android.library)
}

android {
    namespace = "com.example.core.database"
}

dependencies {

    implementation(libs.org.mongodb.bson)

    implementation(projects.core.domain)
}