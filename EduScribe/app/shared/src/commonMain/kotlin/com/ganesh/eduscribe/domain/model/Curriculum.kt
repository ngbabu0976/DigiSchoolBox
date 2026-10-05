package com.ganesh.eduscribe.domain.model

enum class ResourceType(val label: String) {
    TEXTBOOK("Textbook"),
    STUDY_MATERIAL("Study Material"),
    SOCIAL_MAP("Social World Map"),
    GRAPH_PAGE("Graph Page")
}

data class DigitalResource(
    val id: String,
    val title: String,
    val subjectId: String,
    val className: String,
    val type: ResourceType,
    val chapterNumber: Int,
    val totalPages: Int,
    val fileUrl: String,
    val description: String = ""
)

data class Chapter(
    val id: String,
    val number: Int,
    val title: String,
    val resources: List<DigitalResource> = emptyList()
)

data class Section(
    val id: String,
    val name: String, // e.g. "Section A"
    val studentCount: Int = 30
)

data class SchoolClass(
    val id: String,
    val name: String, // e.g. "Class 10"
    val sections: List<Section>
)

data class Subject(
    val id: String,
    val name: String, // e.g. "Mathematics", "Science"
    val iconName: String = "book",
    val classes: List<SchoolClass>,
    val chapters: List<Chapter>
)
