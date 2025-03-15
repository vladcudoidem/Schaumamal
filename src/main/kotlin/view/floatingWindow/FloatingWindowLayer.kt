package view.floatingWindow

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import shared.Colors.accentColor
import shared.Colors.discreteTextColor
import shared.Colors.elevatedBackgroundColor
import shared.Colors.paneBorderColor
import shared.Colors.primaryTextColor
import shared.Dimensions.largePadding
import shared.Dimensions.mediumCornerRadius
import shared.Dimensions.mediumPadding
import shared.Dimensions.paneBorderWidth
import shared.Dimensions.smallPadding
import view.button.DumpHistoryEntry
import view.button.RoundIconButton
import java.awt.Cursor

@Composable
fun DumpHistoryList(
    historyList: List<DumpHistoryEntry>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(smallPadding),
        contentPadding = PaddingValues(mediumPadding),
        modifier = modifier
    ) {
        items(historyList) {
            DumpHistoryLine(it)
        }
    }
}

@Composable
fun DumpHistoryLine(
    dumpHistoryEntry: DumpHistoryEntry,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val indication = ripple(color = accentColor)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(mediumCornerRadius))
            .clickable(
                interactionSource = interactionSource,
                indication = indication,
                onClick = dumpHistoryEntry.onDumpHistoryEntryClicked
            )
            .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
            .background(
                if (dumpHistoryEntry.selected) {
                    accentColor.copy(alpha = 0.3f)
                } else {
                    Color.Transparent
                }
            )
            .padding(
                start = largePadding,
                end = largePadding,
                top = mediumPadding,
                bottom = mediumPadding
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap = dumpHistoryEntry.thumbnail,
            contentDescription = null,
            contentScale = ContentScale.Inside,
            modifier = Modifier.size(30.dp)
        )

        Spacer(modifier = Modifier.width(largePadding))

        Column {
            Text(
                text = dumpHistoryEntry.name,
                color = primaryTextColor,
                fontSize = 16.sp
            )

            Row {
                Text(
                    text = dumpHistoryEntry.date,
                    color = discreteTextColor
                )

                Spacer(modifier = Modifier.weight(1f))

                val count = dumpHistoryEntry.displayCount
                Text(
                    text = "$count display${if (count > 1) "s" else ""}",
                    color = discreteTextColor
                )
            }
        }
    }
}

@Composable
fun FloatingWindowLayer(
    floatingWindowState: FloatingWindowState,
    modifier: Modifier = Modifier
) {
    val windowState by floatingWindowState.windowState.collectAsState()
    val dumpHistoryEntries by floatingWindowState.dumpHistoryEntries.collectAsState(initial = emptyList())

    val showWindow = windowState != WindowState.HIDDEN

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = showWindow,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)) // Todo: store somewhere else.
                    .clickable(
                        interactionSource = null,
                        indication = null,
                        onClick = floatingWindowState::closeFloatingWindow
                    )
            )
        }

        AnimatedVisibility(visible = showWindow) {
            Box(
                modifier = Modifier
                    .padding(50.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .height(500.dp) // Todo: change
                    .width(400.dp)
                    .background(elevatedBackgroundColor)
                    .clickable(interactionSource = null, indication = null, onClick = { }) // Todo: needed?
                    .border(
                        width = paneBorderWidth,
                        color = paneBorderColor,
                        shape = RoundedCornerShape(30.dp)
                    )
            ) {
                Column {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(mediumPadding)
                    ) {
                        RoundIconButton(
                            onClick = floatingWindowState::closeFloatingWindow,
                            iconPainter = painterResource("icons/close.svg"),
                            buttonModifier = Modifier.align(Alignment.CenterStart)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val windowTitleText = when (windowState) {
                                WindowState.SHOW_DUMP_HISTORY -> "Dump History"
                                WindowState.SHOW_SETTINGS -> "Settings"
                                else -> ""
                            }

                            Text(
                                text = windowTitleText,
                                fontSize = 17.sp,
                                color = primaryTextColor,
                                fontWeight = FontWeight.SemiBold
                            )

                            if (windowState == WindowState.SHOW_DUMP_HISTORY) {
                                Text(
                                    text = "Oldest dumps are replaced first.",
                                    fontSize = 12.sp,
                                    color = discreteTextColor
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(Color.Transparent, paneBorderColor, Color.Transparent)
                                )
                            )
                    )

                    if (windowState == WindowState.SHOW_DUMP_HISTORY) {
                        DumpHistoryList(historyList = dumpHistoryEntries)
                    }
                }
            }
        }
    }
}