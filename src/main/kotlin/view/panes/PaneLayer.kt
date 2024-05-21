package view.panes

import AppViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import view.panes.properties.SelectedNodeProperties
import view.panes.tree.XmlTree
import viewmodel.Colors.floatingPaneBackgroundColor
import viewmodel.Colors.hintTextColor
import viewmodel.Dimensions.largeCornerRadius
import viewmodel.Dimensions.mediumPadding
import viewmodel.Dimensions.smallCornerRadius
import viewmodel.Dimensions.smallPadding

@Composable
fun PaneLayer(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(smallPadding),
        modifier = modifier
            .fillMaxHeight()
            .padding(mediumPadding)
    ) {
        VerticalWedge()

        Column(
            verticalArrangement = Arrangement.spacedBy(smallPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            UpperPane(
                modifier = Modifier
                    .height(viewModel.upperPaneHeight)
                    .width(viewModel.paneWidth)
            )

            HorizontalWedge()

            LowerPane(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(viewModel.paneWidth)
            )
        }
    }
}

@Composable
fun UpperPane(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(
                topStart = largeCornerRadius,
                topEnd = largeCornerRadius,
                bottomStart = smallCornerRadius,
                bottomEnd = smallCornerRadius
            ))
            .background(floatingPaneBackgroundColor)
    ) {
        if (viewModel.showXmlTree) {
            XmlTree(modifier = Modifier.fillMaxSize())
        } else {
            Text(
                text = "Missing layout",
                color = hintTextColor,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun LowerPane(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current
    val density = LocalDensity.current.density

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(
                topStart = smallCornerRadius,
                topEnd = smallCornerRadius,
                bottomStart = largeCornerRadius,
                bottomEnd = largeCornerRadius
            ))
            .background(floatingPaneBackgroundColor)
            .onSizeChanged { size ->
                viewModel.onLowerPaneSizeChanged(size, density)
            }
    ) {
        if (viewModel.showSelectedNodeProperties) {
            SelectedNodeProperties(modifier = Modifier.fillMaxSize())
        } else {
            Text(
                text = "No node selected",
                color = hintTextColor,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
