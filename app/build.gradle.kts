import ir.wordpressdashboard.build_logic.convention.implementation

plugins {
    alias(libs.plugins.dastyarwordpress.android.application)
    alias(libs.plugins.dastyarwordpress.android.application.compose)
    alias(libs.plugins.dastyarwordpress.android.hilt)
}

android {
    namespace = "ir.wordpressdashboard"

    defaultConfig {
        applicationId = "ir.wordpressdashboard"
        versionCode = 1
        versionName = "0.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
        }
    }

}

dependencies {
    implementation(projects.feature.login)
    implementation(projects.feature.home)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.navigation.compose)
}
