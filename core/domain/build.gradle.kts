plugins {
    //id("java-library")
    alias(libs.plugins.runmate.jvm.library)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
}