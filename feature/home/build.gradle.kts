plugins {
    alias(libs.plugins.dastyarwordpress.android.feature)
    alias(libs.plugins.dastyarwordpress.android.library.compose)
    alias(libs.plugins.dastyarwordpress.android.hilt)
    alias(libs.plugins.dastyarwordpress.network.dependencies)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "ir.wordpressdashboard.feature.home"
}
dependencies {
    implementation(project(":core:network"))
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation(libs.coil.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.google.accompanist.permissions)
    implementation(libs.google.accompanist.swiperefresh)
}

