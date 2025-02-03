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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import shared.Colors.discreteTextColor
import shared.Dimensions.mediumPadding
import view.UiLayoutState
import view.button.displayControl.DisplayControlPill
import view.button.extraction.ExtractionButton

@Composable
fun ButtonLayer(
    uiLayoutState: UiLayoutState,
    buttonState: ButtonState,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density

    val areResizeButtonsEnabled by buttonState.areResizeButtonsEnabled.collectAsState(initial = false)
    val isExtractButtonEnabled by buttonState.isExtractButtonEnabled.collectAsState(initial = true)
    val extractButtonText by buttonState.extractButtonText.collectAsState(initial = "...")
        // Todo: is the "..." ok?

    val displayCounter by buttonState.displayCounter.collectAsState(initial = "-/-")

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
                    isExtractButtonEnabled = isExtractButtonEnabled,
                    onExtractButtonPressed = buttonState::onExtractButtonPressed
                )

                Text(
                    text = extractButtonText,
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

            DisplayControlPill(
                displayCounter = displayCounter,
                onNextDisplayButtonPressed = buttonState::onNextDisplayButtonPressed,
                onPreviousDisplayButtonPressed = buttonState::onPreviousDisplayButtonPressed
            )
        }

        // This is needed for the elements that follow to be as low on the screen as possible.
        Spacer(modifier = Modifier.weight(1f))

        RoundIconButton(
            onClick = { uiLayoutState.onFitScreenshotToScreenButtonPressed(density) },
            enabled = areResizeButtonsEnabled,
            iconPainter = painterResource("icons/fit.svg")
        )

        RoundIconButton(
            onClick = uiLayoutState::onEnlargeScreenshotButtonPressed,
            enabled = areResizeButtonsEnabled,
            iconPainter = painterResource("icons/enlarge.svg")
        )

        RoundIconButton(
            onClick = uiLayoutState::onShrinkScreenshotButtonPressed,
            enabled = areResizeButtonsEnabled,
            iconPainter = painterResource("icons/shrink.svg")
        )
    }
}
