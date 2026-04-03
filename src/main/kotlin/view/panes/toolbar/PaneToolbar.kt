package view.panes.toolbar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import java.awt.Cursor
import shared.Colors.disabledPrimaryElementColor
import shared.Colors.discreteTextColor
import shared.Colors.elevatedBackgroundColor
import shared.Colors.paneBorderColor
import shared.Colors.primaryElementColor
import shared.Colors.primaryTextColor
import shared.Dimensions.largeCornerRadius
import shared.Dimensions.mediumPadding
import shared.Dimensions.paneBorderWidth
import shared.Dimensions.smallPadding
import view.FadeVisibility

private val toolbarHeight = 40.dp
private val actionButtonSize = 38.dp
private val titleMinWidth = 80.dp
private val searchFieldMinWidth = 500.dp
private val searchFieldBackgroundColor = primaryTextColor.copy(alpha = 0.07f)

@Composable
fun PaneToolbar(
    title: String,
    actions: List<PaneToolbarAction>,
    onSearch: (String) -> Unit,
    onSearchNext: () -> Unit,
    onSearchPrevious: () -> Unit,
    currentSearchIndex: Int,
    totalSearchResults: Int,
    modifier: Modifier = Modifier,
) {
    var isSearchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val totalButtonCount = actions.size + 1

    BoxWithConstraints {
        val showTitle =
            maxWidth >=
                titleMinWidth +
                    actionButtonSize * totalButtonCount +
                    (smallPadding + mediumPadding) * 2

        Column(modifier = modifier.fillMaxWidth()) {
            TitleRow(
                title = title,
                showTitle = showTitle,
                actions = actions,
                isSearchMode = isSearchMode,
                onSearchClick = {
                    if (isSearchMode) {
                        isSearchMode = false
                        searchQuery = ""
                        onSearch("")
                    } else {
                        isSearchMode = true
                    }
                },
            )

            AnimatedVisibility(
                visible = isSearchMode,
                enter = expandVertically(),
                exit = shrinkVertically(),
            ) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        onSearch(it)
                    },
                    onNext = onSearchNext,
                    onPrevious = onSearchPrevious,
                    onClose = {
                        isSearchMode = false
                        searchQuery = ""
                        onSearch("")
                    },
                    currentIndex = currentSearchIndex,
                    totalResults = totalSearchResults,
                )
            }
        }
    }
}

@Composable
fun PaneTitleBar(title: String, modifier: Modifier = Modifier) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(toolbarHeight)
                .background(elevatedBackgroundColor)
                .drawBehind {
                    val lineWidthPx = paneBorderWidth.toPx()
                    drawLine(
                        color = paneBorderColor,
                        start = Offset(lineWidthPx, size.height - lineWidthPx / 2),
                        end = Offset(size.width - lineWidthPx, size.height - lineWidthPx / 2),
                        strokeWidth = lineWidthPx,
                    )
                }
                .padding(horizontal = smallPadding + mediumPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = primaryTextColor,
            fontWeight = FontWeight.Bold,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun TitleRow(
    title: String,
    showTitle: Boolean,
    actions: List<PaneToolbarAction>,
    isSearchMode: Boolean,
    onSearchClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier.fillMaxWidth()
                .height(toolbarHeight)
                .background(elevatedBackgroundColor)
                .drawBehind {
                    val lineWidthPx = paneBorderWidth.toPx()
                    drawLine(
                        color = paneBorderColor,
                        start = Offset(lineWidthPx, size.height - lineWidthPx / 2),
                        end = Offset(size.width - lineWidthPx, size.height - lineWidthPx / 2),
                        strokeWidth = lineWidthPx,
                    )
                }
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = smallPadding + mediumPadding),
    ) {
        FadeVisibility(
            visible = showTitle,
            enter = expandHorizontally(),
            exit = shrinkHorizontally(),
        ) {
            Text(
                text = title,
                color = primaryTextColor,
                fontWeight = FontWeight.Bold,
                softWrap = false,
            )
        }

        Spacer(Modifier.weight(1f))

        Row(horizontalArrangement = Arrangement.spacedBy(smallPadding)) {
            actions.forEach { action ->
                ToolbarIconButton(iconResource = action.iconResource, onClick = action.onClick)
            }

            val searchButtonColors =
                if (!isSearchMode) {
                    ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = primaryElementColor,
                    )
                } else {
                    ButtonDefaults.buttonColors(
                        backgroundColor = discreteTextColor,
                        contentColor = elevatedBackgroundColor,
                    )
                }
            ToolbarIconButton(
                iconResource = "icons/search.svg",
                onClick = onSearchClick,
                buttonColors = searchButtonColors,
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onClose: () -> Unit,
    currentIndex: Int,
    totalResults: Int,
) {
    var availableWidth by remember { mutableStateOf(0.dp) }
    var textWidth by remember { mutableStateOf(0.dp) }

    val targetWidth =
        if (availableWidth < 160.dp) {
            160.dp
        } else {
            if (textWidth < 300.dp) {
                min(availableWidth, 300.dp)
            } else {
                min(availableWidth, textWidth)
            }
        }
    val shouldBeScrollable = availableWidth < 160.dp

    BoxWithConstraints(
        contentAlignment = Alignment.TopCenter,
        modifier =
            Modifier.fillMaxWidth()
                .height(toolbarHeight - 1.dp)
                .background(elevatedBackgroundColor)
                .drawBehind {
                    val lineWidthPx = paneBorderWidth.toPx()
                    drawLine(
                        color = paneBorderColor,
                        start = Offset(lineWidthPx, size.height - lineWidthPx / 2),
                        end = Offset(size.width - lineWidthPx, size.height - lineWidthPx / 2),
                        strokeWidth = lineWidthPx,
                    )
                },
    ) {
        LaunchedEffect(maxWidth) {
            availableWidth = max(0.dp, maxWidth - 200.dp)
            println("new availableWidth = $availableWidth")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(smallPadding),
            modifier =
                Modifier.fillMaxWidth()
                    .height(toolbarHeight - 2.dp)
                    .padding(start = smallPadding, end = smallPadding + mediumPadding)
                    .then(
                        if (shouldBeScrollable) {
                            Modifier.horizontalScroll(rememberScrollState())
                        } else {
                            Modifier
                        }
                    ),
        ) {
            BoxWithConstraints(
                contentAlignment = Alignment.CenterStart,
                modifier =
                    Modifier.width(targetWidth)
                        .padding(vertical = 4.dp)
                        .height(actionButtonSize)
                        .clip(RoundedCornerShape(largeCornerRadius))
                        .background(searchFieldBackgroundColor),
            ) {
                Box(modifier = Modifier.padding(horizontal = mediumPadding)) {
                    if (query.isEmpty()) {
                        Text(text = "Type here...", color = discreteTextColor)
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = TextStyle(color = primaryTextColor),
                        cursorBrush = SolidColor(primaryTextColor),
                        modifier = Modifier.fillMaxWidth(),
                        onTextLayout = {
                            textWidth =
                                (it.getLineRight(0) - it.getLineLeft(0)).dp / 2 + mediumPadding * 2
                            println("new textWidth = $textWidth")
                        },
                    )
                }
            }

            Spacer(Modifier.width(mediumPadding))

            if (totalResults > 0) {
                Text(
                    text = "$currentIndex/$totalResults",
                    color = discreteTextColor,
                    softWrap = false,
                )
            } else {
                Text(text = "No results", color = discreteTextColor, softWrap = false)
            }

            Row {
                ToolbarIconButton(iconResource = "icons/long_arrow_up.svg", onClick = onNext)
                ToolbarIconButton(iconResource = "icons/long_arrow_down.svg", onClick = onPrevious)
            }
        }
    }
}

@Composable
private fun ToolbarIconButton(
    iconResource: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    buttonColors: ButtonColors =
        ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = primaryElementColor,
            disabledBackgroundColor = Color.Transparent,
            disabledContentColor = disabledPrimaryElementColor,
        ),
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(largeCornerRadius),
        contentPadding = PaddingValues(0.dp),
        colors = buttonColors,
        elevation =
            ButtonDefaults.elevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                hoveredElevation = 0.dp,
                focusedElevation = 0.dp,
            ),
        modifier =
            Modifier.size(actionButtonSize)
                .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
    ) {
        Icon(
            painter = painterResource(iconResource),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.55f),
        )
    }
}

enum class ScrollBarSizeState {
    SCROLL,
    GROW,
    MAX,
}
