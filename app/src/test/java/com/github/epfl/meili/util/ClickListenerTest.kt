package com.github.epfl.meili.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ClickListenerTest {
    companion object{
        private const val BUTTON_TEST = 1234
        private const val TEST_UID = "uidtest"
    }

    @Test
    fun onClickedTest(){
        val listener: ClickListener = MockClickListener()
        listener.onClicked(BUTTON_TEST, TEST_UID)
    }

    class MockClickListener: ClickListener {
        override fun onClicked(buttonId: Int, info: String) {
            assertEquals(buttonId, BUTTON_TEST)
            assertEquals(info, TEST_UID)

        }

    }
}