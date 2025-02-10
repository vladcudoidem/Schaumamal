import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "2.1.0"
}

group = "com.vladvamos.schaumamal"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://packages.jetbrains.team/maven/p/kpm/public/")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material")
    }

    implementation(libs.kotlinx.coroutinesCore)
    implementation(libs.kotlinx.coroutinesSwing)
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.kotlinx.serializationJson)
    implementation(libs.android.adblib)
    implementation(libs.jewel.standalone)
    implementation(libs.jewel.decoratedWindow)
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(17)
        vendor = JvmVendorSpec.JETBRAINS
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        buildTypes.release.proguard {
            version = "7.6.1"
            obfuscate = false
            optimize = true
            configurationFiles.from(project.file("rules.pro"))
        }

        nativeDistributions {
            targetFormats(
                TargetFormat.Deb,
                TargetFormat.Rpm,
                TargetFormat.Dmg,
                // TargetFormat.Pkg, // Including this throws an error (might be a bug).
                TargetFormat.Exe,
                TargetFormat.Msi,
                // TargetFormat.AppImage // Does not work due to a bug in CMP Gradle plugin.
            )

            packageName = "Schaumamal"
            description = "The second coming of UiAutomatorViewer."
            copyright = "Copyright (c) 2024 Alexandru-Vlad Vamoș"
            licenseFile.set(project.file("LICENSE"))

            modules("java.instrument", "jdk.unsupported")

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

composeCompiler {
    stabilityConfigurationFiles = listOf(layout.projectDirectory.file("compose-stab.config"))
    reportsDestination = layout.buildDirectory.dir("compose-compiler")
}

tasks {
    withType<JavaExec> {
        // afterEvaluate is needed because the Compose Gradle Plugin
        // register the task in the afterEvaluate block
        afterEvaluate {
            javaLauncher = project.javaToolchains.launcherFor { languageVersion = JavaLanguageVersion.of(17) }
            setExecutable(javaLauncher.map { it.executablePath.asFile.absolutePath }.get())
        }
    }
}
