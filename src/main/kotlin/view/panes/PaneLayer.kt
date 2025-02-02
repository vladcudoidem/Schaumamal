package view.panes

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import shared.Colors.discreteTextColor
import shared.Colors.elevatedBackgroundColor
import shared.Colors.paneBorderColor
import shared.Dimensions.largeCornerRadius
import shared.Dimensions.mediumPadding
import shared.Dimensions.paneBorderWidth
import view.FadeVisibility
import view.UiLayoutState
import view.panes.properties.SelectedNodeProperties
import view.panes.tree.XmlTree

@Composable
fun PaneLayer(
    uiLayoutState: UiLayoutState,
    paneState: PaneState,
    modifier: Modifier = Modifier
) {
    val showXmlTree by paneState.showXmlTree.collectAsState(initial = false)
    val flatXmlTree by paneState.flatXmlTree.collectAsState(initial = emptyList())
    val selectedNodeIndex by paneState.selectedNodeIndex.collectAsState(initial = 0)
    val activateScroll by paneState.activateScroll.collectAsState(initial = false)
    val showSelectedNodeProperties by paneState.showSelectedNodeProperties.collectAsState(initial = false)
    val selectedNodePropertyMap by paneState.selectedNodePropertyMap.collectAsState(initial = LinkedHashMap())
        // using an empty linked hash map as initial value
    val paneWidth by uiLayoutState.paneWidth.collectAsState()
    val upperPaneHeight by uiLayoutState.upperPaneHeight.collectAsState()

    // As an exception we are not passing the modifier parameter to the outer composable, as we are using the o. c.
    // (the BoxWithConstraints) just for background UI handling and not for any user-facing functionality.
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {

        LaunchedEffect(maxWidth) {
            uiLayoutState.onPanesWidthConstraintChanged(maxWidth)
        }

        LaunchedEffect(maxHeight) {
            uiLayoutState.onPanesHeightConstraintChanged(maxHeight)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxHeight()
                .padding(mediumPadding)
        ) {
            VerticalWedge(
                onDrag = uiLayoutState::onVerticalWedgeDrag
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UpperPane(
                    showXmlTree = showXmlTree,
                    flatXmlTree = flatXmlTree,
                    selectedNodeIndex = selectedNodeIndex,
                    activateScroll = activateScroll,
                    upperPaneHeight = upperPaneHeight,
                    modifier = Modifier
                        .height(upperPaneHeight)
                        .width(paneWidth)
                )

                HorizontalWedge(
                    onDrag = uiLayoutState::onHorizontalWedgeDrag
                )

                LowerPane(
                    showSelectedNodeProperties = showSelectedNodeProperties,
                    selectedNodePropertyMap = selectedNodePropertyMap,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(paneWidth)
                )
            }
        }
    }
}

@Composable
fun UpperPane(
    showXmlTree: Boolean,
    flatXmlTree: List<XmlTreeLine>,
    selectedNodeIndex: Int,
    activateScroll: Boolean,
    upperPaneHeight: Dp,
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
                selectedNodeIndex = selectedNodeIndex,
                activateScroll = activateScroll,
                upperPaneHeight = upperPaneHeight
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
    selectedNodePropertyMap: LinkedHashMap<String, String>,
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
        FadeVisibility(showSelectedNodeProperties) {
            SelectedNodeProperties(
                selectedNodePropertyMap = selectedNodePropertyMap
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
