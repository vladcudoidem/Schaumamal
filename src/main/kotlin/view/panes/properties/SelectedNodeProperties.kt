package view.panes.properties

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import shared.Dimensions.mediumPadding
import shared.Dimensions.scrollbarThickness
import view.panes.ScrollableBox

@Composable
fun SelectedNodeProperties(
    selectedNodePropertyMap: LinkedHashMap<String, String>,
    modifier: Modifier = Modifier,
) {
    val verticalScrollState = rememberScrollState(initial = 0)
    val horizontalScrollState = rememberScrollState(initial = 0)

    ScrollableBox(
        verticalScrollbarAdapter = rememberScrollbarAdapter(verticalScrollState),
        horizontalScrollbarAdapter = rememberScrollbarAdapter(horizontalScrollState),
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier.verticalScroll(verticalScrollState)
                    .horizontalScroll(horizontalScrollState)
                    .padding(
                        top = mediumPadding,
                        bottom = mediumPadding * 4 + scrollbarThickness,
                        start = mediumPadding,
                        end = mediumPadding * 4 + scrollbarThickness,
                    )
                    .animateContentSize()
        ) {
            selectedNodePropertyMap.forEach { (property, value) ->
                PropertyRow(property = property, value = value)
            }
        }
    }
}
