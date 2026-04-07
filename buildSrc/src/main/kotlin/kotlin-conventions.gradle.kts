plugins {
    `java-library`
    kotlin("jvm")
}

extensions.configure<JavaPluginExtension> {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

kotlin {
    jvmToolchain(21)
}

tasks {
    withType<Javadoc>().configureEach {
        options.encoding = Charsets.UTF_8.name()
    }
    withType<JavaCompile>().configureEach {
        options.release = 21
    }
}
