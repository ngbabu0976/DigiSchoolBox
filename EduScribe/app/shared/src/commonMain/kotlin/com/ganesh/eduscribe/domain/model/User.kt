package com.ganesh.eduscribe.domain.model

enum class UserRole(val displayName: String) {
    ADMIN("Administrator"),
    PRINCIPAL("Principal"),
    TEACHER("Teacher"),
    STUDENT("Student"),
    PARENT("Parent")
}

data class User(
    val id: String,
    val username: String,
    val fullName: String,
    val role: UserRole,
    val email: String,
    val schoolId: String = "SCH-1001",
    val schoolName: String = "Delhi Public School",
    val assignedClass: String? = null,
    val assignedSection: String? = null,
    val studentIdRef: String? = null // For parent user linking to student
)
