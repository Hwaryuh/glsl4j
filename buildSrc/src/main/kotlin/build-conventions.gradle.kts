plugins {
    `java-library`
}

extensions.configure<JavaPluginExtension> {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks {
    withType<Javadoc>().configureEach {
        options.encoding = Charsets.UTF_8.name()
    }
    withType<JavaCompile>().configureEach {
        options.release = 21
    }
}
