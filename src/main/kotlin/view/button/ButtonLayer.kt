package view.button

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import shared.Colors.discreteTextColor
import shared.Dimensions.mediumPadding
import view.UiLayoutState
import viewmodel.ButtonState

@Composable
fun ButtonLayer(
    uiLayoutState: UiLayoutState,
    extractButtonState: ButtonState,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(mediumPadding),
        modifier = modifier.padding(mediumPadding)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(mediumPadding)
        ) {
            HorizontalPill(modifier = modifier) {
                ExtractionButton(
                    isExtractButtonEnabled = extractButtonState.isExtractButtonEnabled,
                    onExtractButtonPressed = extractButtonState::onExtractButtonPressed
                )

                Text(
                    text = extractButtonState.extractButtonText,
                    color = discreteTextColor,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier
                        .padding(end = mediumPadding + 3.dp)
                        .animateContentSize(
                            animationSpec = spring(
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                )
            }

            DisplayControlPill()
        }

        // This is needed for the elements that follow to be as low on the screen as possible.
        Spacer(modifier = Modifier.weight(1f))

        RoundIconButton(
            onClick = { uiLayoutState.onFitScreenshotToScreenButtonPressed(density) },
            enabled = extractButtonState.areResizeButtonsEnabled,
            iconPainter = painterResource("icons/fit.svg")
        )

        RoundIconButton(
            onClick = uiLayoutState::onEnlargeScreenshotButtonPressed,
            enabled = extractButtonState.areResizeButtonsEnabled,
            iconPainter = painterResource("icons/enlarge.svg")
        )

        RoundIconButton(
            onClick = uiLayoutState::onShrinkScreenshotButtonPressed,
            enabled = extractButtonState.areResizeButtonsEnabled,
            iconPainter = painterResource("icons/shrink.svg")
        )
    }
}
