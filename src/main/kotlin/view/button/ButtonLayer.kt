package view.button

import BuildConfig
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import io.github.z4kn4fein.semver.toVersion
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import java.awt.Cursor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import shared.Colors
import shared.Colors.paneBorderColor
import shared.Dimensions.extractButtonDiameter
import shared.Dimensions.mediumCornerRadius
import shared.Dimensions.mediumPadding
import shared.Dimensions.paneBorderWidth
import shared.Dimensions.smallPadding
import view.FadeVisibility
import view.UiLayoutState
import view.button.displayControl.DisplayControlPill
import view.button.extraction.ExtractionPill
import view.utils.toPx

@Composable
fun ButtonLayer(
    uiLayoutState: UiLayoutState,
    buttonState: ButtonState,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current.density

    val showDumpSuggestion by buttonState.showDumpSuggestion.collectAsState(initial = true)
    val dumpSuggestionText = buttonState.dumpSuggestionText

    val showCurrentDump by buttonState.showCurrentDump.collectAsState(initial = false)
    val currentDumpInfo by buttonState.currentDumpInfo.collectAsState(initial = "...")

    val showDumpProgress by buttonState.showDumpProgress.collectAsState(initial = false)
    val dumpProgress by buttonState.dumpProgress.collectAsState()
    val dumpProgressText by buttonState.dumpProgressText.collectAsState()

    val areResizeButtonsEnabled by
        buttonState.areResizeButtonsEnabled.collectAsState(initial = false)
    val isExtractButtonEnabled by buttonState.isExtractButtonEnabled.collectAsState(initial = true)
    val isOpenDumpHistoryButtonEnabled by
        buttonState.isOpenDumpHistoryButtonEnabled.collectAsState(initial = false)

    val areDisplayControlButtonsEnabled by
        buttonState.areDisplayControlButtonsEnabled.collectAsState(initial = false)
    val displayCounter by buttonState.displayCounter.collectAsState(initial = "?/?")

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(mediumPadding),
        modifier = modifier.padding(mediumPadding),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(mediumPadding),
        ) {
            ExtractionPill(
                showDumpSuggestion = showDumpSuggestion,
                dumpSuggestionText = dumpSuggestionText,
                showCurrentDump = showCurrentDump,
                currentDumpInfo = currentDumpInfo,
                showDumpProgress = showDumpProgress,
                dumpProgress = dumpProgress,
                dumpProgressText = dumpProgressText,
                isExtractButtonEnabled = isExtractButtonEnabled,
                onExtractButtonPressed = buttonState::onExtractButtonPressed,
            )

            RoundIconButton(
                onClick = buttonState::onOpenDumpHistoryButtonPressed,
                enabled = isOpenDumpHistoryButtonEnabled,
                iconPainter = painterResource("icons/history.svg"),
            )

            DisplayControlPill(
                areDisplayControlButtonsEnabled = areDisplayControlButtonsEnabled,
                displayCounter = displayCounter,
                onNextDisplayButtonPressed = buttonState::onNextDisplayButtonPressed,
                onPreviousDisplayButtonPressed = buttonState::onPreviousDisplayButtonPressed,
            )
        }

        // This is needed for the elements that follow to be as low on the screen as possible.
        Spacer(modifier = Modifier.weight(1f))

        UpdateRoundIconButton()

        RoundIconButton(
            onClick = { uiLayoutState.onFitScreenshotToScreenButtonPressed(density) },
            enabled = areResizeButtonsEnabled,
            iconPainter = painterResource("icons/fit.svg"),
        )

        RoundIconButton(
            onClick = uiLayoutState::onEnlargeScreenshotButtonPressed,
            enabled = areResizeButtonsEnabled,
            iconPainter = painterResource("icons/enlarge.svg"),
        )

        RoundIconButton(
            onClick = uiLayoutState::onShrinkScreenshotButtonPressed,
            enabled = areResizeButtonsEnabled,
            iconPainter = painterResource("icons/shrink.svg"),
        )
    }
}

@Composable
fun UpdateRoundIconButton(modifier: Modifier = Modifier) {
    val colorPair = Colors.extractionButtonColor to Colors.vibrantAccentColor

    /**
     * Interpolates the paired colors. [value] is between `-1f` and `1f` and represents the
     * interpolation weight.
     */
    fun Pair<Color, Color>.getInterpolatedColor(value: Float): Color {
        val clamped = value.coerceIn(-1f, 1f)
        val transformed = (clamped + 1f) / 2f
        return lerp(this.first, this.second, transformed)
    }

    var updateAvailable by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val client = HttpClient(CIO) { install(ContentNegotiation) { json() } }

        val latestReleaseUrl =
            "https://api.github.com/repos/vladcudoidem/Schaumamal/releases/latest"

        val responseObject =
            try {
                val response: String =
                    client.get(latestReleaseUrl) { accept(ContentType.Application.Json) }.body()
                Json.parseToJsonElement(response)
            } catch (e: Exception) {
                return@LaunchedEffect
            } finally {
                client.close()
            }
        val latestVersion =
            responseObject.jsonObject["name"]?.jsonPrimitive?.contentOrNull?.removePrefix("v")
                ?: return@LaunchedEffect

        try {
            if (latestVersion.toVersion() > BuildConfig.VERSION.toVersion()) {
                updateAvailable = true
            }
        } catch (_: Exception) {
            updateAvailable = false
        }
    }

    val transition = rememberInfiniteTransition()
    val virtualAngle by
        transition.animateFloat(
            initialValue = 0f,
            targetValue = 2 * PI.toFloat(),
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = 3000, easing = FastOutLinearInEasing),
                    repeatMode = RepeatMode.Restart,
                ),
        )
    val gradientBrush =
        Brush.linearGradient(
            colors =
                listOf(
                    colorPair.getInterpolatedColor(sin(virtualAngle)),
                    colorPair.getInterpolatedColor(cos(virtualAngle)),
                ),
            start = Offset.Zero,
            end = Offset.Infinite,
        )

    val localUriHandler = LocalUriHandler.current
    val latestReleaseLink = "https://github.com/vladcudoidem/Schaumamal/releases/latest"

    var showPopup by remember { mutableStateOf(false) }
    val density = LocalDensity.current.density

    FadeVisibility(updateAvailable) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(mediumPadding),
        ) {
            Box {
                RoundIconButton(
                    onClick = { showPopup = true },
                    enabled = true,
                    iconPainter = painterResource("icons/upgrade.svg"),
                    backgroundBrush = gradientBrush,
                    buttonModifier = modifier,
                )

                if (showPopup) {
                    Popup(
                        onDismissRequest = { showPopup = false },
                        alignment = Alignment.CenterStart,
                        offset = IntOffset((extractButtonDiameter * 1.2f).toPx(density).toInt(), 0),
                    ) {
                        Box(
                            modifier =
                                Modifier.clip(RoundedCornerShape(mediumCornerRadius))
                                    .background(Colors.elevatedBackgroundColor)
                                    .border(
                                        width = paneBorderWidth,
                                        color = paneBorderColor,
                                        shape = RoundedCornerShape(mediumCornerRadius),
                                    )
                        ) {
                            RoundIconButton(
                                onClick = { showPopup = false },
                                iconPainter = painterResource("icons/close.svg"),
                                buttonModifier =
                                    Modifier.align(Alignment.TopEnd)
                                        .padding(smallPadding)
                                        .size(25.dp),
                            )

                            Column(modifier = Modifier.padding(mediumPadding * 1.5f)) {
                                Text(
                                    text = "New release!",
                                    color = Colors.primaryTextColor,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = mediumPadding),
                                )

                                Text(
                                    text = "A new version of Schaumamal has been released!",
                                    color = Colors.primaryTextColor,
                                )

                                Row {
                                    Text(
                                        text = "Click here",
                                        color = Colors.vibrantAccentColor,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier =
                                            Modifier.clickable {
                                                    localUriHandler.openUri(latestReleaseLink)
                                                    showPopup = false
                                                }
                                                .pointerHoverIcon(
                                                    PointerIcon(Cursor(Cursor.HAND_CURSOR))
                                                ),
                                    )

                                    Text(
                                        text = " to check it out.",
                                        color = Colors.primaryTextColor,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            TinyHorizontalSpacer()
        }
    }
}

@Composable
fun TinyHorizontalSpacer(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .size(width = 20.dp, height = 2.dp)
                .clip(RoundedCornerShape(50))
                .background(paneBorderColor)
    )
}
