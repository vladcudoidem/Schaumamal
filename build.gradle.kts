import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("dev.hydraulic.conveyor") version "1.10"
}

group = "com.vladvamos.schaumamal"
version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality

    // implementation(compose.desktop.currentOs)
    linuxAmd64(compose.desktop.linux_x64)
    macAmd64(compose.desktop.macos_x64)
    macAarch64(compose.desktop.macos_arm64)
    windowsAmd64(compose.desktop.windows_x64)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Schaumamal"
            packageVersion = "1.33.7" // This version is not relevant.

            macOS {
                iconFile.set(project.file("src/main/resources/icons/icon.icns"))
            }

            windows {
                iconFile.set(project.file("src/main/resources/icons/icon.ico"))
            }

            linux {
                iconFile.set(project.file("src/main/resources/icons/icon.png"))
            }
        }
    }
}

// region Work around temporary Compose bugs.
configurations.all {
    attributes {
        // https://github.com/JetBrains/compose-jb/issues/1404#issuecomment-1146894731
        attribute(Attribute.of("ui", String::class.java), "awt")
    }
}
// endregion
