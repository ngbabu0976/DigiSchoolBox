package com.ganesh.eduscribe.domain.model

enum class RuleType(val displayName: String, val description: String) {
    SINGLE_LINE("Single Line", "Standard ruled notebook lines for general writing"),
    FOUR_LINE("Four Line", "Red and blue guides for English calligraphy and handwriting"),
    SQUARE_GRID("Square Grid", "Grid squares for Mathematics, Tables, and Data"),
    BLANK("Blank Page", "Unruled canvas for Diagrams, Physics, and Freehand Art")
}

enum class BookType(val displayName: String) {
    CLASSWORK("Classwork (CW)"),
    HOMEWORK("Homework (HW)"),
    PRACTICE("Practice Book"),
    TEST("Test & Quiz Book")
}

data class PageSharing(
    val id: String,
    val notebookId: String,
    val pageNumber: Int,
    val sharedByTeacherId: String,
    val targetSectionId: String,
    val studentName: String,
    val subjectName: String,
    val timestampMs: Long
)

data class Notebook(
    val id: String,
    val title: String,
    val studentId: String,
    val studentName: String,
    val subjectId: String,
    val subjectName: String,
    val className: String,
    val sectionId: String,
    val ruleType: RuleType,
    val bookType: BookType,
    val totalPages: Int = 10,
    val createdByTeacherId: String = "TCH-001",
    val createdAtMs: Long = 1710000000000L,
    val isEvaluated: Boolean = false,
    val teacherGrade: String? = null,
    val teacherFeedback: String? = null
)
