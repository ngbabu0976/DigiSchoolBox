package com.ganesh.eduscribe.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ganesh.eduscribe.data.EduScribeRepository
import com.ganesh.eduscribe.domain.model.User

@Composable
fun ParentWorkspaceScreen(
    repository: EduScribeRepository,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    val dailyAnalytics by repository.dailyAnalytics.collectAsState()
    val notebooks by repository.notebooks.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
    ) {
        // Parent Header
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "👨‍👩‍👧 Parent Progress Portal",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Monitoring Learning Analytics & Digital Notebook Progress for Aarav Patel (Class 10-A)",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                Surface(color = Color.White, shape = RoundedCornerShape(20.dp)) {
                    Text("Attendance: 98.5% (Present)", modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Left Card: Daily Learning Time Breakdown
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⏱️ Yesterday's Study Time Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Total study time recorded on student tablet: 2 Hours 50 Mins", fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    dailyAnalytics?.records?.forEach { record ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF8FAFC)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(record.itemTitle, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(record.category.label, fontSize = 11.sp, color = Color.Gray)
                                }
                                Text("${record.durationSeconds / 60} mins", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF0284C7))
                            }
                        }
                    }
                }
            }

            // Right Card: Teacher Graded Notebook Progress
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📝 Graded Notebooks & Teacher Feedback", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Review teacher corrections and red pen annotations", fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    notebooks.forEach { nb ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF8FAFC),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(nb.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(nb.bookType.displayName, fontSize = 11.sp, color = Color.Gray)
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                if (nb.isEvaluated) {
                                    Text("Grade: ${nb.teacherGrade} - \"${nb.teacherFeedback}\"", fontSize = 12.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                                } else {
                                    Text("Pending Teacher Evaluation", fontSize = 11.sp, color = Color(0xFFD97706))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
