import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.21"
}

group = "org.usefulness"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(20)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_16)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(16)
}

tasks {
    withType<Test>().configureEach {
        useJUnitPlatform()
    }
}

dependencies {
    implementation("com.squareup.okio:okio:3.3.0")
    implementation("com.github.ajalt.clikt:clikt:3.5.4")
    implementation(kotlin("test"))
}

tasks.named<Jar>("jar").configure {
    manifest {
        attributes["Main-Class"] = "org.usefulness.ftl.cli.FtlTestsResultsCliKt"
    }
    archiveClassifier.set("fat")

    from(sourceSets.main.get().output.classesDirs)
    configurations.named("runtimeClasspath").let { runtimeClasses ->
        dependsOn(runtimeClasses)
        from({ runtimeClasses.get().map { if (it.isDirectory) it else zipTree(it) } }) {
            exclude("META-INF/**/*")
        }
    }
    exclude("META-INF/**/*")
}

configurations.register("r8")
dependencies.add("r8", "com.android.tools:r8:8.0.40")
tasks.register<JavaExec>("r8jar") {
    javaLauncher.set(javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(19)) })

    dependsOn("jar")

    classpath(configurations.named("r8"))
    mainClass.set("com.android.tools.r8.R8")

    val jar = tasks.getByName<Jar>("jar")
    val outFile = buildDir.resolve("libs/${jar.archiveBaseName.get()}-${jar.archiveVersion.get()}-r8.jar")

    args(
        "--release",
        "--classfile",
        "--output", outFile.toString(),
        "--pg-conf", file("proguard-rules.pro"),
        "--lib", System.getProperty("java.home").toString(),
        jar.archiveFile.get().toString(),
    )
}

tasks.named("build").configure {
    dependsOn("r8jar")
}
