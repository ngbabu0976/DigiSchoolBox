package com.ganesh.eduscribe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ganesh.eduscribe.data.EduScribeRepository
import com.ganesh.eduscribe.domain.model.SchoolConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    repository: EduScribeRepository,
    modifier: Modifier = Modifier
) {
    val config by repository.schoolConfig.collectAsState()

    var schoolName by remember(config) { mutableStateOf(config.schoolName) }
    var state by remember(config) { mutableStateOf(config.state) }
    var region by remember(config) { mutableStateOf(config.region) }
    var branchName by remember(config) { mutableStateOf(config.branchName) }
    var headerText by remember(config) { mutableStateOf(config.headerText) }
    var footerText by remember(config) { mutableStateOf(config.footerText) }

    var importStatusMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Admin Header Banner
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
                        text = "⚙️ Admin & Principal Control Panel",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Manage Institution Settings, Bulk Ingestion, Curriculums, and Ecosystem Analytics",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                Button(
                    onClick = {
                        repository.updateSchoolConfig(
                            SchoolConfig(
                                schoolName = schoolName,
                                state = state,
                                region = region,
                                branchName = branchName,
                                headerText = headerText,
                                footerText = footerText
                            )
                        )
                        importStatusMessage = "School Configuration updated successfully!"
                    }
                ) {
                    Text("💾 Save Configuration")
                }
            }
        }

        if (importStatusMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✅ $importStatusMessage", color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = { importStatusMessage = null }) {
                        Text("✖", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Module 2.1: School Configuration Form
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🏫 School & Branch Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = schoolName,
                        onValueChange = { schoolName = it },
                        label = { Text("School / Institution Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = state,
                            onValueChange = { state = it },
                            label = { Text("State") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = region,
                            onValueChange = { region = it },
                            label = { Text("Region / City") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = branchName,
                        onValueChange = { branchName = it },
                        label = { Text("Branch / Campus Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = headerText,
                        onValueChange = { headerText = it },
                        label = { Text("Official Document Header Text") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = footerText,
                        onValueChange = { footerText = it },
                        label = { Text("Official Footer Text & Accreditation") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            // Module 2.2: Bulk Data Ingestion (Excel & ZIP Import)
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📊 Bulk Data Ingestion Engine", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Import Excel student rosters & ZIP textbook packages", fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("📄 Excel Student & Teacher Batch Import", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Upload .xlsx file containing Student Names, Roll Numbers, Classes, Sections & Parent Contacts.", fontSize = 11.sp, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val count = repository.simulateExcelImportStudents()
                                    importStatusMessage = "Successfully imported $count student and teacher profiles from Excel file!"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                            ) {
                                Text("📁 Upload & Process Excel (.xlsx)", fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE4E6)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("📦 ZIP Textbook & Material Package Ingestion", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Upload subject ZIP containing Class-wise PDFs, Social Maps, and Graph Pages.", fontSize = 11.sp, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    val count = repository.simulateZipImportTextbooks()
                                    importStatusMessage = "Successfully unzipped and cataloged $count textbook and study material PDFs!"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48))
                            ) {
                                Text("📦 Upload & Extract ZIP Package", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Module 2.3: Supplementary Media & Ecosystem Summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🗺️ Supplementary Assets & Asset Library", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AssistChip(
                        onClick = { importStatusMessage = "Social World Maps catalog updated!" },
                        label = { Text("🗺️ Social World Maps (3 HD Layers)") }
                    )
                    AssistChip(
                        onClick = { importStatusMessage = "Graph Pages template updated!" },
                        label = { Text("📐 Math Graph Pages (1mm & 5mm Grid)") }
                    )
                    AssistChip(
                        onClick = { importStatusMessage = "Physics & Optics Diagrams updated!" },
                        label = { Text("🔬 Science Diagram Worksheets") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(16.dp))

                Text("📈 System Deployment & Tablet Hardware Overview", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Target Tablets", fontSize = 11.sp, color = Color.Gray)
                        Text("10-Inch Android Fleet", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column {
                        Text("Min SDK Version", fontSize = 11.sp, color = Color.Gray)
                        Text("Android 9.0 (API 28)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column {
                        Text("Active Students", fontSize = 11.sp, color = Color.Gray)
                        Text("1,240 Enrolled", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Column {
                        Text("Teachers / Staff", fontSize = 11.sp, color = Color.Gray)
                        Text("64 Active Teachers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
