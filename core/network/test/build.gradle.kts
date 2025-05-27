import ir.wordpressdashboard.build_logic.convention.libs
import ir.wordpressdashboard.build_logic.convention.testImplementation

plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.dastyarwordpress.kotlin.jvm.test)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}

//dependencies {
//    testImplementation(libs.mockwebserver)
//    testImplementation(libs.junit)
//}
