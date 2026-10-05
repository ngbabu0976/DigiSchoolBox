package com.ganesh.eduscribe.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BarChartItem(val label: String, val valueHours: Float)
data class PieChartSegment(val name: String, val percentage: Float, val color: Color)

@Composable
fun TrendBarChart(
    title: String,
    subtitle: String,
    items: List<BarChartItem>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(subtitle, fontSize = 11.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            val maxValue = (items.maxOfOrNull { it.valueHours } ?: 1f).coerceAtLeast(1f)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                items.forEach { item ->
                    val barHeightRatio = (item.valueHours / maxValue).coerceIn(0.1f, 1.0f)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${item.valueHours}h",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.5f)
                                .fillMaxHeight(barHeightRatio * 0.8f)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = item.label,
                            fontSize = 10.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubjectPieChart(
    segments: List<PieChartSegment>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📊 Subject-Wise Time Proportion (Pie Chart)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text("Breakdown of study time distribution across subjects", fontSize = 11.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Canvas Canvas Pie Chart Drawing
                Canvas(modifier = Modifier.size(140.dp)) {
                    var startAngle = -90f
                    val total = segments.sumOf { it.percentage.toDouble() }.toFloat()

                    segments.forEach { segment ->
                        val sweepAngle = (segment.percentage / (if (total == 0f) 1f else total)) * 360f
                        drawArc(
                            color = segment.color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            useCenter = true,
                            size = Size(size.width, size.height)
                        )
                        startAngle += sweepAngle
                    }

                    // Inner White Circle for Donut Ring Effect
                    drawCircle(
                        color = Color.White,
                        radius = size.width * 0.28f,
                        center = Offset(size.width / 2, size.height / 2)
                    )
                }

                // Legend
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    segments.forEach { segment ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(segment.color)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${segment.name}: ${segment.percentage.toInt()}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
