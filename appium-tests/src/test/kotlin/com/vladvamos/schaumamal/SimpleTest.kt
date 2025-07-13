package com.vladvamos.schaumamal

import io.appium.java_client.AppiumBy
import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.remote.options.BaseOptions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.URL

class SimpleTest {
    private lateinit var driver: AppiumDriver

    @BeforeEach
    fun setUp() {
        val options =
            BaseOptions().setPlatformName("mac").setAutomationName("mac2").apply {
                setCapability("appium:appPath", "/Users/vladvamos/Developer/Schaumamal/build/compose/binaries/main/app/Schaumamal.app")
            }
        driver = AndroidDriver(URL("http://127.0.0.1:4723"), options)
        Thread.sleep(3000) // Todo: do differently
    }

    @Test
    fun testHistoryButton() {
        val historyButton = driver.findElement(AppiumBy.accessibilityId("history_button"))
        historyButton.click()

        val currentTime = System.currentTimeMillis()
        while (System.currentTimeMillis() < currentTime + 2000) {
            try {
                val floatingWindow = driver.findElement(AppiumBy.accessibilityId("floating_window"))
                assert(floatingWindow.isDisplayed)
                break
            } catch (_: Exception) {
                println("Exception!")
            }
        }
    }

    @Test
    fun testDisplayButton() {
        val increaseDisplayCountButton = driver.findElement(AppiumBy.accessibilityId("increase_display_count_button"))
        val decreaseDisplayCountButton = driver.findElement(AppiumBy.accessibilityId("decrease_display_count"))
        increaseDisplayCountButton.click()

        Thread.sleep(1000)

        val displayCountBox = driver.findElement(AppiumBy.accessibilityId("display_counter_text"))
        assert(displayCountBox.findElements(AppiumBy.accessibilityId("2/2")).size == 1)

        decreaseDisplayCountButton.click()

        assert(displayCountBox.findElements(AppiumBy.accessibilityId("1/2")).size == 1)
    }

    @AfterEach
    fun tearDown() {
        driver.quit()
    }
}
