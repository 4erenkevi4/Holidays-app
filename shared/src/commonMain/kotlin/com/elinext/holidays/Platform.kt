package com.elinext.holidays

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform