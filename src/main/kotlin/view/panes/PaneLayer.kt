package view.panes

import AppViewModel
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import shared.Colors.discreteTextColor
import shared.Colors.paneBackgroundColor
import shared.Dimensions.largeCornerRadius
import shared.Dimensions.mediumPadding
import view.panes.properties.SelectedNodeProperties
import view.panes.tree.XmlTree

@Composable
fun PaneLayer(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    // As an exception we are not passing the modifier parameter to the outer composable, as we are using the o. c.
    // (the BoxWithConstraints) just for background UI handling and not for any user-facing functionality.
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(mediumPadding)
    ) {

        LaunchedEffect(maxWidth) {
            viewModel.onPanesWidthConstraintChanged(newWidthConstraint = maxWidth)
        }

        LaunchedEffect(maxHeight) {
            viewModel.onPanesHeightConstraintChanged(newHeightConstraint = maxHeight)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxHeight()
        ) {
            VerticalWedge()

            Column(
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
}

@Composable
fun UpperPane(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(
                topStart = largeCornerRadius,
                topEnd = largeCornerRadius,
                bottomStart = largeCornerRadius,
                bottomEnd = largeCornerRadius
            ))
            .background(paneBackgroundColor)
    ) {
        if (viewModel.showXmlTree) {
            XmlTree()
        } else {
            Text(
                text = "Missing layout",
                color = discreteTextColor,
                fontFamily = FontFamily.Monospace,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                modifier = Modifier
                    .align(Alignment.Center)
                    .animateContentSize()
            )
        }
    }
}

@Composable
fun LowerPane(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(
                topStart = largeCornerRadius,
                topEnd = largeCornerRadius,
                bottomStart = largeCornerRadius,
                bottomEnd = largeCornerRadius
            ))
            .background(paneBackgroundColor)
            .onSizeChanged { size ->
                viewModel.onLowerPaneSizeChanged(size)
            }
    ) {
        if (viewModel.showSelectedNodeProperties) {
            SelectedNodeProperties()
        } else {
            Text(
                text = "No node selected",
                color = discreteTextColor,
                fontFamily = FontFamily.Monospace,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                modifier = Modifier
                    .align(Alignment.Center)
                    .animateContentSize()
            )
        }
    }
}
