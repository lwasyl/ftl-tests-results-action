import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.21"
}

kotlin {
    jvmToolchain(23)
}

val targetJavaVersion = "17"
tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion))
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(targetJavaVersion.toInt())
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    implementation("com.squareup.okio:okio:3.12.0")
    implementation("com.github.ajalt.clikt:clikt:5.0.3")
    testImplementation(kotlin("test"))
}

tasks.named<Jar>("jar").configure {
    manifest {
        attributes["Main-Class"] = "io.github.lwasyl.ftl.cli.FtlTestsResultsCliKt"
    }
    archiveClassifier.set("fat")

    configurations.named("runtimeClasspath").let { runtimeClasses ->
        dependsOn(runtimeClasses)
        from({ runtimeClasses.get().map { if (it.isDirectory) it else zipTree(it) } }) {
            exclude("META-INF/**/*")
        }
    }
    exclude("META-INF/**/*")

    finalizedBy("r8jar")
}

configurations.register("r8")
dependencies.add("r8", "com.android.tools:r8:8.7.18")
tasks.register<JavaExec>("r8jar") {
    javaLauncher.set(javaToolchains.launcherFor { languageVersion.set(JavaLanguageVersion.of(23)) })

    dependsOn("jar")

    classpath(configurations.named("r8"))
    mainClass.set("com.android.tools.r8.R8")

    val jar = tasks.getByName<Jar>("jar")
    val outFile = layout.buildDirectory.asFile.get().resolve("libs/${jar.archiveBaseName.get()}-r8.jar")

    args(
        "--release",
        "--classfile",
        "--output", outFile.toString(),
        "--pg-conf", file("proguard-rules.pro"),
        "--lib", System.getProperty("java.home").toString(),
        jar.archiveFile.get().toString(),
    )
}
