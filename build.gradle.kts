import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "2.1.0"
    alias(libs.plugins.spotless)
}

group = "com.vladvamos.schaumamal"

val releaseVersion = "1.1.0"

version = releaseVersion.substringBefore("-")

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
    implementation(compose.desktop.currentOs)

    implementation(libs.kotlinx.coroutinesCore)
    implementation(libs.kotlinx.coroutinesSwing)
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.kotlinx.serializationJson)
    implementation(libs.android.adblib)
    implementation(libs.jewel.standalone)
    implementation(libs.jewel.decoratedWindow)
    implementation(libs.z4kn4fein.semver)
    implementation(libs.ktor.clientCore)
    implementation(libs.ktor.clientCio)
    implementation(libs.ktor.clienContenNegotiation)
    implementation(libs.ktor.serializatioKotlinxJson)
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(17)
        vendor = JvmVendorSpec.JETBRAINS
    }
}

sourceSets {
    named("main") { java.srcDir(layout.buildDirectory.dir("generated/source/buildConfig")) }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        buildTypes.release.proguard {
            obfuscate = false
            optimize = false
            isEnabled = false
            configurationFiles.from(project.file("rules.pro"))
        }

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

            modules("java.instrument", "jdk.unsupported")

            macOS { iconFile.set(project.file("src/main/resources/appIcons/icon.icns")) }

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

spotless {
    isEnforceCheck = false

    kotlin {
        ktfmt().kotlinlangStyle().configure {
            it.apply {
                setMaxWidth(100)
                setContinuationIndent(4)
                setBlockIndent(4)
            }
        }
        targetExclude("build/**/BuildConfig.kt")
    }

    kotlinGradle { ktfmt().kotlinlangStyle() }

    yaml {
        target(".github/**/*.yml", ".github/**/*.yaml")
        prettier()
    }
}

val generateBuildConfig by
    tasks.registering {
        val outputDir = layout.buildDirectory.dir("generated/source/buildConfig")

        inputs.property("version", project.version.toString())
        outputs.dir(outputDir)

        doLast {
            val file = outputDir.get().file("BuildConfig.kt").asFile
            file.parentFile.mkdirs()
            file.writeText(
                """
            |object BuildConfig {
            |    const val VERSION = "$releaseVersion"
            |}
            """
                    .trimMargin()
            )
        }
    }

tasks.named("compileKotlin") { dependsOn(generateBuildConfig) }
