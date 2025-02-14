package view.floatingWindow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import shared.Colors.elevatedBackgroundColor
import shared.Colors.paneBorderColor
import shared.Colors.primaryTextColor
import shared.Dimensions.paneBorderWidth
import view.button.RoundIconButton

@Composable
fun FloatingWindowLayer(
    floatingWindowState: FloatingWindowState,
    modifier: Modifier = Modifier
) {
    val windowState by floatingWindowState.windowState.collectAsState()

    if (windowState != WindowState.HIDDEN) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)) // Todo: store somewhere else.
                .clickable(
                    interactionSource = null,
                    indication = null,
                    onClick = floatingWindowState::closeFloatingWindow
                )
        ) {
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
                Column(modifier = Modifier.padding(10.dp)) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RoundIconButton(
                            onClick = floatingWindowState::closeFloatingWindow,
                            iconPainter = painterResource("icons/close.svg"),
                            buttonModifier = Modifier.align(Alignment.CenterStart)
                        )

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
                    }

                    Box(
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 10.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    listOf(Color.Transparent, paneBorderColor, Color.Transparent)
                                )
                            )
                    )

                    // Actual content of the floating window follows here:
                    // ...
                }
            }
        }
    }
}