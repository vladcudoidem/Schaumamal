package view

import AppViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import view.Dimensions.largeCornerRadius
import view.Dimensions.mediumPadding
import view.Dimensions.paddingBetweenItems
import view.Dimensions.smallCornerRadius
import java.awt.Cursor

private val initialTopLevelBoxWidth = 250.dp
private val initialUpperBoxHeight = 300.dp
private val minimumFloatingPaneDimension = 100.dp

private val wedgeSmallDimension = 6.dp
private val wedgeLargeDimension = 30.dp

@Composable
fun FloatingPanes(modifier: Modifier = Modifier) {
    var topLevelBoxWidth by remember { mutableStateOf(initialTopLevelBoxWidth) }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(topLevelBoxWidth)
            .padding(mediumPadding)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(paddingBetweenItems)

        ) {
            Wedge(
                width = wedgeSmallDimension, height = wedgeLargeDimension,
                pointerHoverIcon = PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)),
                pointerInputHandler = {
                    detectDragGestures { change, dragAmount ->
                        if (change.positionChange() != Offset.Zero) change.consume()

                        topLevelBoxWidth = (topLevelBoxWidth - dragAmount.x.dp / 2).coerceAtLeast(
                            minimumFloatingPaneDimension
                        )
                    }
                }
            )

            TwoBoxColumn()
        }
    }
}

@Composable
fun TwoBoxColumn(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current
    val density = LocalDensity.current.density

    var upperBoxHeight by remember { mutableStateOf(initialUpperBoxHeight) }
    // The lower Box takes up as much height as is left. This value gets updated with the real height at composition.
    var lowerBoxHeight by remember { mutableStateOf(0.dp) }

    Column(
        verticalArrangement = Arrangement.spacedBy(paddingBetweenItems),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(upperBoxHeight)
                .clip(RoundedCornerShape(
                    topStart = largeCornerRadius,
                    topEnd = largeCornerRadius,
                    bottomStart = smallCornerRadius,
                    bottomEnd = smallCornerRadius
                ))
                .background(Colors.floatingPaneBackgroundColor)
        ) {
            if (viewModel.isInspectorPopulated) {
                Tree(modifier = Modifier.fillMaxSize())
            }
        }

        Wedge(
            width = wedgeLargeDimension, height = wedgeSmallDimension,
            pointerHoverIcon = PointerIcon(Cursor(Cursor.S_RESIZE_CURSOR)),
            pointerInputHandler = {
                detectDragGestures { change, dragAmount ->
                    if (change.positionChange() != Offset.Zero) change.consume()

                    if (lowerBoxHeight >= minimumFloatingPaneDimension || dragAmount.y.toDp() < 0.dp) {
                        upperBoxHeight = (upperBoxHeight + dragAmount.y.toDp()).coerceAtLeast(
                            minimumFloatingPaneDimension
                        )
                    }
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(
                    topStart = smallCornerRadius,
                    topEnd = smallCornerRadius,
                    bottomStart = largeCornerRadius,
                    bottomEnd = largeCornerRadius
                ))
                .background(Colors.floatingPaneBackgroundColor)
                .padding(mediumPadding)
                .onSizeChanged {
                    lowerBoxHeight = (it.height / density).dp
                }
        ) {
            if (viewModel.isNodeSelected) {
                SelectedNode(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun Wedge(
    width: Dp,
    height: Dp,
    pointerHoverIcon: PointerIcon,
    pointerInputHandler: suspend PointerInputScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(min(width, height) / 2))
            .background(Colors.wedgeColor)
            .pointerHoverIcon(pointerHoverIcon)
            .pointerInput(Unit, pointerInputHandler)
    )
}