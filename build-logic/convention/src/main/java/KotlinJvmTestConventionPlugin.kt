import ir.wordpressdashboard.build_logic.convention.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.withType

class KotlinJvmTestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
       with(target) {
           pluginManager.apply("org.jetbrains.kotlin.jvm")
           dependencies {
               testImplementation(kotlin("test"))
               testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
           }
           tasks.withType<Test>().configureEach {
               useJUnitPlatform()
           }
       }
    }
}