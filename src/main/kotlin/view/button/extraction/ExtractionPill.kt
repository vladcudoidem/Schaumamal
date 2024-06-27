package view.button.extraction

import AppViewModel
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import shared.Colors.discreteTextColor
import shared.Dimensions.mediumPadding
import view.button.HorizontalPill

@Composable
fun ExtractionPill(modifier: Modifier = Modifier) {
    val viewModel = AppViewModel.current

    HorizontalPill(modifier = modifier) {
        ExtractionButton()

        Text(
            text = viewModel.buttonText,
            color = discreteTextColor,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .padding(end = mediumPadding)
                .animateContentSize()
        )
    }
}