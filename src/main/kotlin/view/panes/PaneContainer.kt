package view.panes

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import shared.Colors.discreteTextColor
import shared.Colors.elevatedBackgroundColor
import shared.Colors.paneBorderColor
import shared.Dimensions.largeCornerRadius
import shared.Dimensions.mediumPadding
import shared.Dimensions.paneBorderWidth
import view.FadeVisibility

@Composable
fun PaneContainer(
    showContent: Boolean,
    placeholder: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .clip(RoundedCornerShape(largeCornerRadius))
                .border(
                    width = paneBorderWidth,
                    color = paneBorderColor,
                    shape = RoundedCornerShape(largeCornerRadius),
                )
                .background(elevatedBackgroundColor),
    ) {
        FadeVisibility(showContent) { content() }

        FadeVisibility(!showContent) {
            Text(
                text = placeholder,
                color = discreteTextColor,
                overflow = TextOverflow.Ellipsis,
                softWrap = false,
                modifier = Modifier.animateContentSize().padding(mediumPadding),
            )
        }
    }
}
