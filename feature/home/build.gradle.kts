plugins {
    alias(libs.plugins.dastyarwordpress.android.feature)
    alias(libs.plugins.dastyarwordpress.android.library.compose)
    alias(libs.plugins.dastyarwordpress.network.dependencies)}

android {
    namespace = "ir.wordpressdashboard.feature.home"
}

dependencies {
    implementation(project(":core:network"))
}