package view

import AppViewModel
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import model.parser.Node
import view.Dimensions.mediumPadding
import view.Dimensions.smallPadding

// TODO do this with LazyColumn

val startPaddingPerLevel = 20.dp

@Composable
fun TreePrinter(modifier: Modifier = Modifier) {
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

        TreeLine(text = "Root", depth = 0)
        for (display in rootNode.displays) {
            TreeLine(text = "Display ${display.id}", depth = 1)
            for (window in display.windows) {
                TreeLine(text = "Window ${window.id}", depth = 2)
                for (rootWindowNode in window.nodes) {
                    NodePrinter(rootWindowNode, depth = 3)
                }
            }
        }

        Spacer(modifier = Modifier.height(mediumPadding))
    }
}

@Composable
fun NodePrinter(node: Node, depth: Int) {
    TreeLine(text = "Node:${node.className.split(".").last()}", depth = depth)
    for (childNode in node.children) {
        NodePrinter(childNode, depth = depth + 1)
    }
}

@Composable
fun TreeLine(
    text: String,
    modifier: Modifier = Modifier,
    depth: Int = 0
) {
    if (depth < 0) error("Depth cannot be lower than 0.")

    Row(modifier = modifier) {
        Spacer(modifier = Modifier.width(mediumPadding))

        Spacer(modifier = Modifier.width(startPaddingPerLevel * depth))
        Text(text = text, color = Colors.secondaryTextColor, modifier = Modifier.padding(smallPadding))

        Spacer(modifier = Modifier.width(mediumPadding))
    }
}