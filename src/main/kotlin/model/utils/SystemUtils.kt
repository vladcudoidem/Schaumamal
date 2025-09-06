package model.utils

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

fun copyToClipboard(text: String): Boolean {
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    val success =
        try {
            clipboard.setContents(StringSelection(text), null)
            true
        } catch (_: IllegalStateException) {
            false
        }

    return success
}
