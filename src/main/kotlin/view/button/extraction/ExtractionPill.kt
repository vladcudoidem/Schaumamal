package view.button.extraction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import shared.Colors.discreteTextColor
import shared.Colors.elevatedBackgroundColor
import shared.Colors.primaryTextColor
import shared.Dimensions.mediumPadding

@Composable
fun ExtractionPill(
    showDumpSuggestion: Boolean,
    dumpSuggestionText: String,
    showCurrentDump: Boolean,
    currentDumpInfo: String,
    showDumpProgress: Boolean,
    dumpProgressText: String,
    isExtractButtonEnabled: Boolean,
    onExtractButtonPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(mediumPadding),
        modifier = modifier.clip(RoundedCornerShape(50)).background(elevatedBackgroundColor),
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
