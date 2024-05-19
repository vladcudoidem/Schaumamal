package view.panes.properties

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import viewmodel.Colors.secondaryTextColor
import viewmodel.Dimensions.maximumPropertyValueWidth
import viewmodel.Dimensions.propertyNameWidth
import viewmodel.Dimensions.smallPadding

@Composable
fun PropertyRow(
    property: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(smallPadding)
    ) {
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
    }
}