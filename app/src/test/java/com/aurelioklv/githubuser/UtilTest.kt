package com.aurelioklv.githubuser

import com.aurelioklv.githubuser.ui.formatCount
import org.junit.Assert.assertEquals
import org.junit.Test

class UtilTest {
    @Test
    fun util() {
        assertEquals("15.9k", formatCount(15872L))
        assertEquals("161k", formatCount(161730L))
    }
}