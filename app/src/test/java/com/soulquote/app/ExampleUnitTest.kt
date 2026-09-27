package com.soulquote.app

import com.soulquote.app.core.util.Resource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun resourceSuccess_holdsDataCorrectly() {
        val resource = Resource.Success("SoulQuote Initialized")
        assertEquals("SoulQuote Initialized", resource.data)
    }

    @Test
    fun resourceError_holdsMessageCorrectly() {
        val resource = Resource.Error<String>("Failed to load")
        assertEquals("Failed to load", resource.message)
    }
}
