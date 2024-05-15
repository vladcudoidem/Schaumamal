package shared.graphics

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

data class HighlighterGraphics(
    override val offset: Offset,
    override val size: Size
): Graphics {

    companion object {
        fun from(
            imageOffset: Offset,
            imageSize: Size,
            screenshotFileSize: Size,
            selectedNodeGraphics: RawNodeGraphics
        ): HighlighterGraphics {
            val scalingFactor = imageSize.height / screenshotFileSize.height
                // it is irrelevant whether we use width or height when calculating the scaling factor

            return HighlighterGraphics(
                offset = imageOffset + selectedNodeGraphics.offset * scalingFactor,
                size = selectedNodeGraphics.size * scalingFactor
            )
        }
    }
}
