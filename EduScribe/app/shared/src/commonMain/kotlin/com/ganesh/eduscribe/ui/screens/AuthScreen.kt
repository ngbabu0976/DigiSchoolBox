package com.ganesh.eduscribe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ganesh.eduscribe.data.EduScribeRepository
import com.ganesh.eduscribe.domain.model.UserRole

@Composable
fun AuthScreen(
    repository: EduScribeRepository,
    onLoginSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("student") }
    var password by remember { mutableStateOf("password") }
    var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }

    val schoolConfig by repository.schoolConfig.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(520.dp)
                .wrapContentHeight()
                .padding(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Logo Title
                Text(
                    text = "✏️ EduScribe",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8)
                )

                Text(
                    text = schoolConfig.schoolName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF94A3B8)
                )

                Text(
                    text = "School Management & Digital Learning Ecosystem",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Role Selector Presets (Quick Demo Access)
                Text(
                    text = "Select Role / Quick Preset Login:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFCBD5E1),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    UserRole.entries.forEach { role ->
                        val isSelected = selectedRole == role
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp)
                                .clickable {
                                    selectedRole = role
                                    username = role.name.lowercase()
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFF0284C7) else Color(0xFF334155)
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val icon = when (role) {
                                    UserRole.ADMIN -> "⚙️"
                                    UserRole.PRINCIPAL -> "🏛️"
                                    UserRole.TEACHER -> "👩‍🏫"
                                    UserRole.STUDENT -> "🎓"
                                    UserRole.PARENT -> "👨‍👩‍👧"
                                }
                                Text(icon, fontSize = 16.sp)
                                Text(
                                    role.displayName.take(7),
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Username Input
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username / Student ID", color = Color(0xFF94A3B8)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF475569)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password Input
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = Color(0xFF94A3B8)) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF38BDF8),
                        unfocusedBorderColor = Color(0xFF475569)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Login Action Button
                Button(
                    onClick = {
                        repository.loginAs(selectedRole)
                        onLoginSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "LOGIN TO ${selectedRole.displayName.uppercase()} WORKSPACE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Compatible with Android 9+ (Pie, API 28+) 10-Inch Tablets & Web",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
