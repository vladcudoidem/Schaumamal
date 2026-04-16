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
    val activeElementColor = Color(0xFF4275c7)

    val backgroundColor = Color(0xFF121317)
    val elevatedBackgroundColor = Color(0xFF1E2024)

    /* Button Layer */

    val extractionButtonColor = Color(0xFFBA1A1A)
    val extractionProgressBarColor = Color(0xFF006622)

    /* Pane Layer */

    val resizingHandleColor = Color(0x79C3C6D2)

    val scrollbarHoverColor = Color.White.copy(alpha = 0.50f)
    val scrollbarUnhoverColor = Color.White.copy(alpha = 0.12f)

    val paneBorderColor = scrollbarUnhoverColor

    /* Screenshot Layer */

    val highlighterColor = Color.Red

    /* Notifications */

    val infoIconColor = Color(0xFF3997FE)
    val warningIconColor = Color(0xFFFFE74B)
    val errorIconColor = Color(0xFFFF3A42)

    val notificationActionTextColor = Color(0xFF4C82FE)
}
