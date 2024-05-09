package view

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import model.parser.Node
import view.button.RoundButton
import view.panels.FloatingPanes
import view.screenshot.Screenshot

val UpperBoxVerticalScrollState = compositionLocalOf<ScrollState> {
    error("No Scroll State provided")
}

val UpperBoxItemPositions = compositionLocalOf<SnapshotStateMap<Node, Int>> {
    error("No Item Positions provided")
}

@Composable
fun MainScreen() {
    val upperBoxVerticalScrollState = rememberScrollState()
    val upperBoxItemPositions = remember { mutableStateMapOf<Node, Int>() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.backgroundColor)
    ) {
        CompositionLocalProvider(
            UpperBoxVerticalScrollState provides upperBoxVerticalScrollState,
            UpperBoxItemPositions provides upperBoxItemPositions
        ) {
            Screenshot(modifier = Modifier.fillMaxSize())
            RoundButton(modifier = Modifier.align(Alignment.TopStart))
            FloatingPanes(modifier = Modifier.align(Alignment.TopEnd))
        }
    }
}