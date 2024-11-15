package view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import shared.Dimensions.Initial.initialPaneWidth
import shared.Dimensions.Initial.initialUpperPaneHeight
import shared.Dimensions.extractButtonDiameter
import shared.Dimensions.largePadding
import shared.Dimensions.mediumPadding
import shared.Dimensions.minimumPaneDimension
import shared.Dimensions.smallPadding
import shared.Dimensions.wedgeSmallDimension
import shared.Values.keyboardZoomFactor
import shared.Values.maxScreenshotScale
import shared.Values.minScreenshotScale
import shared.Values.scrollZoomFactor
import viewmodel.toDp
import viewmodel.toPx
import kotlin.math.min

class UiLayoutState(private val getScreenshotComposableSize: () -> Size) {

    // panesHeightConstraint and panesWidthConstraint are, under the current implementation, also the height and width
    // of the window content area (i.e. the total area, excluding the window bar). These values do not need to be states
    // as they are only used as limits in the wedge drag event handlers.
    private var panesHeightConstraint = Dp.Unspecified
    private var panesWidthConstraint = Dp.Unspecified

    var screenshotLayerOffset by mutableStateOf(Offset.Zero)
        private set
    var screenshotLayerScale by mutableStateOf(1f)
        private set

    var paneWidth by mutableStateOf(initialPaneWidth)
        private set
    var upperPaneHeight by mutableStateOf(initialUpperPaneHeight)
        private set

    fun onPanesHeightConstraintChanged(newHeightConstraint: Dp) {
        // First update the height constraint.
        panesHeightConstraint = newHeightConstraint

        upperPaneHeight = upperPaneHeight.coerceIn(
            minimumValue = minimumPaneDimension,
            maximumValue = panesHeightConstraint - minimumPaneDimension
        )
    }

    fun onPanesWidthConstraintChanged(newWidthConstraint: Dp) {
        // First update the width constraint.
        panesWidthConstraint = newWidthConstraint

        paneWidth = paneWidth.coerceIn(
            minimumValue = minimumPaneDimension,
            maximumValue = panesWidthConstraint - minimumPaneDimension
        )
    }

    fun onVerticalWedgeDrag(change: PointerInputChange, dragAmount: Offset, density: Float) {
        if (change.positionChange() != Offset.Zero) change.consume()

        val dragAmountDp = dragAmount.x.toDp(density)
        paneWidth = (paneWidth - dragAmountDp).coerceIn(
            minimumValue = minimumPaneDimension,
            maximumValue = panesWidthConstraint - minimumPaneDimension
        )
    }

    fun onHorizontalWedgeDrag(change: PointerInputChange, dragAmount: Offset, density: Float) {
        if (change.positionChange() != Offset.Zero) change.consume()

        val dragAmountDp = dragAmount.y.toDp(density)
        upperPaneHeight = (upperPaneHeight + dragAmountDp).coerceIn(
            minimumValue = minimumPaneDimension,
            maximumValue = panesHeightConstraint - minimumPaneDimension
        )
    }

    fun onImageGesture(centroid: Offset, pan: Offset, zoom: Float, rotation: Float) {
        screenshotLayerOffset += pan * screenshotLayerScale
    }

    fun onImageScroll(event: PointerEvent) {
        val oldScale = screenshotLayerScale
        val change = event.changes.first()
        val zoomFactor = 1f - change.scrollDelta.y / scrollZoomFactor // We are using an arbitrary factor.

        // First change scale.
        val newScale = screenshotLayerScale * zoomFactor

        // Don't change scale if it will be outside limits.
        if (newScale !in minScreenshotScale..maxScreenshotScale) {
            return
        } else {
            screenshotLayerScale = newScale
        }

        // Change offset. Enables zooming around the pointer location.
        val screenshotComposableSize = getScreenshotComposableSize()
        val pointerOffsetFromCenter =
            change.position - Offset(x = screenshotComposableSize.width, y = screenshotComposableSize.height) / 2f
        screenshotLayerOffset -= pointerOffsetFromCenter * oldScale * (zoomFactor - 1f)
    }

    // This is a method that is highly dependent on the specific arrangement of the UI components on the screen. It will
    // likely break when the UI undergoes significant change.
    fun onFitScreenshotToScreenButtonPressed(density: Float) {
        val screenshotComposableSize = getScreenshotComposableSize()

        // The following variables define the screenshot area (the area between the buttons and the panes).
        val topVisibleScreenshotAreaBound = mediumPadding + extractButtonDiameter
        val bottomVisibleScreenshotAreaBound = panesHeightConstraint
        val startVisibleScreenshotAreaBound = mediumPadding + extractButtonDiameter
        val endVisibleScreenshotAreaBound =
            panesWidthConstraint - (wedgeSmallDimension + smallPadding + paneWidth + mediumPadding)

        // We first place the screenshot in the center of the screenshot area.

        // The reference of this offset is the upper-left corner (x = 0, y = 0).
        val screenshotComposableGlobalOffsetAtCenter = Offset(
            x = largePadding.toPx(density) + screenshotComposableSize.width / 2,
            y = panesHeightConstraint.toPx(density) / 2
        )

        // The reference of this offset is the upper-left corner (x = 0, y = 0) as well.
        val targetGlobalOffsetAtCenter = Offset(
            x = (startVisibleScreenshotAreaBound + endVisibleScreenshotAreaBound).toPx(density) / 2,
            y = (topVisibleScreenshotAreaBound + bottomVisibleScreenshotAreaBound).toPx(density) / 2
        )

        screenshotLayerOffset = targetGlobalOffsetAtCenter - screenshotComposableGlobalOffsetAtCenter

        // Then we resize the screenshot to take up as much space as possible (the actual "fit-to-screen").

        val targetHorizontalScale =
            (endVisibleScreenshotAreaBound - startVisibleScreenshotAreaBound)
                .toPx(density)
                .div(screenshotComposableSize.width)
        val targetVerticalScale =
            (bottomVisibleScreenshotAreaBound - topVisibleScreenshotAreaBound)
                .toPx(density)
                .div(screenshotComposableSize.height)

        // We use this factor to leave some space between the screenshot and the other UI elements.
        val convenienceFactor = 0.9f

        // We then use the most restrictive scale.
        screenshotLayerScale =
            min(targetHorizontalScale, targetVerticalScale)
                .times(convenienceFactor)
                .coerceIn(
                    minimumValue = minScreenshotScale,
                    maximumValue = maxScreenshotScale
                )
    }

    fun onEnlargeScreenshotButtonPressed() {
        screenshotLayerScale = (screenshotLayerScale * keyboardZoomFactor).coerceAtMost(maxScreenshotScale)
    }

    fun onShrinkScreenshotButtonPressed() {
        screenshotLayerScale = (screenshotLayerScale / keyboardZoomFactor).coerceAtLeast(minScreenshotScale)
    }

}