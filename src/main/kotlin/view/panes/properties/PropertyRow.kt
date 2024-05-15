package view.panes.properties

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import view.Colors.secondaryTextColor
import view.Dimensions.maximumPropertyValueWidth
import view.Dimensions.mediumPadding
import view.Dimensions.propertyNameWidth
import view.Dimensions.smallPadding

@Composable
fun PropertyRow(
    property: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(smallPadding)
    ) {
        Spacer(modifier = Modifier.width(mediumPadding))

        Text(
            text = property,
            modifier = Modifier.width(propertyNameWidth),
            color = secondaryTextColor
        )

        Text(
            text = value,
            modifier = Modifier.widthIn(max = maximumPropertyValueWidth),
            color = secondaryTextColor
        )

        Spacer(modifier = Modifier.width(mediumPadding))
    }
}