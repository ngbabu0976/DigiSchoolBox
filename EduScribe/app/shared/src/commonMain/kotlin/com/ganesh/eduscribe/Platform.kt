package com.ganesh.eduscribe

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform