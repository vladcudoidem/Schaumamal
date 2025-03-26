package view.panes

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.awt.Cursor
import kotlin.properties.Delegates
import shared.Colors.wedgeColor
import shared.Dimensions.smallPadding
import shared.Dimensions.wedgeLargeDimension
import shared.Dimensions.wedgeSmallDimension

@Composable
fun Wedge(
    orientation: WedgeOrientation,
    onDrag: (PointerInputChange, Offset, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var width by Delegates.notNull<Dp>()
    var height by Delegates.notNull<Dp>()
    var pointerIcon by Delegates.notNull<PointerIcon>()
    when (orientation) {
        WedgeOrientation.VERTICAL -> {
            width = wedgeSmallDimension
            height = wedgeLargeDimension
            pointerIcon = PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR))
        }

        WedgeOrientation.HORIZONTAL -> {
            width = wedgeLargeDimension
            height = wedgeSmallDimension
            pointerIcon = PointerIcon(Cursor(Cursor.S_RESIZE_CURSOR))
        }
    }

    val density = LocalDensity.current.density

    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier // Todo: how does the order of modifiers work (here)?
                .run {
                    when (orientation) {
                        WedgeOrientation.VERTICAL -> fillMaxHeight()
                        WedgeOrientation.HORIZONTAL -> fillMaxWidth()
                    }
                }
                .pointerHoverIcon(pointerIcon)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount -> onDrag(change, dragAmount, density) }
                }
                .padding(smallPadding + 3.dp),
    ) {
        Box(
            modifier =
                Modifier.width(width)
                    .height(height)
                    .clip(RoundedCornerShape(50))
                    .background(wedgeColor)
        )
    }
}
