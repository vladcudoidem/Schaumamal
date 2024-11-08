import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("dev.hydraulic.conveyor") version "1.10"
}

group = "com.vladvamos.schaumamal"
version = "1.0.0"

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
    implementation(compose.desktop.currentOs)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
    implementation("io.insert-koin:koin-core:4.0.0")
    implementation("io.insert-koin:koin-compose:4.0.0")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(
                TargetFormat.Deb,
                TargetFormat.Rpm,
                TargetFormat.Dmg,
                // TargetFormat.Pkg including this throws an error (might be a bug)
                TargetFormat.Exe,
                TargetFormat.Msi,
                // TargetFormat.AppImage does not work due to a bug in CMP Gradle plugin
            )
            packageName = "Schaumamal"
            description = "The second coming of UiAutomatorViewer."
            copyright = "Copyright (c) 2024 Alexandru-Vlad Vamoș"
            licenseFile.set(project.file("LICENSE"))

            macOS {
                iconFile.set(project.file("src/main/resources/appIcons/icon.icns"))
            }

            windows {
                iconFile.set(project.file("src/main/resources/appIcons/icon.ico"))
                menu = true
                upgradeUuid = "de0bbc5f-6290-4967-aab2-94707a706f92"
            }

            linux {
                iconFile.set(project.file("src/main/resources/appIcons/icon.png"))
                menuGroup = "Development"
            }
        }
    }
}
