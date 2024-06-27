package shared

import androidx.compose.ui.graphics.Color

@Suppress("unused")
object Colors {

    /* Generic */

    val primaryTextColor = Color(0xFFB4B7C2)
    val discreteTextColor = primaryTextColor.copy(alpha = 0.55f)
    val accentColor = Color(0xFF705575)

    val backgroundColor = Color(0xFF121317)
    val elevatedBackgroundColor = Color(0xFF1E2024)

    /* Button Layer */

    val extractionButtonColor = Color(0xFFBA1A1A)

    /* Pane Layer */

    val wedgeColor = Color(0xBBC3C6D2)

    val scrollbarHoverColor = Color.White.copy(alpha = 0.50f)
    val scrollbarUnhoverColor = Color.White.copy(alpha = 0.12f)

    /* Screenshot Layer */

    val highlighterColor = Color.Red
}