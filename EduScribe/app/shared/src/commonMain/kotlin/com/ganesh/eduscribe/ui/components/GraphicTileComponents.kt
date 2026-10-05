package com.ganesh.eduscribe.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ganesh.eduscribe.domain.model.*

@Composable
fun GraphicSubjectTile(
    subject: Subject,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientColors = when {
        subject.name.contains("Math", ignoreCase = true) -> listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6))
        subject.name.contains("Science", ignoreCase = true) -> listOf(Color(0xFF065F46), Color(0xFF10B981))
        subject.name.contains("Social", ignoreCase = true) -> listOf(Color(0xFF92400E), Color(0xFFF59E0B))
        else -> listOf(Color(0xFF4C1D95), Color(0xFF8B5CF6))
    }

    Card(
        modifier = modifier
            .width(260.dp)
            .height(140.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 2.dp),
        border = if (isSelected) BorderStroke(3.dp, Color(0xFF38BDF8)) else null,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(gradientColors))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    ) {
                        Text(
                            text = when {
                                subject.name.contains("Math", ignoreCase = true) -> "📐"
                                subject.name.contains("Science", ignoreCase = true) -> "🔬"
                                subject.name.contains("Social", ignoreCase = true) -> "🗺️"
                                else -> "📚"
                            },
                            modifier = Modifier.padding(10.dp),
                            fontSize = 22.sp
                        )
                    }

                    Surface(
                        color = Color.White.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${subject.chapters.size} Chapters",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column {
                    Text(
                        text = subject.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "${subject.classes.size} Enrolled Classes",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun GraphicClassTile(
    schoolClass: SchoolClass,
    isSelected: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(220.dp)
            .height(120.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) Color(0xFF0284C7) else Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFE0F2FE),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("🏫", modifier = Modifier.padding(12.dp), fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = schoolClass.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${schoolClass.sections.size} Sections",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0369A1)
                    )
                }
            }
        }
    }
}

@Composable
fun GraphicSectionTile(
    section: Section,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(220.dp)
            .height(120.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFDCFCE7),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("👥", modifier = Modifier.padding(12.dp), fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = section.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${section.studentCount} Students",
                    fontSize = 11.sp,
                    color = Color(0xFF15803D),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun GraphicNotebookTile(
    notebook: Notebook,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bookColor = when (notebook.bookType) {
        BookType.CLASSWORK -> Color(0xFF0284C7)
        BookType.HOMEWORK -> Color(0xFFD97706)
        BookType.PRACTICE -> Color(0xFF16A34A)
        BookType.TEST -> Color(0xFFDC2626)
    }

    Card(
        modifier = modifier
            .width(260.dp)
            .height(260.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = bookColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📓", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = notebook.bookType.displayName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = bookColor
                            )
                        }
                    }

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = notebook.ruleType.displayName,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 9.sp,
                            color = Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = notebook.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0F172A)
                )

                Text(
                    text = "Student: ${notebook.studentName}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (notebook.isEvaluated) {
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Grade: ${notebook.teacherGrade} (${notebook.teacherFeedback})",
                            modifier = Modifier.padding(6.dp),
                            fontSize = 10.sp,
                            color = Color(0xFF15803D),
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = "⚠️ Pending Teacher Correction",
                        fontSize = 11.sp,
                        color = Color(0xFFD97706),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = bookColor)
            ) {
                Text("✏️ Open Notebook Canvas", fontSize = 12.sp)
            }
        }
    }
}
