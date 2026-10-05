package com.ganesh.eduscribe.domain.model

data class McqOption(
    val id: String,
    val optionText: String,
    val isCorrect: Boolean = false
)

data class McqQuestion(
    val id: String,
    val questionText: String,
    val options: List<McqOption>,
    val marks: Int = 1,
    val explanation: String = ""
)

data class ExamPaper(
    val id: String,
    val title: String,
    val subjectId: String,
    val subjectName: String,
    val targetClass: String,
    val targetSection: String,
    val durationMinutes: Int = 30,
    val totalMarks: Int = 10,
    val questions: List<McqQuestion> = emptyList(),
    val isExamActive: Boolean = false
)

data class ExamSubmission(
    val id: String,
    val examId: String,
    val studentId: String,
    val studentName: String,
    val answersMap: Map<String, String>, // questionId -> selectedOptionId
    val scoreObtained: Int,
    val totalMarks: Int,
    val percentage: Float,
    val submittedAtMs: Long
)

data class ExamLockdownState(
    val isExamActiveGlobally: Boolean = false,
    val activeExamPaper: ExamPaper? = null,
    val activeSectionId: String? = null,
    val lockTextbooks: Boolean = true,
    val lockNotebooks: Boolean = true,
    val lockStudyMaterials: Boolean = true
)
