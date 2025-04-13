package view.button.extraction

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import shared.Colors.discreteTextColor
import shared.Colors.elevatedBackgroundColor
import shared.Colors.extractionProgressBarColor
import shared.Colors.primaryTextColor
import shared.Dimensions.extractButtonDiameter
import shared.Dimensions.extractProgressBarWidth
import shared.Dimensions.mediumPadding

@Composable
fun ExtractionPill(
    showDumpSuggestion: Boolean,
    dumpSuggestionText: String,
    showCurrentDump: Boolean,
    currentDumpInfo: String,
    showDumpProgress: Boolean,
    dumpProgress: Float,
    dumpProgressText: String,
    isExtractButtonEnabled: Boolean,
    onExtractButtonPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val animatedDumpProgress by
        animateFloatAsState(targetValue = dumpProgress, animationSpec = tween())

    Box(
        modifier =
            modifier
                .clip(RoundedCornerShape(50))
                .background(elevatedBackgroundColor)
                .animateContentSize()
    ) {
        if (showDumpProgress) {
            Box(
                modifier =
                    Modifier.size(width = extractProgressBarWidth, height = extractButtonDiameter)
                        .padding(5.dp)
            ) {
                LinearProgressIndicator(
                    progress = animatedDumpProgress,
                    color = extractionProgressBarColor,
                    backgroundColor = Color.Transparent,
                    strokeCap = StrokeCap.Round,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(mediumPadding),
        ) {
            ExtractionButton(
                isExtractButtonEnabled = isExtractButtonEnabled,
                onExtractButtonPressed = onExtractButtonPressed,
            )

            if (showDumpSuggestion) {
                Text(
                    text = dumpSuggestionText,
                    color = discreteTextColor,
                    modifier = Modifier.padding(end = mediumPadding + 5.dp),
                )
            }

            if (showCurrentDump) {
                Text(text = "Current:", color = discreteTextColor)

                Text(
                    text = currentDumpInfo,
                    color = primaryTextColor,
                    modifier = Modifier.padding(end = mediumPadding + 5.dp),
                )
            }

            if (showDumpProgress) {
                Text(
                    text = dumpProgressText,
                    color = primaryTextColor,
                    modifier = Modifier.padding(end = mediumPadding + 5.dp),
                )
            }
        }
    }
}
