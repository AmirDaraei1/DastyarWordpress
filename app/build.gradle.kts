import ir.wordpressdashboard.build_logic.convention.implementation
import java.util.Properties

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
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            // مقادیر را از keystore.properties بخوانید (آن فایل را به .gitignore اضافه کنید)
            val keystoreFile = rootProject.file("keystore.properties")
            if (keystoreFile.exists()) {
                val props = Properties()
                keystoreFile.inputStream().use { props.load(it) }
                storeFile = file(props.getProperty("storeFile"))
                storePassword = props.getProperty("storePassword")
                keyAlias = props.getProperty("keyAlias")
                keyPassword = props.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

}

dependencies {
    implementation(projects.feature.login)
    implementation(projects.feature.home)
    implementation(projects.core.common)
    implementation(projects.core.network)
    implementation(projects.core.domain)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation(libs.coil.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.androidx.navigation.compose)
}
