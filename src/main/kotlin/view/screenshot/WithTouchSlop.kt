package view.screenshot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.unit.DpSize

@Composable
fun WithTouchSlop(
    touchSlop: Float,
    content: @Composable () -> Unit
) {
    // Create a ViewConfiguration with the new touchSlop.
    val originalViewConfiguration = LocalViewConfiguration.current
    val viewConfiguration = object : ViewConfiguration {
        override val doubleTapMinTimeMillis: Long
            get() = originalViewConfiguration.doubleTapMinTimeMillis

        override val doubleTapTimeoutMillis: Long
            get() = originalViewConfiguration.doubleTapTimeoutMillis

        override val longPressTimeoutMillis: Long
            get() = originalViewConfiguration.longPressTimeoutMillis

        override val touchSlop: Float
            get() = touchSlop

        override val minimumTouchTargetSize: DpSize
            get() = originalViewConfiguration.minimumTouchTargetSize
    }

    // Set the new ViewConfiguration for the composable content.
    CompositionLocalProvider(LocalViewConfiguration provides viewConfiguration) {
        content()
    }
}