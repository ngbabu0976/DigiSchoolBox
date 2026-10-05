package com.ganesh.eduscribe.domain.model

data class SchoolConfig(
    val schoolName: String = "St. Xavier's High School",
    val state: String = "Maharashtra",
    val region: String = "Mumbai Central",
    val branchName: String = "Main Campus",
    val logoUrl: String = "https://images.unsplash.com/photo-1580582932707-520aed937b7b?w=200",
    val headerText: String = "Nurturing Mind, Body, & Spirit - Academic Year 2025-26",
    val footerText: String = "Affiliated to CBSE Board | ISO 9001:2015 Certified",
    val contactEmail: String = "admin@stxaviers.edu.in",
    val phone: String = "+91 98765 43210"
)
