package view.panes

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import org.koin.compose.koinInject
import shared.Colors.wedgeColor
import shared.Dimensions.smallPadding
import shared.Dimensions.wedgeLargeDimension
import shared.Dimensions.wedgeSmallDimension
import viewmodel.AppViewModel
import java.awt.Cursor

@Composable
fun HorizontalWedge(
    viewModel: AppViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .pointerHoverIcon(PointerIcon(Cursor(Cursor.S_RESIZE_CURSOR)))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    viewModel.onHorizontalWedgeDrag(change, dragAmount)
                }
            }
            .padding(smallPadding)
    ) {
        Column(
            modifier = Modifier
                .width(wedgeLargeDimension)
                .height(wedgeSmallDimension)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(wedgeColor)
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(wedgeColor)
            )
        }
    }
}

@Composable
fun VerticalWedge(
    viewModel: AppViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    viewModel.onVerticalWedgeDrag(change, dragAmount)
                }
            }
            .padding(smallPadding)
    ) {
        Row(
            modifier = Modifier
                .width(wedgeSmallDimension)
                .height(wedgeLargeDimension)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(wedgeColor)
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(wedgeColor)
            )
        }
    }
}
