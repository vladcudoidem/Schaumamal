package view.panes.tree

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import shared.Colors.discreteTextColor
import shared.Colors.elevatedBackgroundColor
import shared.Colors.paneBorderColor
import shared.Colors.primaryElementColor
import shared.Colors.primaryTextColor
import shared.Dimensions.largePadding
import shared.Dimensions.mediumPadding
import shared.Dimensions.paneBorderWidth
import shared.Dimensions.scrollbarThickness
import shared.Dimensions.smallPadding
import view.button.RoundIconButton
import view.panes.CustomScrollbarStyle
import view.panes.XmlTreeLine
import view.utils.toPx
import java.awt.Cursor

@Composable
fun XmlTree(
    flatXmlTree: List<XmlTreeLine>,
    selectedNodeIndex: Int,
    activateScroll: Boolean,
    upperPaneHeight: Dp,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current.density

    val upperPaneLazyListState = rememberLazyListState()
    val upperPaneHorizontalScrollState = rememberScrollState(initial = 0)

    LaunchedEffect(selectedNodeIndex) {
        val visibleItemsInfo = upperPaneLazyListState.layoutInfo.visibleItemsInfo
        val visibleItemIndexes = visibleItemsInfo.map { it.index }.drop(1).dropLast(1)
        val selectedNodeHeightPx = visibleItemsInfo.firstOrNull()?.size ?: 0

        if (activateScroll && selectedNodeIndex !in visibleItemIndexes) {
            // Scroll to the selected node in the upper right box.
            upperPaneLazyListState.animateScrollToItem(
                index = selectedNodeIndex,
                // Divide the upper pane height by 2 so that the selected node ends up in the center
                // of the Box.
                scrollOffset =
                    -upperPaneHeight.toPx(density).div(2).minus(selectedNodeHeightPx).toInt(),
            )
        }
    }

    val verticalScrollbarAdapter = rememberScrollbarAdapter(upperPaneLazyListState)
    val horizontalScrollbarAdapter = rememberScrollbarAdapter(upperPaneHorizontalScrollState)

    Column(modifier = modifier) {
        var showSearchBar by remember { mutableStateOf(true) }

        AnimatedVisibility(
            visible = showSearchBar,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            SearchBar(
                onCloseButtonClicked = { showSearchBar = false }
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = upperPaneLazyListState,
                contentPadding =
                    PaddingValues(
                        top = mediumPadding,
                        bottom = mediumPadding * 4 + scrollbarThickness,
                        start = mediumPadding,
                        end = mediumPadding * 4 + scrollbarThickness,
                    ),
                modifier =
                    Modifier.horizontalScroll(upperPaneHorizontalScrollState).animateContentSize(),
            ) {
                items(flatXmlTree) { line -> XmlTreeLine(line = line) }
            }

            HorizontalScrollbar(
                adapter = horizontalScrollbarAdapter,
                style = CustomScrollbarStyle,
                modifier =
                    Modifier.align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(start = largePadding, bottom = smallPadding, end = largePadding)
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
            )

            Column(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                horizontalAlignment = Alignment.End,
            ) {
                androidx.compose.animation.AnimatedVisibility(visible = !showSearchBar) {
                    RoundIconButton(
                        onClick = { showSearchBar = true },
                        iconPainter = painterResource("icons/search.svg"),
                        buttonModifier = Modifier.padding(2.dp).size(50.dp),
                        iconModifier = Modifier.fillMaxSize(0.5f),
                        buttonColors =
                            ButtonDefaults.buttonColors(
                                contentColor = primaryElementColor,
                                disabledContentColor = primaryElementColor.copy(alpha = 0.35f),
                                backgroundColor = Color.Transparent,
                                disabledBackgroundColor = Color.Transparent,
                            ),
                        backgroundBrush =
                            Brush.radialGradient(
                                colors =
                                    listOf(
                                        elevatedBackgroundColor,
                                        elevatedBackgroundColor,
                                        elevatedBackgroundColor,
                                        Color.Transparent,
                                    ),
                                center = Offset.Unspecified,
                                radius = Float.POSITIVE_INFINITY,
                            ),
                    )
                }

                VerticalScrollbar(
                    adapter = verticalScrollbarAdapter,
                    style = CustomScrollbarStyle,
                    modifier =
                        Modifier.fillMaxHeight()
                            .padding(
                                top = if (showSearchBar) mediumPadding else 0.dp,
                                end = smallPadding,
                                bottom = largePadding,
                            )
                            .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
                )
            }
        }
    }
}

@Composable
fun SearchBar(
    onCloseButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = smallPadding),
        ) {
            Row(
                modifier = Modifier
                    .padding(end = 35.dp)
                    .horizontalScroll(scrollState)
                    .padding(
                        start = mediumPadding,
                        top = smallPadding,
                        bottom = smallPadding,
                        end = mediumPadding,
                    )
                    .height(35.dp),
                horizontalArrangement = Arrangement.spacedBy(mediumPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource("icons/search.svg"),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = primaryElementColor,
                )

                var input by remember { mutableStateOf("") }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier.size(200.dp, 30.dp)
                            .clip(RoundedCornerShape(50))
                            .border(
                                width = paneBorderWidth,
                                color = paneBorderColor,
                                shape = RoundedCornerShape(50),
                            )
                            .padding(start = mediumPadding, end = mediumPadding)
                            .semantics {
                                contentDescription = "dwaad"
                            },
                ) {
                    BasicTextField(
                        value = input,
                        onValueChange = { input = it },
                        singleLine = true,
                        cursorBrush = SolidColor(primaryTextColor),
                        textStyle = TextStyle.Default.copy(color = primaryTextColor),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Row {
                    RoundIconButton(
                        onClick = {},
                        iconPainter = painterResource("icons/arrow_upward.svg"),
                        buttonModifier = Modifier.size(35.dp),
                    )

                    RoundIconButton(
                        onClick = {},
                        iconPainter = painterResource("icons/arrow_downward.svg"),
                        buttonModifier = Modifier.size(35.dp),
                    )
                }

                Text(
                    text = "8/19",
                    color = discreteTextColor,
                    fontSize = 14.sp
                )
            }

            RoundIconButton(
                onClick = onCloseButtonClicked,
                iconPainter = painterResource("icons/close.svg"),
                buttonModifier = Modifier.size(35.dp)
                    .align(Alignment.CenterEnd),
                iconModifier = Modifier.fillMaxSize(0.55f)
            )
        }

        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .height(1.dp)
                    .background(
                        brush =
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    paneBorderColor,
                                    paneBorderColor,
                                    paneBorderColor,
                                    Color.Transparent,
                                )
                            )
                    )
        )
    }
}
