package view.screenshot

import AppViewModel
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.unit.toSize
import model.InspectorState
import java.awt.Cursor
import java.io.File
import java.io.FileInputStream

@Composable
fun Screenshot(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    var screenshotFileSize by remember { mutableStateOf(Size.Zero) } // TODO use Offset.Unspecified?
    var imageSize by remember { mutableStateOf(Size.Zero) }
    var imageOffset by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = modifier) {
        if (viewModel.inspectorState == InspectorState.POPULATED) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    bitmap = loadImageBitmap(FileInputStream(File(viewModel.layoutData.screenshotPath))).apply {
                        screenshotFileSize = Size(height = height.toFloat(), width = width.toFloat())
                    },
                    contentDescription = null,
                    modifier = Modifier
                        .graphicsLayer(
                            translationX = imageOffset.x,
                            translationY = imageOffset.y
                        )
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, _, _ ->
                                imageOffset += pan
                            }
                        }
                        .onSizeChanged { imageSize = it.toSize() }
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { offset ->
                                    val scalingFactor = screenshotFileSize.height / imageSize.height
                                    val scaledOffset = offset * scalingFactor // TODO coerce this

                                    val flatNodeList = viewModel.layoutData.root.getNodesFlattened()
                                    flatNodeList.doWithNodeUnder(offset = scaledOffset) {
                                        viewModel.selectNode(node = it)
                                    }
                                }
                            )
                        }
                )
            }
        }

        if (viewModel.isNodeSelected) { // implies that inspectorState == POPULATED
            val (offset, size) = HighlighterGraphics.from(
                imageOffset = imageOffset,
                imageSize = imageSize,
                screenshotFileSize = screenshotFileSize,
                selectedNodeGraphics = NodeGraphics.from(viewModel.selectedNode)
            )

            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        color = Color.Red,
                        topLeft = offset,
                        size = size,
                        style = Stroke(width = 5f)
                    )
                }
            }
        }
    }
}