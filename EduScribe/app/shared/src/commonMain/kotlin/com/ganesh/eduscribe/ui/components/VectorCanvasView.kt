package com.ganesh.eduscribe.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ganesh.eduscribe.domain.model.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VectorCanvasView(
    notebook: Notebook,
    currentPageNumber: Int,
    canvasPage: CanvasPage?,
    currentUser: User,
    selectedPenTool: PenTool,
    mathToolState: MathToolState,
    onPenToolSelected: (PenTool) -> Unit,
    onMathToolSelected: (MathToolType) -> Unit,
    onStrokeCompleted: (List<StrokePoint>) -> Unit,
    onPageSelected: (Int) -> Unit,
    onSharePage: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentDragPoints by remember { mutableStateOf<List<StrokePoint>>(emptyList()) }
    var showGitDiffDrawer by remember { mutableStateOf(false) }
    var isFullScreenMode by remember { mutableStateOf(false) }

    val allowedPenTools = PenTool.entries.filter { currentUser.role in it.allowedRoles }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8))
    ) {
        // Canvas Toolbar Header
        Surface(
            shadowElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Title & Page Controls
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            text = notebook.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${notebook.subjectName} • ${notebook.bookType.displayName} (${notebook.ruleType.displayName})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Page Navigation Buttons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = { if (currentPageNumber > 1) onPageSelected(currentPageNumber - 1) },
                            enabled = currentPageNumber > 1
                        ) {
                            Text("◀", fontSize = 14.sp)
                        }

                        Text(
                            text = "Page $currentPageNumber / ${notebook.totalPages}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(
                            onClick = { if (currentPageNumber < notebook.totalPages) onPageSelected(currentPageNumber + 1) },
                            enabled = currentPageNumber < notebook.totalPages
                        ) {
                            Text("▶", fontSize = 14.sp)
                        }
                    }
                }

                // Pen Palette Selector
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Pen:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))

                    allowedPenTools.forEach { tool ->
                        val isSelected = selectedPenTool == tool
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (isSelected) 36.dp else 28.dp)
                                .clip(CircleShape)
                                .background(if (tool.isEraser) Color.LightGray else Color(tool.defaultColorHex))
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                    shape = CircleShape
                                )
                                .clickable { onPenToolSelected(tool) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (tool.isEraser) {
                                Text("🧹", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Math Tools & Git Diffs Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = mathToolState.activeTool != MathToolType.NONE,
                        onClick = {
                            val nextTool = when (mathToolState.activeTool) {
                                MathToolType.NONE -> MathToolType.RULER
                                MathToolType.RULER -> MathToolType.PROTRACTOR
                                MathToolType.PROTRACTOR -> MathToolType.COMPASS
                                MathToolType.COMPASS -> MathToolType.NONE
                            }
                            onMathToolSelected(nextTool)
                        },
                        label = { Text("📐 Math: ${mathToolState.activeTool.displayName}") }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = { isFullScreenMode = !isFullScreenMode },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(if (isFullScreenMode) "📺 Exit Full Screen" else "📺 Full Screen", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = { showGitDiffDrawer = !showGitDiffDrawer },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("📜 Commit Diffs (${canvasPage?.commits?.size ?: 0})", fontSize = 12.sp)
                    }

                    if (currentUser.role == UserRole.TEACHER || currentUser.role == UserRole.PRINCIPAL) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onSharePage,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("📢 Share Page", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Main Canvas & Side Panel Row
        Row(modifier = Modifier.fillMaxSize()) {
            // Main Drawing Canvas Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            ) {
                // Layer 1: Notebook Ruled Lines Background
                RuleCanvasBackground(ruleType = notebook.ruleType)

                // Layer 2: Vector Strokes & Active Drawing Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(selectedPenTool, currentUser) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentDragPoints = listOf(StrokePoint(offset.x, offset.y))
                                },
                                onDrag = { change, _ ->
                                    change.consume()
                                    currentDragPoints = currentDragPoints + StrokePoint(change.position.x, change.position.y)
                                },
                                onDragEnd = {
                                    if (currentDragPoints.isNotEmpty()) {
                                        onStrokeCompleted(currentDragPoints)
                                        currentDragPoints = emptyList()
                                    }
                                }
                            )
                        }
                ) {
                    // Render Saved Vector Strokes
                    canvasPage?.strokes?.forEach { stroke ->
                        if (stroke.points.size > 1) {
                            val path = Path().apply {
                                moveTo(stroke.points[0].x, stroke.points[0].y)
                                for (i in 1 until stroke.points.size) {
                                    lineTo(stroke.points[i].x, stroke.points[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = Color(stroke.colorHex),
                                style = Stroke(width = stroke.strokeWidth)
                            )
                        }
                    }

                    // Render Active Touch/Stylus Drag Stroke
                    if (currentDragPoints.size > 1) {
                        val activePath = Path().apply {
                            moveTo(currentDragPoints[0].x, currentDragPoints[0].y)
                            for (i in 1 until currentDragPoints.size) {
                                lineTo(currentDragPoints[i].x, currentDragPoints[i].y)
                            }
                        }
                        drawPath(
                            path = activePath,
                            color = if (selectedPenTool.isEraser) Color.Gray else Color(selectedPenTool.defaultColorHex),
                            style = Stroke(width = selectedPenTool.strokeWidthPx)
                        )
                    }

                    // Layer 3: Interactive Math Tools Overlay (Scale, Protractor, Compass)
                    when (mathToolState.activeTool) {
                        MathToolType.RULER -> {
                            // Render Digital Scale / Ruler Overlay
                            val startX = 100f
                            val startY = 150f
                            val rulerWidth = 600f
                            val rulerHeight = 100f

                            drawRect(
                                color = Color(0xDDFFF8E1),
                                topLeft = Offset(startX, startY),
                                size = Size(rulerWidth, rulerHeight)
                            )
                            drawRect(
                                color = Color.DarkGray,
                                topLeft = Offset(startX, startY),
                                size = Size(rulerWidth, rulerHeight),
                                style = Stroke(2f)
                            )

                            // Cm Ticks
                            val cmSpacing = 40f
                            var cm = 0
                            var x = startX
                            while (x <= startX + rulerWidth) {
                                drawLine(
                                    color = Color.Black,
                                    start = Offset(x, startY),
                                    end = Offset(x, startY + (if (cm % 5 == 0) 30f else 15f)),
                                    strokeWidth = 2f
                                )
                                cm++
                                x += cmSpacing / 2
                            }
                        }

                        MathToolType.PROTRACTOR -> {
                            // Render Semi-circular Protractor Overlay
                            val center = Offset(400f, 300f)
                            val radius = 220f

                            drawArc(
                                color = Color(0xCCF0F4C3),
                                startAngle = 180f,
                                sweepAngle = 180f,
                                useCenter = true,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2)
                            )
                            drawArc(
                                color = Color.DarkGray,
                                startAngle = 180f,
                                sweepAngle = 180f,
                                useCenter = true,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(2f)
                            )

                            // Degree Ticks (0° to 180°)
                            for (deg in 0..180 step 15) {
                                val rad = (deg + 180) * (3.14159f / 180f)
                                val tickStart = Offset(center.x + (radius - 20) * cos(rad), center.y + (radius - 20) * sin(rad))
                                val tickEnd = Offset(center.x + radius * cos(rad), center.y + radius * sin(rad))
                                drawLine(Color.Black, tickStart, tickEnd, 1.5f)
                            }
                        }

                        MathToolType.COMPASS -> {
                            // Render Digital Compass Arc Tool Overlay
                            val anchor = Offset(300f, 300f)
                            val compassRadius = 180f

                            drawCircle(color = Color.Red, radius = 8f, center = anchor)
                            drawCircle(color = Color(0xFF0288D1), radius = compassRadius, center = anchor, style = Stroke(1.5f))
                        }

                        MathToolType.NONE -> {}
                    }
                }
            }

            // Side Drawer: Git-Like Differential Commit Timeline
            AnimatedVisibility(visible = showGitDiffDrawer) {
                Card(
                    modifier = Modifier
                        .width(300.dp)
                        .fillMaxHeight()
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Git-Like Stroke Diffs", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            IconButton(onClick = { showGitDiffDrawer = false }) {
                                Text("✖", fontSize = 12.sp)
                            }
                        }

                        Text(
                            "Incremental diff history for page $currentPageNumber. Saves memory & vector revisions.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val commits = canvasPage?.commits ?: emptyList()
                        if (commits.isEmpty()) {
                            Text("No commits recorded yet.", fontSize = 12.sp, color = Color.Gray)
                        } else {
                            commits.reversed().forEach { commit ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    shadowElevation = 1.dp
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = commit.commitMessage,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "By ${commit.authorName} (${commit.authorRole.displayName})",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "+${commit.addedStrokes.size} strokes added",
                                            fontSize = 10.sp,
                                            color = Color(0xFF388E3C)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
