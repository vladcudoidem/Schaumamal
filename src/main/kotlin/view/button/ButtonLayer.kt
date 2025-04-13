package view.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import shared.Dimensions.mediumPadding
import view.UiLayoutState
import view.button.displayControl.DisplayControlPill
import view.button.extraction.ExtractionPill

@Composable
fun ButtonLayer(
    uiLayoutState: UiLayoutState,
    buttonState: ButtonState,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current.density

    val showDumpSuggestion by buttonState.showDumpSuggestion.collectAsState(initial = true)
    val dumpSuggestionText = buttonState.dumpSuggestionText

    val showCurrentDump by buttonState.showCurrentDump.collectAsState(initial = false)
    val currentDumpInfo by buttonState.currentDumpInfo.collectAsState(initial = "...")

    val showDumpProgress by buttonState.showDumpProgress.collectAsState(initial = false)
    val dumpProgress by buttonState.dumpProgress.collectAsState()
    val dumpProgressText by buttonState.dumpProgressText.collectAsState()

    val areResizeButtonsEnabled by
        buttonState.areResizeButtonsEnabled.collectAsState(initial = false)
    val isExtractButtonEnabled by buttonState.isExtractButtonEnabled.collectAsState(initial = true)
    val isOpenDumpHistoryButtonEnabled by
        buttonState.isOpenDumpHistoryButtonEnabled.collectAsState(initial = false)

    val areDisplayControlButtonsEnabled by
        buttonState.areDisplayControlButtonsEnabled.collectAsState(initial = false)
    val displayCounter by buttonState.displayCounter.collectAsState(initial = "?/?")

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(mediumPadding),
        modifier = modifier.padding(mediumPadding),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(mediumPadding),
        ) {
            ExtractionPill(
                showDumpSuggestion = showDumpSuggestion,
                dumpSuggestionText = dumpSuggestionText,
                showCurrentDump = showCurrentDump,
                currentDumpInfo = currentDumpInfo,
                showDumpProgress = showDumpProgress,
                dumpProgress = dumpProgress,
                dumpProgressText = dumpProgressText,
                isExtractButtonEnabled = isExtractButtonEnabled,
                onExtractButtonPressed = buttonState::onExtractButtonPressed,
            )

            RoundIconButton(
                onClick = buttonState::onOpenDumpHistoryButtonPressed,
                enabled = isOpenDumpHistoryButtonEnabled,
                iconPainter = painterResource("icons/history.svg"),
            )

            DisplayControlPill(
                areDisplayControlButtonsEnabled = areDisplayControlButtonsEnabled,
                displayCounter = displayCounter,
                onNextDisplayButtonPressed = buttonState::onNextDisplayButtonPressed,
                onPreviousDisplayButtonPressed = buttonState::onPreviousDisplayButtonPressed,
            )
        }

        // This is needed for the elements that follow to be as low on the screen as possible.
        Spacer(modifier = Modifier.weight(1f))

        RoundIconButton(
            onClick = { uiLayoutState.onFitScreenshotToScreenButtonPressed(density) },
            enabled = areResizeButtonsEnabled,
            iconPainter = painterResource("icons/fit.svg"),
        )

        RoundIconButton(
            onClick = uiLayoutState::onEnlargeScreenshotButtonPressed,
            enabled = areResizeButtonsEnabled,
            iconPainter = painterResource("icons/enlarge.svg"),
        )

        RoundIconButton(
            onClick = uiLayoutState::onShrinkScreenshotButtonPressed,
            enabled = areResizeButtonsEnabled,
            iconPainter = painterResource("icons/shrink.svg"),
        )
    }
}
