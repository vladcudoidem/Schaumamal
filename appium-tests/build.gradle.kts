plugins {
    kotlin("jvm")
    alias(libs.plugins.spotless)
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(libs.appium.java)
}

tasks.test {
    onlyIf { project.findProperty("runAppium") == "true" }
    useJUnitPlatform()
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
}
