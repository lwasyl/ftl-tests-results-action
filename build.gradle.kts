import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "1.8.21"
}

group = "org.usefulness"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

dependencies {
    implementation("com.squareup.okio:okio:3.3.0")
    implementation(kotlin("test"))
}
