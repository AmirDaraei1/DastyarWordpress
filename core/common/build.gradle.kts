plugins {
    alias(libs.plugins.dastyarwordpress.android.library)
    alias(libs.plugins.dastyarwordpress.android.hilt)
}

android {
    namespace = "ir.wordpressdashboard.core.common"
}

dependencies {

    implementation(libs.kotlinx.coroutines.core)


}