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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ganesh.eduscribe.data.EduScribeRepository
import com.ganesh.eduscribe.domain.model.*
import com.ganesh.eduscribe.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherWorkspaceScreen(
    repository: EduScribeRepository,
    currentUser: User,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Class Hierarchy & Corrections, 1: FR-3.9 Class Analytics, 2: MCQ & Exam Controller, 3: Kiosk URLs

    val subjects by repository.subjects.collectAsState()
    val notebooks by repository.notebooks.collectAsState()
    val canvasPages by repository.canvasPages.collectAsState()
    val currentPageNumber by repository.selectedPageNumber.collectAsState()
    val selectedPenTool by repository.selectedPenTool.collectAsState()
    val mathToolState by repository.mathToolState.collectAsState()
    val examPapers by repository.examPapers.collectAsState()
    val examLockdownState by repository.examLockdownState.collectAsState()
    val kioskConfig by repository.kioskConfig.collectAsState()
    val dailyAnalytics by repository.dailyAnalytics.collectAsState()

    var activeReadingResource by remember { mutableStateOf<DigitalResource?>(null) }
    var activeAnnotatingNotebook by remember { mutableStateOf<Notebook?>(null) }

    // Navigation hierarchy state for FR-3.1 & FR-3.8
    var navStep by remember { mutableStateOf(0) } // 0: Select Subject, 1: Select Class, 2: Class Textbooks & Select Section, 3: Select Student, 4: Student Notebook Corrections
    var selectedSubjectNav by remember { mutableStateOf<Subject?>(subjects.firstOrNull()) }
    var selectedClassNav by remember { mutableStateOf<SchoolClass?>(null) }
    var selectedSectionNav by remember { mutableStateOf<Section?>(null) }
    var selectedStudentNav by remember { mutableStateOf<User?>(null) }

    // Dialog states
    var showCreateNotebookDialog by remember { mutableStateOf(false) }
    var showAddKioskUrlDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
    ) {
        // Teacher Workspace Header Bar
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
                    Text(
                        text = "👩‍🏫 Teacher & Principal Workspace",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "${currentUser.fullName} (${currentUser.assignedClass ?: "Class 10"} - ${currentUser.assignedSection ?: "Section A"})",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                // Workspace Section Tabs
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = { Text("🏫 Class Hierarchy & Corrections") }
                    )
                    FilterChip(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        label = { Text("📊 Class Analytics (FR-3.9)") }
                    )
                    FilterChip(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        label = { Text("📝 MCQ & Exam Controller") }
                    )
                    FilterChip(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        label = { Text("🔒 Kiosk URLs") }
                    )
                }
            }
        }

        if (activeReadingResource != null) {
            DocumentReaderView(
                resource = activeReadingResource!!,
                onClose = { activeReadingResource = null }
            )
        } else if (activeAnnotatingNotebook != null) {
            val key = "${activeAnnotatingNotebook!!.id}_$currentPageNumber"
            val currentPage = canvasPages[key]

            VectorCanvasView(
                notebook = activeAnnotatingNotebook!!,
                currentPageNumber = currentPageNumber,
                canvasPage = currentPage,
                currentUser = currentUser,
                selectedPenTool = selectedPenTool,
                mathToolState = mathToolState,
                onPenToolSelected = { repository.setPenTool(it) },
                onMathToolSelected = { repository.setMathTool(it) },
                onStrokeCompleted = { repository.addStrokeToCurrentPage(it) },
                onPageSelected = { repository.selectPage(it) },
                onSharePage = {
                    repository.sharePageWithClass(
                        notebookId = activeAnnotatingNotebook!!.id,
                        pageNumber = currentPageNumber,
                        studentName = activeAnnotatingNotebook!!.studentName,
                        subjectName = activeAnnotatingNotebook!!.subjectName,
                        targetSection = activeAnnotatingNotebook!!.sectionId
                    )
                },
                modifier = Modifier.weight(1f)
            )

            // Bottom Evaluation Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { activeAnnotatingNotebook = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    ) {
                        Text("⬅ Back Navigation to Student List")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Annotating with Teacher Red Pen. Student cannot modify red/green strokes.", fontSize = 12.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = {
                                repository.evaluateNotebook(activeAnnotatingNotebook!!.id, "A+", "Good step-by-step working!")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) {
                            Text("✔ Mark Evaluated (A+)")
                        }
                    }
                }
            }
        } else {
            when (selectedTab) {
                0 -> TeacherHierarchyFlowView(
                    repository = repository,
                    subjects = subjects,
                    allNotebooks = notebooks,
                    navStep = navStep,
                    selectedSubject = selectedSubjectNav,
                    selectedClass = selectedClassNav,
                    selectedSection = selectedSectionNav,
                    selectedStudent = selectedStudentNav,
                    onSelectSubject = { sub ->
                        selectedSubjectNav = sub
                        selectedClassNav = null
                        selectedSectionNav = null
                        selectedStudentNav = null
                        navStep = 1
                    },
                    onSelectClass = { cls ->
                        selectedClassNav = cls
                        selectedSectionNav = null
                        selectedStudentNav = null
                        navStep = 2
                    },
                    onSelectSection = { sec ->
                        selectedSectionNav = sec
                        selectedStudentNav = null
                        navStep = 3
                    },
                    onSelectStudent = { student ->
                        selectedStudentNav = student
                        navStep = 4
                    },
                    onStepBackTo = { targetStep ->
                        navStep = targetStep
                    },
                    onOpenNotebookForCorrection = { nb ->
                        repository.selectNotebook(nb)
                        activeAnnotatingNotebook = nb
                    },
                    onOpenResourceReader = { activeReadingResource = it },
                    onCreateNotebook = { showCreateNotebookDialog = true }
                )

                1 -> TeacherAnalyticsView(dailyAnalytics = dailyAnalytics)

                2 -> McqExamControllerView(
                    examPapers = examPapers,
                    lockdownState = examLockdownState,
                    onToggleLockdown = { paper, enable ->
                        repository.toggleExamLockdown(paper, enable)
                    }
                )

                3 -> KioskUrlManagerView(
                    kioskConfig = kioskConfig,
                    onAddUrlClicked = { showAddKioskUrlDialog = true },
                    onToggleKiosk = { repository.toggleKioskActive(it) }
                )
            }
        }
    }

    if (showCreateNotebookDialog) {
        CreateNotebookDialog(
            repository = repository,
            onDismiss = { showCreateNotebookDialog = false }
        )
    }

    if (showAddKioskUrlDialog) {
        AddKioskUrlDialog(
            repository = repository,
            onDismiss = { showAddKioskUrlDialog = false }
        )
    }
}

// FR-3.1 & FR-3.8 Implementation Component: Subject -> Class -> Class-wise Textbooks (FR-3.8) & Section -> Student List -> Student Notebooks
@Composable
fun TeacherHierarchyFlowView(
    repository: EduScribeRepository,
    subjects: List<Subject>,
    allNotebooks: List<Notebook>,
    navStep: Int,
    selectedSubject: Subject?,
    selectedClass: SchoolClass?,
    selectedSection: Section?,
    selectedStudent: User?,
    onSelectSubject: (Subject) -> Unit,
    onSelectClass: (SchoolClass) -> Unit,
    onSelectSection: (Section) -> Unit,
    onSelectStudent: (User) -> Unit,
    onStepBackTo: (Int) -> Unit,
    onOpenNotebookForCorrection: (Notebook) -> Unit,
    onOpenResourceReader: (DigitalResource) -> Unit,
    onCreateNotebook: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Navigation Bar & Back Button Controls
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (navStep > 0) {
                        OutlinedButton(
                            onClick = { onStepBackTo(navStep - 1) },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("⬅ Back Navigation", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Text("📌 Path: ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Gray)

                    Text(
                        text = "1. Subjects",
                        fontWeight = if (navStep == 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (navStep == 0) MaterialTheme.colorScheme.primary else Color.DarkGray,
                        modifier = Modifier.clickable { onStepBackTo(0) }
                    )

                    if (selectedSubject != null) {
                        Text(" ➔ ", color = Color.Gray, fontSize = 12.sp)
                        Text(
                            text = "2. ${selectedSubject.name}",
                            fontWeight = if (navStep == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (navStep == 1) MaterialTheme.colorScheme.primary else Color.DarkGray,
                            modifier = Modifier.clickable { onStepBackTo(1) }
                        )
                    }

                    if (selectedClass != null) {
                        Text(" ➔ ", color = Color.Gray, fontSize = 12.sp)
                        Text(
                            text = "3. ${selectedClass.name} (Textbooks & Sections)",
                            fontWeight = if (navStep == 2) FontWeight.Bold else FontWeight.Normal,
                            color = if (navStep == 2) MaterialTheme.colorScheme.primary else Color.DarkGray,
                            modifier = Modifier.clickable { onStepBackTo(2) }
                        )
                    }

                    if (selectedSection != null) {
                        Text(" ➔ ", color = Color.Gray, fontSize = 12.sp)
                        Text(
                            text = "4. ${selectedSection.name}",
                            fontWeight = if (navStep == 3) FontWeight.Bold else FontWeight.Normal,
                            color = if (navStep == 3) MaterialTheme.colorScheme.primary else Color.DarkGray,
                            modifier = Modifier.clickable { onStepBackTo(3) }
                        )
                    }

                    if (selectedStudent != null) {
                        Text(" ➔ ", color = Color.Gray, fontSize = 12.sp)
                        Text(
                            text = "5. ${selectedStudent.fullName}",
                            fontWeight = if (navStep == 4) FontWeight.Bold else FontWeight.Normal,
                            color = if (navStep == 4) MaterialTheme.colorScheme.primary else Color.DarkGray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (navStep) {
            // Step 0: Graphic Subject Tiles
            0 -> {
                Text("Select a Subject Tile:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    subjects.forEach { sub ->
                        GraphicSubjectTile(
                            subject = sub,
                            isSelected = selectedSubject?.id == sub.id,
                            onClick = { onSelectSubject(sub) }
                        )
                    }
                }
            }

            // Step 1: Select Graphic Class Tile for Subject
            1 -> {
                Text("Select Class Tile for ${selectedSubject?.name}:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    selectedSubject?.classes?.forEach { cls ->
                        GraphicClassTile(
                            schoolClass = cls,
                            isSelected = selectedClass?.id == cls.id,
                            onClick = { onSelectClass(cls) }
                        )
                    }
                }
            }

            // Step 2: Display Class-wise Textbooks & Study Materials (FR-3.8) AND Graphic Section Tiles
            2 -> {
                Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Left Column: Class-wise Textbooks & Study Materials (FR-3.8)
                    Card(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Surface(color = Color(0xFFE0F2FE), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "📖 ${selectedClass?.name} (${selectedSubject?.name}) - Class-Wise Textbooks (FR-3.8)",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF0369A1)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Displaying class-wise textbooks & study materials (common for all students in ${selectedClass?.name}).", fontSize = 12.sp, color = Color.Gray)

                            Spacer(modifier = Modifier.height(12.dp))

                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(selectedSubject?.chapters ?: emptyList()) { ch ->
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
                                                        .clickable { onOpenResourceReader(res) },
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

                                                        Button(onClick = { onOpenResourceReader(res) }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)) {
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

                    // Right Column: Graphic Section Tiles to inspect student roster
                    Card(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("👥 Select Section Tile for ${selectedClass?.name}:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Choose a section to inspect student roster & notebook corrections", fontSize = 12.sp, color = Color.Gray)

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                selectedClass?.sections?.forEach { sec ->
                                    GraphicSectionTile(
                                        section = sec,
                                        onClick = { onSelectSection(sec) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Step 3: Student Roster List
            3 -> {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Student Roster for ${selectedClass?.name} - ${selectedSection?.name}:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Select a student to inspect & correct their individual notebooks for ${selectedSubject?.name}", fontSize = 12.sp, color = Color.Gray)
                    }

                    OutlinedButton(onClick = { onStepBackTo(2) }) {
                        Text("⬅ Back Navigation")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val students = repository.sampleUsers.filter { it.role == UserRole.STUDENT }
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(students) { student ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectStudent(student) },
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🎓", fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(student.fullName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("${student.email} • ID: ${student.id}", fontSize = 12.sp, color = Color.Gray)
                                    }
                                }

                                Button(onClick = { onSelectStudent(student) }) {
                                    Text("Inspect Notebooks ➔")
                                }
                            }
                        }
                    }
                }
            }

            // Step 4: Individual Student Graphic Notebooks
            4 -> {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                OutlinedButton(onClick = { onStepBackTo(3) }) {
                                    Text("⬅ Back Navigation to Student List")
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text("📓 Digital Notebooks of ${selectedStudent?.fullName}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("Subject: ${selectedSubject?.name} • Class: ${selectedClass?.name} (${selectedSection?.name})", fontSize = 12.sp, color = Color.Gray)
                                }
                            }

                            Button(onClick = onCreateNotebook) {
                                Text("➕ Assign New Notebook")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        val studentNotebooks = allNotebooks.filter {
                            (it.studentName == selectedStudent?.fullName || it.studentId == selectedStudent?.id) &&
                                    (selectedSubject == null || it.subjectName.contains(selectedSubject.name, ignoreCase = true) || selectedSubject.name.contains(it.subjectName, ignoreCase = true))
                        }

                        if (studentNotebooks.isEmpty()) {
                            Text("No notebooks created for this student in ${selectedSubject?.name} yet.", fontSize = 13.sp, color = Color.Gray)
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                studentNotebooks.forEach { nb ->
                                    GraphicNotebookTile(
                                        notebook = nb,
                                        onClick = { onOpenNotebookForCorrection(nb) }
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

// FR-3.9: Teacher & Principal Analytics View (Day, Week, and Month Wise)
@Composable
fun TeacherAnalyticsView(dailyAnalytics: DailyAnalytics?) {
    var reportInterval by remember { mutableStateOf(0) } // 0: Day-wise, 1: Week-wise, 2: Month-wise

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("📊 Class & Student Analytics (FR-3.9)", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("View aggregated student learning activity day, week, and month wise", fontSize = 12.sp, color = Color.Gray)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(selected = reportInterval == 0, onClick = { reportInterval = 0 }, label = { Text("📅 Day-Wise") })
                FilterChip(selected = reportInterval == 1, onClick = { reportInterval = 1 }, label = { Text("📆 Week-Wise") })
                FilterChip(selected = reportInterval == 2, onClick = { reportInterval = 2 }, label = { Text("🗓️ Month-Wise") })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TrendBarChart(
                title = if (reportInterval == 0) "Day-on-Day Class Activity (Hours)" else if (reportInterval == 1) "Week-on-Week Class Activity (Hours)" else "Month-on-Month Class Activity (Hours)",
                subtitle = "Active time spent across Classwork, Homework & Textbooks",
                items = listOf(
                    BarChartItem("Mon", 2.5f), BarChartItem("Tue", 3.2f), BarChartItem("Wed", 4.0f),
                    BarChartItem("Thu", 2.8f), BarChartItem("Fri", 3.5f), BarChartItem("Sat", 1.8f)
                ),
                modifier = Modifier.weight(1f)
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
    }
}

@Composable
fun McqExamControllerView(
    examPapers: List<ExamPaper>,
    lockdownState: ExamLockdownState,
    onToggleLockdown: (ExamPaper, Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("📝 MCQ Exam Bank & Lockdown Mode Controller", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Enforce lockdown exam constraints (blocks textbooks, study materials & notebooks during active test)", fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        examPapers.forEach { paper ->
            val isActive = lockdownState.isExamActiveGlobally && lockdownState.activeExamPaper?.id == paper.id

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isActive) Color(0xFFFFF1F2) else Color.White
                ),
                border = if (isActive) BorderStroke(2.dp, Color(0xFFE11D48)) else null
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(paper.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("${paper.subjectName} • ${paper.targetClass} (${paper.targetSection}) • ${paper.questions.size} Questions • ${paper.durationMinutes} Mins", fontSize = 12.sp, color = Color.Gray)
                        }

                        Switch(
                            checked = isActive,
                            onCheckedChange = { enable -> onToggleLockdown(paper, enable) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isActive) {
                        Surface(
                            color = Color(0xFFFFE4E6),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "🔒 EXAM LOCKDOWN ACTIVE: All student tablets in Section A are locked into Exam Mode. Textbooks, notebooks, and study materials are strictly blocked.",
                                modifier = Modifier.padding(12.dp),
                                color = Color(0xFF9F1239),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Questions Included in Assessment:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    paper.questions.forEachIndexed { idx, q ->
                        Text("${idx + 1}. ${q.questionText} (${q.marks} mark)", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun KioskUrlManagerView(
    kioskConfig: KioskConfig,
    onAddUrlClicked: () -> Unit,
    onToggleKiosk: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("🔒 Kiosk / Restricted App Mode", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Specify allowed educational URLs / YouTube videos. Disables all other tablet apps.", fontSize = 12.sp, color = Color.Gray)
            }

            Button(onClick = onAddUrlClicked) {
                Text("➕ Add Allowed URL / Video")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Kiosk Mode Status on Student Tablets", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Switch(
                        checked = kioskConfig.isKioskActive,
                        onCheckedChange = onToggleKiosk
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Allowed Whitelisted Web Pages & YouTube Videos:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))

                kioskConfig.allowedUrls.forEach { item ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF8FAFC),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(if (item.isYouTube) "▶️" else "🌐", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(item.url, fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateNotebookDialog(
    repository: EduScribeRepository,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("Mathematics Homework Book") }
    var selectedRule by remember { mutableStateOf(RuleType.SINGLE_LINE) }
    var selectedBookType by remember { mutableStateOf(BookType.HOMEWORK) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configure & Create Notebook for Student") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Notebook Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Rule Type:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                RuleType.entries.forEach { rule ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { selectedRule = rule }
                    ) {
                        RadioButton(selected = selectedRule == rule, onClick = { selectedRule = rule })
                        Text(rule.displayName, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Book Type:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                BookType.entries.forEach { bType ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { selectedBookType = bType }
                    ) {
                        RadioButton(selected = selectedBookType == bType, onClick = { selectedBookType = bType })
                        Text(bType.displayName, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val aarav = repository.sampleUsers[3]
                    repository.createNotebook(
                        title = title,
                        subjectId = "SUB-MATH",
                        subjectName = "Mathematics",
                        className = "Class 10",
                        sectionId = "Section A",
                        ruleType = selectedRule,
                        bookType = selectedBookType,
                        targetStudent = aarav
                    )
                    onDismiss()
                }
            ) {
                Text("Create Notebook")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddKioskUrlDialog(
    repository: EduScribeRepository,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("NCERT Official Physics Experiments") }
    var url by remember { mutableStateOf("https://ncert.nic.in/physics-labs") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Whitelisted Educational URL") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title / Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL Link (https://...)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    repository.addAllowedUrlToKiosk(title, url, url.contains("youtube"))
                    onDismiss()
                }
            ) {
                Text("Add URL")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
