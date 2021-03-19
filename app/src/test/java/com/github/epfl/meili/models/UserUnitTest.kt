package com.github.epfl.meili.models

import com.github.epfl.meili.models.User
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test

import org.junit.Assert.*


class UserUnitTest {

    private val TEST_UID: String = "test_uid"
    private val TEST_USERNAME: String = "moderator"



    @Test
    fun userConstructor() {

        var user = User(TEST_UID, TEST_USERNAME)
        assertThat(user.uid, `is`(TEST_UID))
        assertThat(user.username, `is`(TEST_USERNAME))
        assertThat(user.describeContents(), `is`(0))

    }


}