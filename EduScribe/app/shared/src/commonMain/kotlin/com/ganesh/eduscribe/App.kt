package com.ganesh.eduscribe

import androidx.compose.foundation.BorderStroke
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
import com.ganesh.eduscribe.domain.model.UserRole
import com.ganesh.eduscribe.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val repository = remember { EduScribeRepository() }
    val currentUser by repository.currentUser.collectAsState()
    val schoolConfig by repository.schoolConfig.collectAsState()

    MaterialTheme {
        if (currentUser == null) {
            AuthScreen(
                repository = repository,
                onLoginSuccess = { }
            )
        } else {
            val user = currentUser!!

            Column(modifier = Modifier.fillMaxSize()) {
                // Global Ecosystem Top Bar
                Surface(
                    color = Color(0xFF0F172A),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // School Logo & Header Text
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✏️ EduScribe", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("•", color = Color.Gray)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(schoolConfig.schoolName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${schoolConfig.branchName}, ${schoolConfig.state}", color = Color(0xFF94A3B8), fontSize = 11.sp)
                            }
                        }

                        // Current Logged-in User Profile & Logout / Switch Role
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = when (user.role) {
                                    UserRole.ADMIN -> Color(0xFFDC2626)
                                    UserRole.PRINCIPAL -> Color(0xFF16A34A)
                                    UserRole.TEACHER -> Color(0xFF2563EB)
                                    UserRole.STUDENT -> Color(0xFF0284C7)
                                    UserRole.PARENT -> Color(0xFFD97706)
                                },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(user.fullName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("(${user.role.displayName})", color = Color(0xFFE2E8F0), fontSize = 10.sp)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // FR-1.1: Logout button in each login session
                            Button(
                                onClick = { repository.logout() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("🚪 Logout", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Main Workspace Routed Component
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    when (user.role) {
                        UserRole.ADMIN, UserRole.PRINCIPAL -> AdminDashboardScreen(repository = repository)
                        UserRole.TEACHER -> TeacherWorkspaceScreen(repository = repository, currentUser = user)
                        UserRole.STUDENT -> StudentWorkspaceScreen(repository = repository, currentUser = user)
                        UserRole.PARENT -> ParentWorkspaceScreen(repository = repository, currentUser = user)
                    }
                }
            }
        }
    }
}
