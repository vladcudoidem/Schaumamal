package shared

import androidx.compose.ui.graphics.Color

@Suppress("unused")
object Colors {

    /* Generic */

    val primaryTextColor = Color(0xFFE2E2E8)
    val secondaryTextColor = Color(0xFFB4B7C2)
    val hintTextColor = Color(0x88FFFFFF)
    val highlightedBackgroundColor = Color(0xFF705575)

    /* Button Layer */

    val buttonColor = Color(0xFFBA1A1A)

    /* Pane Layer */

    val wedgeColor = Color(0xBBC3C6D2)
    val floatingPaneBackgroundColor = Color(0xFF1E2024)

    /* Other */

    val backgroundColor = Color(0xFF121317)
    val scrollbarHoverColor = Color.White.copy(alpha = 0.50f)
    val scrollbarUnhoverColor = Color.White.copy(alpha = 0.12f)
}