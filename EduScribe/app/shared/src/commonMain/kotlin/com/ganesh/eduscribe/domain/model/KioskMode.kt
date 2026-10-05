package com.ganesh.eduscribe.domain.model

data class AllowedUrl(
    val id: String,
    val title: String,
    val url: String,
    val isYouTube: Boolean = false,
    val addedByTeacher: String = "TCH-001"
)

data class KioskConfig(
    val isKioskActive: Boolean = false,
    val title: String = "Classroom Restricted Browser Session",
    val allowedUrls: List<AllowedUrl> = emptyList(),
    val restrictSystemApps: Boolean = true
)
