package com.ganesh.eduscribe.domain.model

enum class AnalyticsCategory(val label: String) {
    TEXTBOOK("Textbooks"),
    STUDY_MATERIAL("Study Materials"),
    CLASSWORK_NOTEBOOK("Classwork Notebooks"),
    HOMEWORK_NOTEBOOK("Homework Notebooks"),
    PRACTICE_NOTEBOOK("Practice Notebooks"),
    TEST_NOTEBOOK("Test Notebooks")
}

data class TimeSpentRecord(
    val id: String,
    val studentId: String,
    val category: AnalyticsCategory,
    val itemTitle: String,
    val durationSeconds: Long,
    val dateString: String // YYYY-MM-DD
)

data class DailyAnalytics(
    val dateString: String,
    val totalTimeSeconds: Long,
    val textbookSeconds: Long,
    val studyMaterialSeconds: Long,
    val classworkSeconds: Long,
    val homeworkSeconds: Long,
    val practiceSeconds: Long,
    val testSeconds: Long,
    val records: List<TimeSpentRecord> = emptyList()
)
