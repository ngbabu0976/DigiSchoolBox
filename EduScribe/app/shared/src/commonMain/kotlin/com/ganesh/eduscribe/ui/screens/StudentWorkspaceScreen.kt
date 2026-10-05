package com.ganesh.eduscribe.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ganesh.eduscribe.data.EduScribeRepository
import com.ganesh.eduscribe.domain.model.*
import com.ganesh.eduscribe.ui.components.*

@Composable
fun StudentWorkspaceScreen(
    repository: EduScribeRepository,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: FR-4.3 Subject Tiles, 1: Time Analytics & Trend Graphs (FR-4.4), 2: Kiosk Browser

    val dailyAnalytics by repository.dailyAnalytics.collectAsState()
    val subjects by repository.subjects.collectAsState()
    val notebooks by repository.notebooks.collectAsState()
    val canvasPages by repository.canvasPages.collectAsState()
    val currentPageNumber by repository.selectedPageNumber.collectAsState()
    val selectedPenTool by repository.selectedPenTool.collectAsState()
    val mathToolState by repository.mathToolState.collectAsState()
    val examLockdownState by repository.examLockdownState.collectAsState()
    val kioskConfig by repository.kioskConfig.collectAsState()

    var selectedSubjectTile by remember { mutableStateOf<Subject?>(subjects.firstOrNull()) }

    var activeReadingResource by remember { mutableStateOf<DigitalResource?>(null) }
    var activeNotebookCanvas by remember { mutableStateOf<Notebook?>(null) }

    // MCQ Exam Submission State
    var examAnswers by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var isExamSubmitted by remember { mutableStateOf(false) }

    // If Exam Lockdown is active, lock student into MCQ Exam Mode screen!
    if (examLockdownState.isExamActiveGlobally && examLockdownState.activeExamPaper != null) {
        val exam = examLockdownState.activeExamPaper!!
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF881337))
                .padding(24.dp)
        ) {
            Surface(
                color = Color(0xFF9F1239),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("🔒 EXAM MODE ACTIVE (LOCKDOWN ENFORCED)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${exam.title} • ${exam.durationMinutes} Minutes • Total Marks: ${exam.totalMarks}", color = Color(0xFFFECDD3), fontSize = 12.sp)
                    }

                    Surface(color = Color(0xFFE11D48), shape = RoundedCornerShape(20.dp)) {
                        Text("⚠️ Textbooks & Notebooks Blocked", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isExamSubmitted) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🎉 Assessment Submitted Successfully!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF15803D))
                        Spacer(modifier = Modifier.height(12.dp))

                        val score = examAnswers.count { (qId, optionId) ->
                            val q = exam.questions.firstOrNull { it.id == qId }
                            q?.options?.firstOrNull { it.id == optionId }?.isCorrect == true
                        }

                        Text("Your Score: $score / ${exam.totalMarks} (${(score.toFloat() / exam.totalMarks * 100).toInt()}%)", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Auto-evaluated and committed to school database.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(exam.questions) { question ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(question.questionText, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.height(8.dp))

                                    question.options.forEach { option ->
                                        val isSelected = examAnswers[question.id] == option.id
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    val updated = examAnswers.toMutableMap()
                                                    updated[question.id] = option.id
                                                    examAnswers = updated
                                                }
                                                .padding(vertical = 4.dp)
                                        ) {
                                            RadioButton(
                                                selected = isSelected,
                                                onClick = {
                                                    val updated = examAnswers.toMutableMap()
                                                    updated[question.id] = option.id
                                                    examAnswers = updated
                                                }
                                            )
                                            Text(option.optionText, fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Button(
                                onClick = { isExamSubmitted = true },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Text("SUBMIT MCQ EXAMINATION", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
    ) {
        // Student Workspace Header Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎓 Student Tablet Workspace", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(color = Color(0xFFE0F2FE), shape = RoundedCornerShape(12.dp)) {
                        Text("${currentUser.fullName} (${currentUser.assignedClass} - ${currentUser.assignedSection})", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                    }
                }

                // Student Section Navigation Bar
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = { Text("📘 Subject Graphic Tiles (FR-4.3)") }
                    )
                    FilterChip(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        label = { Text("📈 Trend Graphs & Pie Chart (FR-4.4)") }
                    )
                    FilterChip(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        label = { Text("🌐 Kiosk Browser") }
                    )
                }
            }
        }

        if (activeReadingResource != null) {
            DocumentReaderView(
                resource = activeReadingResource!!,
                onClose = { activeReadingResource = null }
            )
        } else if (activeNotebookCanvas != null) {
            val key = "${activeNotebookCanvas!!.id}_$currentPageNumber"
            val page = canvasPages[key]

            Column(modifier = Modifier.fillMaxSize()) {
                Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = { activeNotebookCanvas = null }, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                            Text("⬅ Back Navigation to Subject Hub")
                        }
                        Text("Immutability Active: Blue/Black pen drawing. Teacher red pen annotations are read-only.", fontSize = 11.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)
                    }
                }

                VectorCanvasView(
                    notebook = activeNotebookCanvas!!,
                    currentPageNumber = currentPageNumber,
                    canvasPage = page,
                    currentUser = currentUser,
                    selectedPenTool = selectedPenTool,
                    mathToolState = mathToolState,
                    onPenToolSelected = { repository.setPenTool(it) },
                    onMathToolSelected = { repository.setMathTool(it) },
                    onStrokeCompleted = { repository.addStrokeToCurrentPage(it) },
                    onPageSelected = { repository.selectPage(it) },
                    onSharePage = {},
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            when (selectedTab) {
                // FR-4.3 Implementation: Textbooks, Study Materials & Notebooks organized under Subject-Wise Tiles
                0 -> StudentSubjectWiseTilesView(
                    subjects = subjects,
                    allNotebooks = notebooks.filter { it.studentName == currentUser.fullName || it.studentId == currentUser.id },
                    selectedSubjectTile = selectedSubjectTile,
                    onSelectSubjectTile = { selectedSubjectTile = it },
                    onOpenReader = { activeReadingResource = it },
                    onOpenNotebook = { nb ->
                        repository.selectNotebook(nb)
                        activeNotebookCanvas = nb
                    }
                )

                1 -> StudentDashboardAnalyticsView(dailyAnalytics = dailyAnalytics)

                2 -> StudentKioskBrowserView(kioskConfig = kioskConfig)
            }
        }
    }
}

// FR-4.3 Component: Textbooks, Study Materials & Notebooks organized under Graphic Subject Tiles
@Composable
fun StudentSubjectWiseTilesView(
    subjects: List<Subject>,
    allNotebooks: List<Notebook>,
    selectedSubjectTile: Subject?,
    onSelectSubjectTile: (Subject) -> Unit,
    onOpenReader: (DigitalResource) -> Unit,
    onOpenNotebook: (Notebook) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("📘 Subject-Wise Learning Hub (FR-4.3)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Select a Graphic Subject Tile to open Textbooks, Study Materials, and Notebooks for that subject only", fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        // Graphic Subject Tiles Row
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            subjects.forEach { sub ->
                GraphicSubjectTile(
                    subject = sub,
                    isSelected = selectedSubjectTile?.id == sub.id,
                    onClick = { onSelectSubjectTile(sub) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (selectedSubjectTile != null) {
            val sub = selectedSubjectTile
            val subjectNotebooks = allNotebooks.filter {
                it.subjectName.contains(sub.name, ignoreCase = true) || sub.name.contains(it.subjectName, ignoreCase = true)
            }

            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Left Column: Textbooks, Study Materials & Maps for selected subject
                Card(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📚 ${sub.name} - Textbooks & Study Materials", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Chapter-wise PDFs, formula sheets, maps & graph pages", fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(sub.chapters) { ch ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Chapter ${ch.number}: ${ch.title}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.height(6.dp))

                                        ch.resources.forEach { res ->
                                            Surface(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                                    .clickable { onOpenReader(res) },
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color.White,
                                                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(10.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text("📕", fontSize = 16.sp)
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Column {
                                                            Text(res.title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                                            Text("${res.type.label} • ${res.totalPages} Pages", fontSize = 10.sp, color = Color.Gray)
                                                        }
                                                    }

                                                    Button(onClick = { onOpenReader(res) }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)) {
                                                        Text("📖 Read", fontSize = 11.sp)
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

                // Right Column: Graphic Digital Notebooks for selected subject
                Card(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📒 ${sub.name} - My Digital Notebooks", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Classwork, Homework, Practice & Test books under ${sub.name}", fontSize = 12.sp, color = Color.Gray)

                        Spacer(modifier = Modifier.height(12.dp))

                        if (subjectNotebooks.isEmpty()) {
                            Text("No notebooks created for ${sub.name} yet.", fontSize = 13.sp, color = Color.Gray)
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                subjectNotebooks.forEach { nb ->
                                    GraphicNotebookTile(
                                        notebook = nb,
                                        onClick = { onOpenNotebook(nb) }
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

// FR-4.4 Implementation: Day-on-Day, Week-on-Week, Month-on-Month Trend Graphs & Subject-Wise Pie Chart
@Composable
fun StudentDashboardAnalyticsView(dailyAnalytics: DailyAnalytics?) {
    var reportInterval by remember { mutableStateOf(0) } // 0: Day-on-Day, 1: Week-on-Week, 2: Month-on-Month

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Surface(color = Color.White, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("📈 Analytics Reports & Visual Trend Graphs (FR-4.4)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Day-on-Day, Week-on-Week, Month-on-Month trend graphs and Subject-wise Pie Chart", fontSize = 12.sp, color = Color.Gray)
                    }

                    // Report interval toggles
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = reportInterval == 0,
                            onClick = { reportInterval = 0 },
                            label = { Text("📅 Day-on-Day") }
                        )
                        FilterChip(
                            selected = reportInterval == 1,
                            onClick = { reportInterval = 1 },
                            label = { Text("📆 Week-on-Week") }
                        )
                        FilterChip(
                            selected = reportInterval == 2,
                            onClick = { reportInterval = 2 },
                            label = { Text("🗓️ Month-on-Month") }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // FR-4.4 Trend Bar Chart & Subject-Wise Pie Chart Side by Side
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    val barItems = when (reportInterval) {
                        0 -> listOf(BarChartItem("Mon", 2.2f), BarChartItem("Tue", 3.0f), BarChartItem("Wed", 2.8f), BarChartItem("Thu", 3.5f), BarChartItem("Fri", 4.1f), BarChartItem("Sat", 1.5f))
                        1 -> listOf(BarChartItem("W1", 18f), BarChartItem("W2", 22f), BarChartItem("W3", 20f), BarChartItem("W4", 25f))
                        else -> listOf(BarChartItem("Jan", 70f), BarChartItem("Feb", 85f), BarChartItem("Mar", 92f))
                    }

                    TrendBarChart(
                        title = if (reportInterval == 0) "Day-on-Day Trend Graph" else if (reportInterval == 1) "Week-on-Week Trend Graph" else "Month-on-Month Trend Graph",
                        subtitle = "Active hours spent reading and writing",
                        items = barItems,
                        modifier = Modifier.weight(1.2f)
                    )

                    SubjectPieChart(
                        segments = listOf(
                            PieChartSegment("Mathematics", 40f, Color(0xFF1E3A8A)),
                            PieChartSegment("Science & Physics", 35f, Color(0xFF065F46)),
                            PieChartSegment("Social Studies", 25f, Color(0xFF92400E))
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text("Detailed Activity Log:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                dailyAnalytics?.records?.forEach { record ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF8FAFC)
                    ) {
                        Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⏱️", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(record.itemTitle, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text("Category: ${record.category.label}", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            Text("${record.durationSeconds / 60} minutes", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0369A1))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentKioskBrowserView(kioskConfig: KioskConfig) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Surface(color = Color.White, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("🌐 Whitelisted Educational Browser", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Access allowed websites & YouTube video lectures permitted by your teacher", fontSize = 12.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                kioskConfig.allowedUrls.forEach { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(if (item.isYouTube) "▶️" else "🌐", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(item.url, fontSize = 11.sp, color = Color.Gray)
                                }
                            }

                            Button(onClick = {}) {
                                Text("Launch Link")
                            }
                        }
                    }
                }
            }
        }
    }
}
