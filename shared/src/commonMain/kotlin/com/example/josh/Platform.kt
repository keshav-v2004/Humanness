package com.example.josh

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform