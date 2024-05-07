package view

import AppViewModel
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import view.Dimensions.mediumPadding
import view.Dimensions.smallPadding

@Composable
fun SelectedNode(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current
    val selectedNode = viewModel.selectedNode

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(verticalScrollState)
            .horizontalScroll(horizontalScrollState)
    ) {
        Spacer(modifier = Modifier.height(mediumPadding))

        PropertyRow(property = "index", value = "${selectedNode.index}")
        PropertyRow(property = "text", value = selectedNode.text)
        PropertyRow(property = "resource-id", value = selectedNode.resourceId)
        PropertyRow(property = "class", value = selectedNode.className)
        PropertyRow(property = "package", value = selectedNode.packageName)
        PropertyRow(property = "content-desc", value = selectedNode.contentDesc)
        PropertyRow(property = "checkable", value = "${selectedNode.checkable}")
        PropertyRow(property = "checked", value = "${selectedNode.checked}")
        PropertyRow(property = "clickable", value = "${selectedNode.clickable}")
        PropertyRow(property = "enabled", value = "${selectedNode.enabled}")
        PropertyRow(property = "focusable", value = "${selectedNode.focusable}")
        PropertyRow(property = "focused", value = "${selectedNode.focused}")
        PropertyRow(property = "scrollable", value = "${selectedNode.scrollable}")
        PropertyRow(property = "long-clickable", value = "${selectedNode.longClickable}")
        PropertyRow(property = "password", value = "${selectedNode.password}")
        PropertyRow(property = "selected", value = "${selectedNode.selected}")
        PropertyRow(property = "bounds", value = selectedNode.bounds)

        Spacer(modifier = Modifier.height(mediumPadding))
    }
}

@Composable
fun PropertyRow(
    property: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(smallPadding)
    ) {
        Spacer(modifier = Modifier.width(mediumPadding))

        Text(
            text = property,
            modifier = Modifier.width(200.dp),
            color = Colors.secondaryTextColor
        )

        Text(
            text = value.ifEmpty { "-" },
            modifier = Modifier.widthIn(max = 600.dp),
            color = Colors.secondaryTextColor
        )

        Spacer(modifier = Modifier.width(mediumPadding))
    }
}