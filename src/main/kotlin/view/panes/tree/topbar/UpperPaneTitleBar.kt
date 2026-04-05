package view.panes.tree.topbar

import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import shared.Colors.primaryTextColor
import shared.Dimensions.mediumPadding
import shared.Dimensions.smallPadding
import view.FadeVisibility
import view.Spacer
import view.panes.topbar.PaneTopBarActionButton
import view.panes.topbar.StatefulTopBarIconButton
import view.panes.topbar.TopBarContainer
import view.panes.topbar.TopBarIconButton
import view.panes.topbar.topBarButtonSize

@Composable
fun UpperPaneTitleBar(
    actions: List<PaneTopBarActionButton>,
    isSearchModeActive: Boolean,
    onSearchClick: () -> Unit,
) {
    val totalButtonCount = actions.size + 1 // one more for search button

    TopBarContainer(isFirstTopBar = true) {
        BoxWithConstraints {
            // approximate width of title text element
            val titleWidth = 50.dp
            // We need a tolerance to force the title to disappear a little sooner than absolutely
            // required.
            val tolerance = 20.dp

            // The following logic heavily relies on the UI implementation and might break often.
            val showTitle =
                maxWidth >=
                    titleWidth +
                        tolerance +
                        (topBarButtonSize + smallPadding) * totalButtonCount +
                        (smallPadding + mediumPadding) * 2

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize().horizontalScroll(rememberScrollState()),
            ) {
                FadeVisibility(
                    visible = showTitle,
                    enter = expandHorizontally(),
                    exit = shrinkHorizontally(),
                ) {
                    Text(
                        text = "UI Tree",
                        color = primaryTextColor,
                        fontWeight = FontWeight.Bold,
                        softWrap = false,
                        modifier = Modifier.padding(start = smallPadding + mediumPadding),
                    )
                }

                Spacer(Modifier.weight(1f))

                Spacer(width = smallPadding + mediumPadding)

                Row(horizontalArrangement = Arrangement.spacedBy(smallPadding)) {
                    actions.forEach { action ->
                        TopBarIconButton(
                            iconResource = action.iconResource,
                            onClick = action.onClick,
                        )
                    }

                    StatefulTopBarIconButton(
                        iconResource = "icons/search.svg",
                        isActive = isSearchModeActive,
                        onClick = onSearchClick,
                    )
                }

                Spacer(width = smallPadding + mediumPadding)
            }
        }
    }
}
