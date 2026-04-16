package view.panes.tree.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import shared.Colors.discreteTextColor
import shared.Colors.primaryTextColor
import shared.Dimensions.largeCornerRadius
import shared.Dimensions.largePadding
import shared.Dimensions.mediumPadding
import shared.Dimensions.smallPadding
import view.Spacer
import view.panes.topbar.StatefulTopBarIconButton
import view.panes.topbar.TopBarContainer
import view.panes.topbar.TopBarIconButton
import view.utils.toDp

// Todo: fix stutter when resizing search bar, use ".weight(...)"
// Todo: implement search on screenshot
// Todo: make panes draggable in both directions at once
// Todo: add feature to lock screenshot to center
// Todo: persist screenshot lock setting, pane and window sizes.

// This approximation depends on the UI elements that are used and might break often.
// We use this to approximate how much space the search bar field has, starting from the width of
// the search bar.
private val widthOfOtherSearchBarElements = 260.dp

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    currentIndex: Int,
    totalResults: Int,
) {
    var availableInputFieldWidth by remember { mutableStateOf(0.dp) }
    var requiredInputFieldWidth by remember { mutableStateOf(0.dp) }

    val searchBarLayoutState by remember {
        derivedStateOf { computeLayoutState(availableInputFieldWidth, requiredInputFieldWidth) }
    }

    val density = LocalDensity.current.density

    TopBarContainer {
        BoxWithConstraints {
            LaunchedEffect(maxWidth) {
                availableInputFieldWidth = max(0.dp, maxWidth - widthOfOtherSearchBarElements)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier.fillMaxSize()
                        .then(
                            if (searchBarLayoutState.shouldScroll) {
                                Modifier.horizontalScroll(rememberScrollState())
                            } else {
                                Modifier
                            }
                        ),
            ) {
                Spacer(width = smallPadding)

                val searchFieldBackgroundColor = primaryTextColor.copy(alpha = 0.07f)
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier =
                        Modifier.width(searchBarLayoutState.inputFieldWidth)
                            .padding(vertical = 4.dp)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(largeCornerRadius))
                            .background(searchFieldBackgroundColor)
                            .padding(horizontal = mediumPadding),
                ) {
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
                            val inputTextWidth =
                                (it.getLineRight(0) - it.getLineLeft(0)).toDp(density)
                            requiredInputFieldWidth = inputTextWidth + mediumPadding * 2
                        },
                    )
                }

                Spacer(width = largePadding)

                if (totalResults > 0) {
                    Text(
                        text = "$currentIndex/$totalResults",
                        color = discreteTextColor,
                        softWrap = false,
                    )
                } else {
                    Text(text = "No results", color = discreteTextColor, softWrap = false)
                }

                Spacer(width = mediumPadding)

                TopBarIconButton(iconResource = "icons/long_arrow_up.svg", onClick = onNext)
                TopBarIconButton(iconResource = "icons/long_arrow_down.svg", onClick = onPrevious)

                Spacer(width = smallPadding)

                var isImageSearchActive by remember { mutableStateOf(true) }
                StatefulTopBarIconButton(
                    iconResource = "icons/image_search.svg",
                    isActive = isImageSearchActive,
                    onClick = { isImageSearchActive = !isImageSearchActive },
                )

                Spacer(width = smallPadding + mediumPadding)
            }
        }
    }
}

private fun computeLayoutState(
    availableInputFieldWidth: Dp,
    requiredInputFieldWidth: Dp,
): SearchBarLayoutState {
    val minimumInputFieldWidth = 160.dp
    val maximumInputFieldWidthForNoInput = 380.dp

    val isEnoughSpaceForInputField = availableInputFieldWidth > minimumInputFieldWidth
    val isWideEnoughForCurrentInput = requiredInputFieldWidth < maximumInputFieldWidthForNoInput

    val layoutState =
        if (isEnoughSpaceForInputField) {
            if (isWideEnoughForCurrentInput) {
                SearchBarLayoutState(
                    inputFieldWidth =
                        min(availableInputFieldWidth, maximumInputFieldWidthForNoInput),
                    shouldScroll = false,
                )
            } else {
                SearchBarLayoutState(
                    inputFieldWidth = min(availableInputFieldWidth, requiredInputFieldWidth),
                    shouldScroll = false,
                )
            }
        } else {
            SearchBarLayoutState(inputFieldWidth = minimumInputFieldWidth, shouldScroll = true)
        }

    return layoutState
}

private data class SearchBarLayoutState(val inputFieldWidth: Dp, val shouldScroll: Boolean)
