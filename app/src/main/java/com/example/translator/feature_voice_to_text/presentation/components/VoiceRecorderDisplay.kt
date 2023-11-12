package com.example.translator.feature_voice_to_text.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.translator.feature_translator.presentation.components.gradientSurface
import com.example.translator.ui.theme.TranslatorTheme
import kotlin.math.PI
import kotlin.math.sin


@Composable
fun VoiceRecorderDisplay(
    modifier: Modifier = Modifier,
    powerRatios: List<Float>
) {

    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .gradientSurface()
            .padding(
                horizontal = 32.dp,
                vertical = 8.dp
            )
            .drawBehind {
                // width of the bar
                val powerRadioWidth = 3.dp.toPx()
                // 2 * powerRadioWidth cuz we wanna create a gap between them
                // so for a single bar its going to be 6 dp in width, 3 with gap, 3 with out gap
                val powerRatioCount = (size.width / (2 * powerRadioWidth)).toInt()


                // topLeft -> x,y = 0 in the graph,
                // so thats why we have left and top in the clipRect = 0
                // we use clipRect cuz we wanna cut what its going beyond our "rect"
                clipRect(
                    left = 0f,
                    top = 0f,
                    right = size.width, // x -> coordinate
                    bottom = size.height // y -> coordinate
                ) {
                    powerRatios
                        .takeLast(powerRatioCount) // take the most up to date value
                        .reversed() // start from right and move to left
                        .forEachIndexed { index, ratio ->
                            val yTopStart = center.y - (size.height / 2f) * ratio

                            drawRoundRect(
                                color = primary,
                                topLeft = Offset(
                                    x = size.width - index * 2 * powerRadioWidth,
                                    y = yTopStart
                                ),
                                size = Size(
                                    width = powerRadioWidth,
                                    height = (center.y - yTopStart) * 2f
                                ),
                                cornerRadius = CornerRadius(100f)
                            )
                        }
                }
            }
    )
}

@Preview
@Composable
fun VoiceRecorderDisplayPreview() {
    TranslatorTheme {
        VoiceRecorderDisplay(
            powerRatios = (0..4).map {
                val percentageValue = it / 100f
                sin(percentageValue * 2 * PI).toFloat()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )
    }
}