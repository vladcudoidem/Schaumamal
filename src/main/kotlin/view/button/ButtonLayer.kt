package view.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import shared.Dimensions.mediumPadding
import view.button.displayControl.DisplayControlPill
import view.button.extraction.ExtractionPill

@Composable
fun ButtonLayer(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(mediumPadding),
        modifier = modifier.padding(mediumPadding)
    ) {
        ExtractionPill()

        DisplayControlPill()
    }
}
