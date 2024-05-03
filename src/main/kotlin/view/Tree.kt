package view

import AppViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.parser.DisplayNode
import model.parser.Node
import model.parser.SystemNode
import model.parser.WindowNode
import view.Dimensions.mediumPadding
import view.Dimensions.smallPadding

// TODO do this with LazyColumn. Is that possible?
// TODO this file has to be refactored sometime.

val startPaddingPerLevel = 20.dp

@Composable
fun Tree(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current
    val rootNode = viewModel.layoutData.root

    val verticalScrollState = rememberScrollState()
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

    TreeLine(text = "System", depth = 0)
    if (enabled) {
        Column(modifier = modifier) {
            for (display in system.displays) {
                DisplayPrinter(display)
            }
        }
    }
}

@Composable
fun DisplayPrinter(display: DisplayNode, modifier: Modifier = Modifier) {
    var enabled by remember { mutableStateOf(true) }

    TreeLine(text = "Display", depth = 1)
    if (enabled) {
        Column(modifier = modifier) {
            for (window in display.windows) {
                WindowPrinter(window)
            }
        }
    }
}

@Composable
fun WindowPrinter(window: WindowNode, modifier: Modifier = Modifier) {
    var enabled by remember { mutableStateOf(true) }

    TreeLine(text = "Window", depth = 2)
    if (enabled) {
        Column(modifier = modifier) {
            for (node in window.nodes) {
                NodePrinter(node, 3)
            }
        }
    }
}

@Composable
fun NodePrinter(node: Node, depth: Int, modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    var enabled by remember { mutableStateOf(true) }

    TreeLine(text = "Node", depth = depth, onClickText = { viewModel.selectNode(node) })
    if (enabled) {
        Column(modifier = modifier) {
            for (child in node.children) {
                NodePrinter(child, depth + 1)
            }
        }
    }
}

@Composable
fun TreeLine(
    text: String,
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
                .padding(smallPadding)
                .clickable { onClickText() }
        )

        Spacer(modifier = Modifier.width(mediumPadding))
    }
}