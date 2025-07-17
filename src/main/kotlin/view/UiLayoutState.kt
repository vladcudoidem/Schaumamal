package view

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import kotlin.math.min
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
import view.utils.toDp
import view.utils.toPx

class UiLayoutState(private val screenshotComposableSize: StateFlow<Size>) {

    // panesHeightConstraint and panesWidthConstraint are, under the current implementation, also
    // the height and width
    // of the window content area (i.e. the total area, excluding the window bar). These values do
    // not need to be states
    // as they are only used as limits in the wedge drag event handlers.
    private var panesHeightConstraint = Dp.Unspecified
    private var panesWidthConstraint = Dp.Unspecified

    private val _screenshotLayerOffset = MutableStateFlow(Offset.Zero)
    val screenshotLayerOffset
        get() = _screenshotLayerOffset.asStateFlow()

    private val _screenshotLayerScale = MutableStateFlow(1f)
    val screenshotLayerScale
        get() = _screenshotLayerScale.asStateFlow()

    private val _paneWidth = MutableStateFlow(initialPaneWidth)
    val paneWidth
        get() = _paneWidth.asStateFlow()

    private val _upperPaneHeight = MutableStateFlow(initialUpperPaneHeight)
    val upperPaneHeight
        get() = _upperPaneHeight.asStateFlow()

    fun onPanesHeightConstraintChanged(newHeightConstraint: Dp) {
        // First update the height constraint.
        panesHeightConstraint = newHeightConstraint

        _upperPaneHeight.update {
            val minUpperPaneHeight = minimumPaneDimension
            val maxUpperPaneHeight =
                max(minUpperPaneHeight, panesHeightConstraint - minimumPaneDimension)
            it.coerceIn(minUpperPaneHeight, maxUpperPaneHeight)
        }
    }

    fun onPanesWidthConstraintChanged(newWidthConstraint: Dp) {
        // First update the width constraint.
        panesWidthConstraint = newWidthConstraint

        _paneWidth.update {
            val minPaneWidth = minimumPaneDimension
            val maxPaneWidth = max(minPaneWidth, panesWidthConstraint - minimumPaneDimension)
            it.coerceIn(minPaneWidth, maxPaneWidth)
        }
    }

    fun onVerticalWedgeDrag(change: PointerInputChange, dragAmount: Offset, density: Float) {
        if (change.positionChange() != Offset.Zero) change.consume()

        val dragAmountDp = dragAmount.x.toDp(density)
        _paneWidth.update {
            val minPaneWidth = minimumPaneDimension
            val maxPaneWidth = max(minPaneWidth, panesWidthConstraint - minimumPaneDimension)
            (it - dragAmountDp).coerceIn(minPaneWidth, maxPaneWidth)
        }
    }

    fun onHorizontalWedgeDrag(change: PointerInputChange, dragAmount: Offset, density: Float) {
        if (change.positionChange() != Offset.Zero) change.consume()

        val dragAmountDp = dragAmount.y.toDp(density)
        _upperPaneHeight.update {
            val minUpperPaneHeight = minimumPaneDimension
            val maxUpperPaneHeight =
                max(minUpperPaneHeight, panesHeightConstraint - minimumPaneDimension)
            (it + dragAmountDp).coerceIn(minUpperPaneHeight, maxUpperPaneHeight)
        }
    }

    fun onImageGesture(centroid: Offset, pan: Offset, zoom: Float, rotation: Float) {
        _screenshotLayerOffset.value += pan * _screenshotLayerScale.value
    }

    fun onImageScroll(event: PointerEvent) {
        val oldScale = _screenshotLayerScale.value
        val change = event.changes.first()
        val zoomFactor =
            1f - change.scrollDelta.y / scrollZoomFactor // We are using an arbitrary factor.

        // First change scale.
        val newScale = _screenshotLayerScale.value * zoomFactor

        // Don't change scale if it will be outside limits.
        if (newScale !in minScreenshotScale..maxScreenshotScale) {
            return
        } else {
            _screenshotLayerScale.value = newScale
        }

        // Change offset. Enables zooming around the pointer location.
        val screenshotComposableSize = screenshotComposableSize
        val pointerOffsetFromCenter =
            change.position -
                Offset(
                    x = screenshotComposableSize.value.width,
                    y = screenshotComposableSize.value.height,
                ) / 2f
        _screenshotLayerOffset.value -= pointerOffsetFromCenter * oldScale * (zoomFactor - 1f)
    }

    // This is a method that is highly dependent on the specific arrangement of the UI components on
    // the screen. It will
    // likely break when the UI undergoes significant change.
    fun onFitScreenshotToScreenButtonPressed(density: Float) {
        val screenshotComposableSize = screenshotComposableSize

        // The following variables define the screenshot area (the area between the buttons and the
        // panes).
        val topVisibleScreenshotAreaBound = mediumPadding + extractButtonDiameter
        val bottomVisibleScreenshotAreaBound = panesHeightConstraint
        val startVisibleScreenshotAreaBound = mediumPadding + extractButtonDiameter
        val endVisibleScreenshotAreaBound =
            panesWidthConstraint -
                (wedgeSmallDimension + (smallPadding + 3.dp) + _paneWidth.value + mediumPadding)

        // We first place the screenshot in the center of the screenshot area.

        // The reference of this offset is the upper-left corner (x = 0, y = 0).
        val screenshotComposableGlobalOffsetAtCenter =
            Offset(
                x = largePadding.toPx(density) + screenshotComposableSize.value.width / 2,
                y = panesHeightConstraint.toPx(density) / 2,
            )

        // The reference of this offset is the upper-left corner (x = 0, y = 0) as well.
        val targetGlobalOffsetAtCenter =
            Offset(
                x =
                    (startVisibleScreenshotAreaBound + endVisibleScreenshotAreaBound).toPx(
                        density
                    ) / 2,
                y =
                    (topVisibleScreenshotAreaBound + bottomVisibleScreenshotAreaBound).toPx(
                        density
                    ) / 2,
            )

        _screenshotLayerOffset.value =
            targetGlobalOffsetAtCenter - screenshotComposableGlobalOffsetAtCenter

        // Then we resize the screenshot to take up as much space as possible (the actual
        // "fit-to-screen").

        val targetHorizontalScale =
            (endVisibleScreenshotAreaBound - startVisibleScreenshotAreaBound)
                .toPx(density)
                .div(screenshotComposableSize.value.width)
        val targetVerticalScale =
            (bottomVisibleScreenshotAreaBound - topVisibleScreenshotAreaBound)
                .toPx(density)
                .div(screenshotComposableSize.value.height)

        // We use this factor to leave some space between the screenshot and the other UI elements.
        val convenienceFactor = 0.9f

        // We then use the most restrictive scale.
        _screenshotLayerScale.value =
            min(targetHorizontalScale, targetVerticalScale)
                .times(convenienceFactor)
                .coerceIn(minimumValue = minScreenshotScale, maximumValue = maxScreenshotScale)
    }

    fun onEnlargeScreenshotButtonPressed() {
        _screenshotLayerScale.value =
            (_screenshotLayerScale.value * keyboardZoomFactor).coerceAtMost(maxScreenshotScale)
    }

    fun onShrinkScreenshotButtonPressed() {
        _screenshotLayerScale.value =
            (_screenshotLayerScale.value / keyboardZoomFactor).coerceAtLeast(minScreenshotScale)
    }
}
