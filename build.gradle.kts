plugins {
    kotlin("multiplatform") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
}

kotlin {
    js {
        nodejs()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation("com.squareup.okio:okio:3.9.1")
            implementation("com.github.ajalt.clikt:clikt:5.0.0")
            implementation("io.github.pdvrieze.xmlutil:core:0.90.1")
            implementation("io.github.pdvrieze.xmlutil:serialization:0.90.1")
        }
        jsMain {
            dependencies {
                implementation("com.squareup.okio:okio-nodefilesystem:3.9.1")
            }
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
