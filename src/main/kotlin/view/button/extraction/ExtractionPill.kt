package view.button.extraction

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import shared.Colors.discreteTextColor
import shared.Colors.elevatedBackgroundColor
import shared.Colors.extractionProgressBarColor
import shared.Colors.primaryTextColor
import shared.Dimensions.extractButtonDiameter
import shared.Dimensions.extractProgressBarWidth
import shared.Dimensions.mediumPadding
import view.FadeVisibility

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

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(mediumPadding),
        modifier =
            modifier
                .clip(RoundedCornerShape(50))
                .background(elevatedBackgroundColor)
                .animateContentSize(),
    ) {
        ExtractionButton(
            isExtractButtonEnabled = isExtractButtonEnabled,
            onExtractButtonPressed = onExtractButtonPressed,
        )

        Box(contentAlignment = Alignment.CenterStart) {
            FadeVisibility(showDumpSuggestion) {
                Text(
                    text = dumpSuggestionText,
                    color = discreteTextColor,
                    modifier = Modifier.padding(end = mediumPadding + 5.dp),
                )
            }

            FadeVisibility(showCurrentDump) {
                Row {
                    Text(text = "Current:", color = discreteTextColor)

                    Spacer(modifier = Modifier.width(mediumPadding))

                    Text(
                        text = currentDumpInfo,
                        color = primaryTextColor,
                        modifier = Modifier.padding(end = mediumPadding + 5.dp),
                    )
                }
            }

            FadeVisibility(showDumpProgress) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier.height(extractButtonDiameter)
                            .width(extractProgressBarWidth)
                            .padding(top = 6.dp, bottom = 6.dp, end = 6.dp),
                ) {
                    LinearProgressIndicator(
                        progress = animatedDumpProgress,
                        color = extractionProgressBarColor,
                        backgroundColor = extractionProgressBarColor.copy(alpha = 0.2f),
                        strokeCap = StrokeCap.Round,
                        modifier = Modifier.fillMaxSize(),
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(start = mediumPadding, end = mediumPadding),
                    ) {
                        Text(text = dumpProgressText, color = primaryTextColor)

                        Text(
                            text = "${(animatedDumpProgress * 100).roundToInt()}%",
                            color = primaryTextColor,
                        )
                    }
                }
            }
        }
    }
}
