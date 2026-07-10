plugins {
    alias(libs.plugins.dastyarwordpress.android.library)
    alias(libs.plugins.dastyarwordpress.android.hilt)
}

android {
    namespace = "ir.wordpressdashboard.core.common"
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.runtime)
    implementation(libs.kotlinx.coroutines.core)
}