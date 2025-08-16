plugins {
    alias(libs.plugins.dastyarwordpress.android.feature)
    alias(libs.plugins.dastyarwordpress.android.library.compose)
}

android {
    namespace = "ir.wordpressdashboard.feature.login"

}

dependencies {

    implementation (libs.google.accompanist.pager)
    implementation("androidx.webkit:webkit:1.8.0")

}