package com.ganesh.eduscribe.domain.model

enum class MathToolType(val displayName: String) {
    NONE("None"),
    RULER("Ruler / Scale (cm)"),
    PROTRACTOR("Protractor (360°)"),
    COMPASS("Compass & Arc Drawer")
}

data class MathToolState(
    val activeTool: MathToolType = MathToolType.NONE,
    val centerXPx: Float = 300f,
    val centerYPx: Float = 300f,
    val widthPx: Float = 500f,
    val heightPx: Float = 120f,
    val rotationDegrees: Float = 0f,
    val radiusPx: Float = 180f
)
