package com.healthos.app

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test

private interface Clock {
    fun now(): Long
}

class ExampleUnitTest {

    @Test
    fun `mockk is wired and returns stubbed value`() {
        val clock = mockk<Clock>()
        every { clock.now() } returns 42L

        val result = clock.now()

        assertEquals(42L, result)
        verify(exactly = 1) { clock.now() }
    }
}
