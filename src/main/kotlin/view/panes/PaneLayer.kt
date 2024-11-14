package view.panes

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import shared.Colors.discreteTextColor
import shared.Colors.elevatedBackgroundColor
import shared.Colors.paneBorderColor
import shared.Dimensions.largeCornerRadius
import shared.Dimensions.mediumPadding
import shared.Dimensions.paneBorderWidth
import view.FadeVisibility
import view.panes.properties.SelectedNodeProperties
import view.panes.tree.XmlTree
import viewmodel.PaneState
import viewmodel.XmlTreeLine

@Composable
fun PaneLayer(
    paneState: PaneState,
    onWidthConstraintChanged: (Dp) -> Unit,
    onHeightConstraintChanged: (Dp) -> Unit,
    modifier: Modifier = Modifier
) {
    // As an exception we are not passing the modifier parameter to the outer composable, as we are using the o. c.
    // (the BoxWithConstraints) just for background UI handling and not for any user-facing functionality.
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {

        LaunchedEffect(maxWidth) {
            onWidthConstraintChanged(maxWidth)
        }

        LaunchedEffect(maxHeight) {
            onHeightConstraintChanged(maxHeight)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxHeight()
                .padding(mediumPadding)
        ) {
            VerticalWedge(
                onDrag = paneState::onVerticalWedgeDrag
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UpperPane(
                    showXmlTree = paneState.showXmlTree,
                    flatXmlTree = paneState.flatXmlTree,
                    lazyListState = paneState.upperPaneLazyListState,
                    horizontalScrollState = paneState.upperPaneHorizontalScrollState,
                    modifier = Modifier
                        .height(paneState.upperPaneHeight)
                        .width(paneState.paneWidth)
                )

                HorizontalWedge(
                    onDrag = paneState::onHorizontalWedgeDrag
                )

                LowerPane(
                    showSelectedNodeProperties = paneState.showSelectedNodeProperties,
                    onSizeChanged = paneState::onLowerPaneSizeChanged,
                    selectedNodePropertyMap = paneState.selectedNodePropertyMap,
                    verticalScrollState = paneState.lowerPaneVerticalScrollState,
                    horizontalScrollState = paneState.lowerPaneHorizontalScrollState,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(paneState.paneWidth)
                )
            }
        }
    }
}

@Composable
fun UpperPane(
    showXmlTree: Boolean,
    flatXmlTree: List<XmlTreeLine>,
    lazyListState: LazyListState,
    horizontalScrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(largeCornerRadius))
            .border(
                width = paneBorderWidth,
                color = paneBorderColor,
                shape = RoundedCornerShape(largeCornerRadius)
            )
            .background(elevatedBackgroundColor)
    ) {
        FadeVisibility(showXmlTree) {
            XmlTree(
                flatXmlTree = flatXmlTree,
                lazyListState = lazyListState,
                horizontalScrollState = horizontalScrollState
            )
        }

        FadeVisibility(!showXmlTree) {
            Text(
                text = "Missing layout.",
                color = discreteTextColor,
                fontFamily = FontFamily.SansSerif,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                modifier = Modifier
                    .animateContentSize()
                    .padding(mediumPadding)
            )
        }
    }
}

@Composable
fun LowerPane(
    showSelectedNodeProperties: Boolean,
    onSizeChanged: (IntSize) -> Unit,
    selectedNodePropertyMap: LinkedHashMap<String, String>,
    verticalScrollState: ScrollState,
    horizontalScrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(largeCornerRadius))
            .border(
                width = paneBorderWidth,
                color = paneBorderColor,
                shape = RoundedCornerShape(largeCornerRadius)
            )
            .background(elevatedBackgroundColor)
            .onSizeChanged { size ->
                onSizeChanged(size)
            }
    ) {
        FadeVisibility(showSelectedNodeProperties) {
            SelectedNodeProperties(
                selectedNodePropertyMap = selectedNodePropertyMap,
                verticalScrollState = verticalScrollState,
                horizontalScrollState = horizontalScrollState
            )
        }

        FadeVisibility(!showSelectedNodeProperties) {
            Text(
                text = "No node selected.",
                color = discreteTextColor,
                fontFamily = FontFamily.SansSerif,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                modifier = Modifier
                    .animateContentSize()
                    .padding(mediumPadding)
            )
        }
    }
}
