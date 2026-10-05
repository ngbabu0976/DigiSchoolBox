package com.ganesh.eduscribe.domain.model

enum class PenTool(
    val displayName: String,
    val defaultColorHex: Long,
    val strokeWidthPx: Float,
    val isEraser: Boolean = false,
    val allowedRoles: Set<UserRole>
) {
    STUDENT_BLUE(
        displayName = "Blue Pen",
        defaultColorHex = 0xFF1E88E5,
        strokeWidthPx = 4f,
        allowedRoles = setOf(UserRole.STUDENT, UserRole.TEACHER, UserRole.PRINCIPAL, UserRole.ADMIN)
    ),
    STUDENT_BLACK(
        displayName = "Black Pen",
        defaultColorHex = 0xFF212121,
        strokeWidthPx = 4f,
        allowedRoles = setOf(UserRole.STUDENT, UserRole.TEACHER, UserRole.PRINCIPAL, UserRole.ADMIN)
    ),
    PENCIL(
        displayName = "Pencil Tone",
        defaultColorHex = 0xFF616161,
        strokeWidthPx = 3f,
        allowedRoles = setOf(UserRole.STUDENT, UserRole.TEACHER, UserRole.PRINCIPAL, UserRole.ADMIN)
    ),
    TEACHER_RED(
        displayName = "Teacher Red Pen",
        defaultColorHex = 0xFFE53935,
        strokeWidthPx = 5f,
        allowedRoles = setOf(UserRole.TEACHER, UserRole.PRINCIPAL, UserRole.ADMIN)
    ),
    PRINCIPAL_GREEN(
        displayName = "Principal Green Pen",
        defaultColorHex = 0xFF43A047,
        strokeWidthPx = 5f,
        allowedRoles = setOf(UserRole.PRINCIPAL, UserRole.ADMIN)
    ),
    ERASER(
        displayName = "Stroke Eraser",
        defaultColorHex = 0x00000000,
        strokeWidthPx = 20f,
        isEraser = true,
        allowedRoles = setOf(UserRole.STUDENT, UserRole.TEACHER, UserRole.PRINCIPAL, UserRole.ADMIN)
    )
}

data class StrokePoint(
    val x: Float,
    val y: Float,
    val pressure: Float = 1.0f,
    val timestampMs: Long = 0L
)

data class VectorStroke(
    val id: String,
    val points: List<StrokePoint>,
    val colorHex: Long,
    val strokeWidth: Float,
    val isEraser: Boolean = false,
    val tool: PenTool,
    val authorRole: UserRole,
    val authorName: String,
    val timestampMs: Long
)

data class StrokeDiffCommit(
    val commitId: String,
    val notebookId: String,
    val pageNumber: Int,
    val authorId: String,
    val authorName: String,
    val authorRole: UserRole,
    val commitMessage: String,
    val timestampMs: Long,
    val addedStrokes: List<VectorStroke>,
    val removedStrokeIds: List<String>
)

data class CanvasPage(
    val pageId: String,
    val pageNumber: Int,
    val ruleType: RuleType,
    val strokes: List<VectorStroke> = emptyList(),
    val commits: List<StrokeDiffCommit> = emptyList(),
    val isBookmarked: Boolean = false
)
