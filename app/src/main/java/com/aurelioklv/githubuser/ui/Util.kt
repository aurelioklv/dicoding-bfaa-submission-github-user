package com.aurelioklv.githubuser.ui

fun formatCount(count: Long): String {
    return when {
        count < 1000L -> count.toString()
        count < 100000L -> String.format("%.1fk", count / 1000.0)
        count < 1000000L -> String.format("%dk", count / 1000L)
        else -> String.format("%.1fM", count / 1000000L)
    }
}