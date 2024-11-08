package view.button.extraction

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject
import shared.Colors.discreteTextColor
import shared.Dimensions.mediumPadding
import view.button.HorizontalPill
import viewmodel.AppViewModel

@Composable
fun ExtractionPill(
    viewModel: AppViewModel = koinInject(),
    modifier: Modifier = Modifier
) {
    HorizontalPill(modifier = modifier) {
        ExtractionButton()

        Text(
            text = viewModel.extractButtonText,
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
}