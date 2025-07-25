package shared

import androidx.compose.ui.graphics.Color

@Suppress("unused")
object Colors {

    /* Generic */

    val primaryTextColor = Color(0xFFB4B7C2)
    val discreteTextColor = Color(0xFF71727B)

    val primaryElementColor = primaryTextColor
    val disabledPrimaryElementColor = Color(0xFF53555B)
    val accentColor = Color(0xFF705575)
    val vibrantAccentColor = Color(0xFF753b99)

    val backgroundColor = Color(0xFF121317)
    val elevatedBackgroundColor = Color(0xFF1E2024)

    /* Button Layer */

    val extractionButtonColor = Color(0xFFBA1A1A)
    val extractionProgressBarColor = Color(0xFF006622)

    /* Pane Layer */

    val wedgeColor = Color(0xBBC3C6D2)

    val scrollbarHoverColor = Color.White.copy(alpha = 0.50f)
    val scrollbarUnhoverColor = Color.White.copy(alpha = 0.12f)

    val paneBorderColor = scrollbarUnhoverColor

    /* Screenshot Layer */

    val highlighterColor = Color.Red
}
