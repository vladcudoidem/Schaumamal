package view.panels

import AppViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import model.parser.DisplayNode
import model.parser.Node
import model.parser.SystemNode
import model.parser.WindowNode
import view.Colors
import view.Dimensions
import view.Dimensions.mediumPadding
import view.Dimensions.smallPadding
import view.UpperBoxItemPositions
import view.UpperBoxVerticalScrollState
import java.awt.Cursor
import kotlin.math.roundToInt

// TODO do this with LazyColumn. Is that possible?
// TODO this file has to be refactored sometime.

val startPaddingPerLevel = 25.dp

@Composable
fun Tree(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current
    val rootNode = viewModel.layoutData.root

    val verticalScrollState = UpperBoxVerticalScrollState.current
    val horizontalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(verticalScrollState)
            .horizontalScroll(horizontalScrollState)
    ) {
        Spacer(modifier = Modifier.height(mediumPadding))
        SystemPrinter(rootNode)
        Spacer(modifier = Modifier.height(mediumPadding))
    }
}

@Composable
fun SystemPrinter(system: SystemNode, modifier: Modifier = Modifier) {
    var enabled by remember { mutableStateOf(true) }
        // TODO use these variables at some point...

    val text = with(system) {
        "System {displays=${displays.size}}"
    }

    TreeLine(text = text, depth = 0, modifier = modifier)
    if (enabled) {
        for (display in system.displays) {
            DisplayPrinter(display)
        }
    }
}

@Composable
fun DisplayPrinter(display: DisplayNode, modifier: Modifier = Modifier) {
    var enabled by remember { mutableStateOf(true) }

    val text = with(display) {
        "Display {id=$id, windows=${windows.size}}"
    }

    TreeLine(text = text, depth = 1, modifier = modifier)
    if (enabled) {
        for (window in display.windows) {
            WindowPrinter(window)
        }
    }
}

@Composable
fun WindowPrinter(window: WindowNode, modifier: Modifier = Modifier) {
    var enabled by remember { mutableStateOf(true) }

    val text = with(window) {
        "($index) Window {title=\"$title\"} $bounds"
    }

    TreeLine(text = text, depth = 2, modifier = modifier)
    if (enabled) {
        for (node in window.nodes) {
            NodePrinter(node, 3)
        }
    }
}

@Composable
fun NodePrinter(node: Node, depth: Int, modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current
    val itemPositions = UpperBoxItemPositions.current
    var enabled by remember { mutableStateOf(true) }

    val text = with(node) {
        val formattedClassName = className.split(".").last()
        val formattedResourceId = resourceId.split(":").last().let {
            // Add a delimiter if node has a resource-id
            if (it.isEmpty()) it
            else "$it "
        }
        val formattedText = text.take(10) + if (text.length > 10) "..." else ""

        "($index) $formattedClassName $formattedResourceId" +
            "{text=\"$formattedText\", contDesc=\"$contentDesc\"} $bounds"
    }

    TreeLine(
        text = text,
        textBackgroundColor = if (viewModel.selectedNode === node) {
            Colors.highlightedTextBackgroundColor
        } else {
            Color.Transparent
        },
        depth = depth,
        onClickText = { viewModel.selectNode(node) },
        modifier = modifier.onGloballyPositioned { layoutCoordinates ->
            // Capture the position of each text
            itemPositions[node] = layoutCoordinates.positionInParent().y.roundToInt() - 300
                // TODO remove the magic number 300
        }
    )
    if (enabled) {
        for (child in node.children) {
            NodePrinter(child, depth + 1)
        }
    }
}

@Composable
fun TreeLine(
    text: String,
    textBackgroundColor: Color = Color.Transparent,
    modifier: Modifier = Modifier,
    depth: Int = 0,
    onClickText: () -> Unit = { }
) {
    if (depth < 0) error("Depth cannot be lower than 0.")

    Row(modifier = modifier) {
        Spacer(modifier = Modifier.width(mediumPadding))

        Spacer(modifier = Modifier.width(startPaddingPerLevel * depth))
        Text(
            text = text,
            color = Colors.secondaryTextColor,
            modifier = Modifier
                .clip(RoundedCornerShape(Dimensions.smallCornerRadius))
                .background(textBackgroundColor)
                .clickable { onClickText() }
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
                .padding(smallPadding)
        )

        Spacer(modifier = Modifier.width(mediumPadding))
    }
}