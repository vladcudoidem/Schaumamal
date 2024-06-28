package view.button

import AppViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import shared.Dimensions.mediumPadding
import view.button.displayControl.DisplayControlPill
import view.button.extraction.ExtractionPill

@Composable
fun ButtonLayer(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(mediumPadding),
        modifier = modifier.padding(mediumPadding)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(mediumPadding)
        ) {
            ExtractionPill()

            DisplayControlPill()
        }

        // This is needed for the elements that follow to be as low on the screen as possible.
        Spacer(modifier = Modifier.weight(1f))

        RoundIconButton(
            onClick = viewModel::onFitScreenshotToScreenButtonPressed,
            enabled = viewModel.areResizeButtonsEnabled,
            iconPainter = painterResource("icons/fit.svg")
        )

        RoundIconButton(
            onClick = viewModel::onEnlargeScreenshotButtonPressed,
            enabled = viewModel.areResizeButtonsEnabled,
            iconPainter = painterResource("icons/enlarge.svg")
        )

        RoundIconButton(
            onClick = viewModel::onShrinkScreenshotButtonPressed,
            enabled = viewModel.areResizeButtonsEnabled,
            iconPainter = painterResource("icons/shrink.svg")
        )
    }
}
