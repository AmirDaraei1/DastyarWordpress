plugins {
    alias(libs.plugins.dastyarwordpress.android.feature)
    alias(libs.plugins.dastyarwordpress.android.library.compose)
}

android {
    namespace = "ir.wordpressdashboard.feature.login"

}

dependencies {

    implementation (libs.google.accompanist.pager)
    implementation (libs.google.accompanist.permissions)
    implementation("androidx.webkit:webkit:1.8.0")

    // CameraX and QR Code Scanning
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.google.mlkit.barcode.scanning)

}