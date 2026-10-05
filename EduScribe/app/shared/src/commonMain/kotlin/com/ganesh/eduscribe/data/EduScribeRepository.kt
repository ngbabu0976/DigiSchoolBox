package com.ganesh.eduscribe.data

import com.ganesh.eduscribe.domain.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

class EduScribeRepository {

    // Default Pre-populated Users merging MasterDatabaseImport Excel Database (200 Students & 10 Teachers/HODs)
    val sampleUsers = listOf(
        User("USR-ADMIN", "admin", "Dr. Rajesh Sharma", UserRole.ADMIN, "admin@stxaviers.edu.in"),
        User("USR-PRINCIPAL", "principal", "Sr. Mary D'Souza", UserRole.PRINCIPAL, "principal@stxaviers.edu.in"),
        User("USR-PARENT", "parent", "Mr. Vikram Patel", UserRole.PARENT, "v.patel@gmail.com", studentIdRef = "STU1001")
    ) + MasterDatabaseImport.importedTeachers + MasterDatabaseImport.importedStudents

    private val _currentUser = MutableStateFlow<User?>(null) // Starting page is Login Screen (FR-1.1)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _schoolConfig = MutableStateFlow(SchoolConfig())
    val schoolConfig: StateFlow<SchoolConfig> = _schoolConfig.asStateFlow()

    // Curriculum & Resources
    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    // Student Notebooks
    private val _notebooks = MutableStateFlow<List<Notebook>>(emptyList())
    val notebooks: StateFlow<List<Notebook>> = _notebooks.asStateFlow()

    // Vector Canvas Pages (Key: notebookId_pageNumber)
    private val _canvasPages = MutableStateFlow<Map<String, CanvasPage>>(emptyMap())
    val canvasPages: StateFlow<Map<String, CanvasPage>> = _canvasPages.asStateFlow()

    // Shared Pages
    private val _sharedPages = MutableStateFlow<List<PageSharing>>(emptyList())
    val sharedPages: StateFlow<List<PageSharing>> = _sharedPages.asStateFlow()

    // MCQ Exam Papers
    private val _examPapers = MutableStateFlow<List<ExamPaper>>(emptyList())
    val examPapers: StateFlow<List<ExamPaper>> = _examPapers.asStateFlow()

    // Exam Lockdown State
    private val _examLockdownState = MutableStateFlow(ExamLockdownState())
    val examLockdownState: StateFlow<ExamLockdownState> = _examLockdownState.asStateFlow()

    // Kiosk Mode Configuration
    private val _kioskConfig = MutableStateFlow(
        KioskConfig(
            allowedUrls = listOf(
                AllowedUrl("U1", "Khan Academy Science", "https://www.khanacademy.org/science"),
                AllowedUrl("U2", "NCERT Official eBooks", "https://ncert.nic.in/textbook.php"),
                AllowedUrl("U3", "YouTube Math Channel (Educational)", "https://www.youtube.com/embed/videoseries?list=PL123", isYouTube = true)
            )
        )
    )
    val kioskConfig: StateFlow<KioskConfig> = _kioskConfig.asStateFlow()

    // Analytics Data
    private val _dailyAnalytics = MutableStateFlow<DailyAnalytics?>(null)
    val dailyAnalytics: StateFlow<DailyAnalytics?> = _dailyAnalytics.asStateFlow()

    // Active Selection States
    private val _selectedSubject = MutableStateFlow<Subject?>(null)
    val selectedSubject: StateFlow<Subject?> = _selectedSubject.asStateFlow()

    private val _selectedNotebook = MutableStateFlow<Notebook?>(null)
    val selectedNotebook: StateFlow<Notebook?> = _selectedNotebook.asStateFlow()

    private val _selectedPageNumber = MutableStateFlow(1)
    val selectedPageNumber: StateFlow<Int> = _selectedPageNumber.asStateFlow()

    // Math Tool State
    private val _mathToolState = MutableStateFlow(MathToolState())
    val mathToolState: StateFlow<MathToolState> = _mathToolState.asStateFlow()

    // Active Selected Pen Tool
    private val _selectedPenTool = MutableStateFlow(PenTool.STUDENT_BLUE)
    val selectedPenTool: StateFlow<PenTool> = _selectedPenTool.asStateFlow()

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        // Build Classes & Sections matching Excel database (Class VI, VII, VIII, IX, X with Section A & Section B)
        val secA_VI = Section("S-VI-A", "Section A (E-Techno)", 25)
        val secB_VI = Section("S-VI-B", "Section B (Olympiad)", 25)
        val classVI = SchoolClass("C-VI", "Class VI", listOf(secA_VI, secB_VI))

        val secA_VII = Section("S-VII-A", "Section A (E-Techno)", 25)
        val secB_VII = Section("S-VII-B", "Section B (Olympiad)", 25)
        val classVII = SchoolClass("C-VII", "Class VII", listOf(secA_VII, secB_VII))

        val secA_X = Section("S-X-A", "Section A", 30)
        val secB_X = Section("S-X-B", "Section B", 30)
        val classX = SchoolClass("C-X", "Class 10", listOf(secA_X, secB_X))

        val mathSub = Subject(
            id = "SUB-MATH",
            name = "Mathematics",
            iconName = "calculate",
            classes = listOf(classVI, classVII, classX),
            chapters = listOf(
                Chapter("CH-M1", 1, "Real Numbers & Trigonometry", listOf(
                    DigitalResource("R1", "NCERT Class 10 Math Textbook", "SUB-MATH", "Class 10", ResourceType.TEXTBOOK, 1, 45, "pdf_math_ch1.pdf", "Official Textbook Chapter 1"),
                    DigitalResource("R2", "Trigonometry Formula Sheet & Practice", "SUB-MATH", "Class 10", ResourceType.STUDY_MATERIAL, 1, 12, "pdf_math_formulas.pdf", "Summary Study Notes"),
                    DigitalResource("R3", "Coordinate Geometry Graph Pages", "SUB-MATH", "Class 10", ResourceType.GRAPH_PAGE, 1, 5, "graph_paper.pdf", "Standard 1mm Grid Graph Pages")
                )),
                Chapter("CH-M2", 2, "Quadratic Equations & Polynomials", listOf(
                    DigitalResource("R4", "Quadratic Equations Chapter", "SUB-MATH", "Class 10", ResourceType.TEXTBOOK, 2, 38, "pdf_math_ch2.pdf", "Official Textbook Chapter 2")
                ))
            )
        )

        val scienceSub = Subject(
            id = "SUB-SCI",
            name = "Science & Physics",
            iconName = "science",
            classes = listOf(classVI, classVII, classX),
            chapters = listOf(
                Chapter("CH-S1", 1, "Light - Reflection and Refraction", listOf(
                    DigitalResource("R5", "Physics Class 10 Chapter 1", "SUB-SCI", "Class 10", ResourceType.TEXTBOOK, 1, 50, "pdf_sci_ch1.pdf", "Ray diagrams and optics"),
                    DigitalResource("R6", "Ray Diagram Quick Guide", "SUB-SCI", "Class 10", ResourceType.STUDY_MATERIAL, 1, 15, "pdf_optics.pdf", "Optics reference guide")
                ))
            )
        )

        val socialSub = Subject(
            id = "SUB-SST",
            name = "Social Studies & Geography",
            iconName = "public",
            classes = listOf(classVI, classVII, classX),
            chapters = listOf(
                Chapter("CH-SS1", 1, "Resources & World Geography", listOf(
                    DigitalResource("R7", "World Physical & Political Map", "SUB-SST", "Class 10", ResourceType.SOCIAL_MAP, 1, 4, "world_map.pdf", "HD World Map for practice")
                ))
            )
        )

        _subjects.value = listOf(mathSub, scienceSub, socialSub)
        _selectedSubject.value = mathSub

        // Seed Sample Notebooks across Excel imported students
        val initialNotebooks = mutableListOf<Notebook>()
        val firstStudent = MasterDatabaseImport.importedStudents.firstOrNull() ?: User("STU1001", "stu1001", "Diya Sharma", UserRole.STUDENT, "diya@student.edu")

        initialNotebooks.add(
            Notebook("NB-M1", "Mathematics Classwork Notebook", firstStudent.id, firstStudent.fullName, "SUB-MATH", "Mathematics", firstStudent.assignedClass ?: "Class VI", firstStudent.assignedSection ?: "Section A", RuleType.SINGLE_LINE, BookType.CLASSWORK, 10, isEvaluated = true, teacherGrade = "A+", teacherFeedback = "Excellent step-by-step solutions!")
        )
        initialNotebooks.add(
            Notebook("NB-M2", "Mathematics Practice & Geometry", firstStudent.id, firstStudent.fullName, "SUB-MATH", "Mathematics", firstStudent.assignedClass ?: "Class VI", firstStudent.assignedSection ?: "Section A", RuleType.SQUARE_GRID, BookType.PRACTICE, 10)
        )
        initialNotebooks.add(
            Notebook("NB-S1", "Science & Physics Notes", firstStudent.id, firstStudent.fullName, "SUB-SCI", "Science & Physics", firstStudent.assignedClass ?: "Class VI", firstStudent.assignedSection ?: "Section A", RuleType.BLANK, BookType.HOMEWORK, 10)
        )

        // Seed notebooks for secondary students
        MasterDatabaseImport.importedStudents.take(10).forEachIndexed { idx, stu ->
            if (idx > 0) {
                initialNotebooks.add(
                    Notebook("NB-${stu.id}-CW", "Math Classwork Book", stu.id, stu.fullName, "SUB-MATH", "Mathematics", stu.assignedClass ?: "Class VI", stu.assignedSection ?: "Section A", RuleType.SINGLE_LINE, BookType.CLASSWORK, 10, isEvaluated = idx % 2 == 0, teacherGrade = if (idx % 2 == 0) "A" else null, teacherFeedback = if (idx % 2 == 0) "Good work" else null)
                )
            }
        }

        _notebooks.value = initialNotebooks
        _selectedNotebook.value = initialNotebooks[0]

        // Seed Sample Vector Canvas Page
        val pageKey = "${initialNotebooks[0].id}_1"
        val sampleStrokes = listOf(
            VectorStroke(
                id = "STK-1",
                points = listOf(StrokePoint(100f, 200f), StrokePoint(250f, 200f), StrokePoint(250f, 350f), StrokePoint(100f, 200f)),
                colorHex = PenTool.STUDENT_BLUE.defaultColorHex,
                strokeWidth = 4f,
                tool = PenTool.STUDENT_BLUE,
                authorRole = UserRole.STUDENT,
                authorName = firstStudent.fullName,
                timestampMs = 1710000100000L
            ),
            VectorStroke(
                id = "STK-2",
                points = listOf(StrokePoint(100f, 150f), StrokePoint(300f, 150f)),
                colorHex = PenTool.TEACHER_RED.defaultColorHex,
                strokeWidth = 5f,
                tool = PenTool.TEACHER_RED,
                authorRole = UserRole.TEACHER,
                authorName = "Rajesh Kumar (HOD Math)",
                timestampMs = 1710000200000L
            )
        )

        val commit1 = StrokeDiffCommit(
            commitId = "CMT-101",
            notebookId = initialNotebooks[0].id,
            pageNumber = 1,
            authorId = firstStudent.id,
            authorName = firstStudent.fullName,
            authorRole = UserRole.STUDENT,
            commitMessage = "Initial Triangle & Formula Derivation",
            timestampMs = 1710000100000L,
            addedStrokes = listOf(sampleStrokes[0]),
            removedStrokeIds = emptyList()
        )

        val commit2 = StrokeDiffCommit(
            commitId = "CMT-102",
            notebookId = initialNotebooks[0].id,
            pageNumber = 1,
            authorId = "TCH1003",
            authorName = "Rajesh Kumar (HOD Math)",
            authorRole = UserRole.TEACHER,
            commitMessage = "Teacher Evaluation & Red Pen Corrections",
            timestampMs = 1710000200000L,
            addedStrokes = listOf(sampleStrokes[1]),
            removedStrokeIds = emptyList()
        )

        _canvasPages.value = mapOf(
            pageKey to CanvasPage(
                pageId = pageKey,
                pageNumber = 1,
                ruleType = RuleType.SINGLE_LINE,
                strokes = sampleStrokes,
                commits = listOf(commit1, commit2)
            )
        )

        // Seed Sample MCQ Exam
        val mcqExam = ExamPaper(
            id = "EXAM-101",
            title = "Class VI Mathematics Mid-Term MCQ Assessment",
            subjectId = "SUB-MATH",
            subjectName = "Mathematics",
            targetClass = "Class VI",
            targetSection = "Section A",
            durationMinutes = 20,
            totalMarks = 5,
            questions = listOf(
                McqQuestion("Q1", "What is the value of sin(90°) + cos(0°)?", listOf(
                    McqOption("O1", "0"), McqOption("O2", "1"), McqOption("O3", "2", isCorrect = true), McqOption("O4", "Undefined")
                ), 1),
                McqQuestion("Q2", "If a quadratic equation ax² + bx + c = 0 has equal roots, then the discriminant D is:", listOf(
                    McqOption("O5", "D > 0"), McqOption("O6", "D = 0", isCorrect = true), McqOption("O7", "D < 0"), McqOption("O8", "D = 1")
                ), 1),
                McqQuestion("Q3", "The HCF of 96 and 404 is:", listOf(
                    McqOption("O9", "2"), McqOption("O10", "4", isCorrect = true), McqOption("O11", "12"), McqOption("O12", "96")
                ), 1),
                McqQuestion("Q4", "The distance of point P(3, 4) from the origin is:", listOf(
                    McqOption("O13", "3 units"), McqOption("O14", "4 units"), McqOption("O15", "5 units", isCorrect = true), McqOption("O16", "7 units")
                ), 1),
                McqQuestion("Q5", "The nth term of an Arithmetic Progression is an = 3 + 4n. Find the common difference d:", listOf(
                    McqOption("O17", "3"), McqOption("O18", "4", isCorrect = true), McqOption("O19", "7"), McqOption("O20", "12")
                ), 1)
            )
        )
        _examPapers.value = listOf(mcqExam)

        // Seed Time-Spent Analytics
        val analyticsRecords = listOf(
            TimeSpentRecord("TR1", firstStudent.id, AnalyticsCategory.TEXTBOOK, "NCERT Class VI Math Chapter 1", 3600, "2025-03-29"),
            TimeSpentRecord("TR2", firstStudent.id, AnalyticsCategory.CLASSWORK_NOTEBOOK, "Mathematics Classwork Notebook", 2700, "2025-03-29"),
            TimeSpentRecord("TR3", firstStudent.id, AnalyticsCategory.HOMEWORK_NOTEBOOK, "Science Lab Notes", 1800, "2025-03-29"),
            TimeSpentRecord("TR4", firstStudent.id, AnalyticsCategory.PRACTICE_NOTEBOOK, "Mathematics Practice Book", 1200, "2025-03-29"),
            TimeSpentRecord("TR5", firstStudent.id, AnalyticsCategory.STUDY_MATERIAL, "Formula Sheet", 900, "2025-03-29")
        )

        _dailyAnalytics.value = DailyAnalytics(
            dateString = "Yesterday (2025-03-29)",
            totalTimeSeconds = 10200, // 2h 50m
            textbookSeconds = 3600,
            studyMaterialSeconds = 900,
            classworkSeconds = 2700,
            homeworkSeconds = 1800,
            practiceSeconds = 1200,
            testSeconds = 0,
            records = analyticsRecords
        )
    }

    // Authentication Actions
    fun loginAs(role: UserRole) {
        val matchedUser = sampleUsers.firstOrNull { it.role == role } ?: sampleUsers[0]
        _currentUser.value = matchedUser
        // Adjust default pen tool according to role
        when (role) {
            UserRole.TEACHER -> _selectedPenTool.value = PenTool.TEACHER_RED
            UserRole.PRINCIPAL, UserRole.ADMIN -> _selectedPenTool.value = PenTool.PRINCIPAL_GREEN
            else -> _selectedPenTool.value = PenTool.STUDENT_BLUE
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    fun updateSchoolConfig(newConfig: SchoolConfig) {
        _schoolConfig.value = newConfig
    }

    fun selectSubject(subject: Subject) {
        _selectedSubject.value = subject
    }

    fun selectNotebook(notebook: Notebook) {
        _selectedNotebook.value = notebook
        _selectedPageNumber.value = 1
        ensureCanvasPageExists(notebook.id, 1, notebook.ruleType)
    }

    fun selectPage(pageNumber: Int) {
        _selectedPageNumber.value = pageNumber
        _selectedNotebook.value?.let { nb ->
            ensureCanvasPageExists(nb.id, pageNumber, nb.ruleType)
        }
    }

    fun setPenTool(tool: PenTool) {
        _selectedPenTool.value = tool
    }

    fun setMathTool(toolType: MathToolType) {
        _mathToolState.value = _mathToolState.value.copy(activeTool = toolType)
    }

    fun updateMathToolState(newState: MathToolState) {
        _mathToolState.value = newState
    }

    // Vector Canvas Actions & Git-like Diff Commits
    private fun ensureCanvasPageExists(notebookId: String, pageNumber: Int, ruleType: RuleType) {
        val key = "${notebookId}_$pageNumber"
        if (!_canvasPages.value.containsKey(key)) {
            val updatedMap = _canvasPages.value.toMutableMap()
            updatedMap[key] = CanvasPage(pageId = key, pageNumber = pageNumber, ruleType = ruleType)
            _canvasPages.value = updatedMap
        }
    }

    fun addStrokeToCurrentPage(points: List<StrokePoint>) {
        val nb = _selectedNotebook.value ?: return
        val pageNum = _selectedPageNumber.value
        val key = "${nb.id}_$pageNum"
        val user = _currentUser.value ?: return
        val currentTool = _selectedPenTool.value

        val strokeId = "STK-${100000 + (10000..99999).random()}"
        val newStroke = VectorStroke(
            id = strokeId,
            points = points,
            colorHex = currentTool.defaultColorHex,
            strokeWidth = currentTool.strokeWidthPx,
            isEraser = currentTool.isEraser,
            tool = currentTool,
            authorRole = user.role,
            authorName = user.fullName,
            timestampMs = 1710000300000L
        )

        val pageMap = _canvasPages.value.toMutableMap()
        val existingPage = pageMap[key] ?: CanvasPage(pageId = key, pageNumber = pageNum, ruleType = nb.ruleType)

        if (currentTool.isEraser) {
            // Stroke Eraser logic: Remove strokes that user touches (or student's own strokes)
            val updatedStrokes = existingPage.strokes.filterNot { stroke ->
                // Immutability: Students cannot erase Teacher or Principal red/green strokes
                val isProtected = (user.role == UserRole.STUDENT && (stroke.authorRole == UserRole.TEACHER || stroke.authorRole == UserRole.PRINCIPAL || stroke.authorRole == UserRole.ADMIN))
                if (isProtected) return@filterNot false
                // Check if eraser points intersect stroke points within threshold
                val eraserBox = points
                stroke.points.any { p -> eraserBox.any { ep -> abs(p.x - ep.x) < 25f && abs(p.y - ep.y) < 25f } }
            }
            val pageWithErased = existingPage.copy(strokes = updatedStrokes)
            pageMap[key] = pageWithErased
            _canvasPages.value = pageMap
        } else {
            val updatedStrokes = existingPage.strokes + newStroke

            // Create Git-Like Diff Commit
            val commit = StrokeDiffCommit(
                commitId = "CMT-${1000 + (100..999).random()}",
                notebookId = nb.id,
                pageNumber = pageNum,
                authorId = user.id,
                authorName = user.fullName,
                authorRole = user.role,
                commitMessage = "Added ${currentTool.displayName} stroke by ${user.fullName}",
                timestampMs = 1710000300000L,
                addedStrokes = listOf(newStroke),
                removedStrokeIds = emptyList()
            )

            val pageWithNewStroke = existingPage.copy(
                strokes = updatedStrokes,
                commits = existingPage.commits + commit
            )
            pageMap[key] = pageWithNewStroke
            _canvasPages.value = pageMap
        }
    }

    fun createNotebook(
        title: String,
        subjectId: String,
        subjectName: String,
        className: String,
        sectionId: String,
        ruleType: RuleType,
        bookType: BookType,
        targetStudent: User
    ) {
        val newNotebook = Notebook(
            id = "NB-${100 + _notebooks.value.size + 1}",
            title = title,
            studentId = targetStudent.id,
            studentName = targetStudent.fullName,
            subjectId = subjectId,
            subjectName = subjectName,
            className = className,
            sectionId = sectionId,
            ruleType = ruleType,
            bookType = bookType,
            totalPages = 10,
            createdByTeacherId = _currentUser.value?.id ?: "TCH-001"
        )
        _notebooks.value = _notebooks.value + newNotebook
        selectNotebook(newNotebook)
    }

    fun evaluateNotebook(notebookId: String, grade: String, feedback: String) {
        _notebooks.value = _notebooks.value.map { nb ->
            if (nb.id == notebookId) {
                nb.copy(isEvaluated = true, teacherGrade = grade, teacherFeedback = feedback)
            } else nb
        }
        _selectedNotebook.value?.let { current ->
            if (current.id == notebookId) {
                _selectedNotebook.value = current.copy(isEvaluated = true, teacherGrade = grade, teacherFeedback = feedback)
            }
        }
    }

    fun sharePageWithClass(notebookId: String, pageNumber: Int, studentName: String, subjectName: String, targetSection: String) {
        val shareItem = PageSharing(
            id = "PS-${100 + _sharedPages.value.size + 1}",
            notebookId = notebookId,
            pageNumber = pageNumber,
            sharedByTeacherId = _currentUser.value?.id ?: "TCH-001",
            targetSectionId = targetSection,
            studentName = studentName,
            subjectName = subjectName,
            timestampMs = 1710000400000L
        )
        _sharedPages.value = _sharedPages.value + shareItem
    }

    fun toggleExamLockdown(examPaper: ExamPaper, enable: Boolean, sectionId: String = "Section A") {
        val updatedPaper = examPaper.copy(isExamActive = enable)
        _examPapers.value = _examPapers.value.map { if (it.id == examPaper.id) updatedPaper else it }

        _examLockdownState.value = ExamLockdownState(
            isExamActiveGlobally = enable,
            activeExamPaper = if (enable) updatedPaper else null,
            activeSectionId = if (enable) sectionId else null,
            lockTextbooks = enable,
            lockNotebooks = enable,
            lockStudyMaterials = enable
        )
    }

    fun addAllowedUrlToKiosk(title: String, url: String, isYouTube: Boolean) {
        val newUrl = AllowedUrl(
            id = "U-${_kioskConfig.value.allowedUrls.size + 1}",
            title = title,
            url = url,
            isYouTube = isYouTube,
            addedByTeacher = _currentUser.value?.id ?: "TCH-001"
        )
        _kioskConfig.value = _kioskConfig.value.copy(
            allowedUrls = _kioskConfig.value.allowedUrls + newUrl
        )
    }

    fun toggleKioskActive(enable: Boolean) {
        _kioskConfig.value = _kioskConfig.value.copy(isKioskActive = enable)
    }

    // Bulk Ingestion Simulations
    fun simulateExcelImportStudents(): Int {
        return MasterDatabaseImport.importedStudents.size
    }

    fun simulateZipImportTextbooks(): Int {
        // Simulates unzipping subject textbook bundles
        return 8 // 8 files extracted & ingested into Chapter hierarchy
    }
}
