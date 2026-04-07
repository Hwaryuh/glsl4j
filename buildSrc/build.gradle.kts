plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(
        libs.plugins.kotlin.jvm
            .map { "org.jetbrains.kotlin:kotlin-gradle-plugin:${it.version}" },
    )
}
