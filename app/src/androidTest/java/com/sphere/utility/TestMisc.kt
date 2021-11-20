package com.sphere.utility

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Test

class TestMisc {
    val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun interpolate_1() {
        val result = interpolate(0f, 1f, 0f, 10f, 0f)

        assert(result == 0f)
    }

    @Test
    fun interpolate_2() {
        val result = interpolate(0f, 1f, 0f, 10f, 1f)

        assert(result == 10f)
    }

    @Test
    fun interpolate_3() {
        val result = interpolate(0f, 1f, 0f, 10f, 0.5f)

        assert(result == 5f)
    }
}