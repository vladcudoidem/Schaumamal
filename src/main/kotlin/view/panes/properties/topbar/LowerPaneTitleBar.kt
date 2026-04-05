package view.panes.properties.topbar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import shared.Colors.primaryTextColor
import shared.Dimensions.mediumPadding
import shared.Dimensions.smallPadding
import view.panes.topbar.TopBarContainer

@Composable
fun LowerPaneTitleBar(modifier: Modifier = Modifier) {
    TopBarContainer(isFirstTopBar = true, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize().padding(horizontal = smallPadding + mediumPadding),
        ) {
            Text(
                text = "Node Properties",
                color = primaryTextColor,
                fontWeight = FontWeight.Bold,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
