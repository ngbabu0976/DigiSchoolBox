package com.ganesh.eduscribe.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import com.ganesh.eduscribe.domain.model.RuleType

@Composable
fun RuleCanvasBackground(
    ruleType: RuleType,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        val width = size.width
        val height = size.height

        when (ruleType) {
            RuleType.SINGLE_LINE -> {
                // Left Red Margin Line
                drawLine(
                    color = Color(0xFFEF5350),
                    start = Offset(100f, 0f),
                    end = Offset(100f, height),
                    strokeWidth = 2f
                )
                // Horizontal Ruled Blue Lines
                val lineSpacing = 40f
                var y = 100f
                while (y < height) {
                    drawLine(
                        color = Color(0xFF90CAF9),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1.5f
                    )
                    y += lineSpacing
                }
            }

            RuleType.FOUR_LINE -> {
                // English handwriting 4-line rule (Red top line, 2 blue middle lines, Red bottom line)
                drawLine(
                    color = Color(0xFFEF5350),
                    start = Offset(80f, 0f),
                    end = Offset(80f, height),
                    strokeWidth = 2f
                )
                val groupSpacing = 90f
                var startY = 80f
                while (startY < height) {
                    // Top Red Line
                    drawLine(Color(0xFFEF5350), Offset(0f, startY), Offset(width, startY), 1.5f)
                    // Upper Middle Blue Line
                    drawLine(Color(0xFF42A5F5), Offset(0f, startY + 20f), Offset(width, startY + 20f), 1.2f)
                    // Lower Middle Blue Line
                    drawLine(Color(0xFF42A5F5), Offset(0f, startY + 40f), Offset(width, startY + 40f), 1.2f)
                    // Bottom Red Line
                    drawLine(Color(0xFFEF5350), Offset(0f, startY + 60f), Offset(width, startY + 60f), 1.5f)

                    startY += groupSpacing
                }
            }

            RuleType.SQUARE_GRID -> {
                // Math / Science Square Grid
                val gridSize = 40f
                var x = 0f
                while (x < width) {
                    drawLine(
                        color = Color(0xFFE0E0E0),
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = 1f
                    )
                    x += gridSize
                }
                var y = 0f
                while (y < height) {
                    drawLine(
                        color = Color(0xFFE0E0E0),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                    y += gridSize
                }
            }

            RuleType.BLANK -> {
                // Fine watermark for Blank notebook page
                drawLine(
                    color = Color(0xFFE0E0E0),
                    start = Offset(60f, 0f),
                    end = Offset(60f, height),
                    strokeWidth = 1f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }
        }
    }
}
