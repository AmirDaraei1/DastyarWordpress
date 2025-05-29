import ir.wordpressdashboard.build_logic.convention.implementation
import ir.wordpressdashboard.build_logic.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class NetworkDependenciesConvention : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("kotlinx-serialization")
            }
            dependencies {
                implementation(libs.findLibrary("retrofit").get())
                implementation(libs.findLibrary("retrofit2-kotlinx-serialization-converter").get())
                implementation(libs.findLibrary("kotlinx-serialization-json").get())
            }
        }
    }
}
