package com.github.epfl.meili.models

import com.github.epfl.meili.models.User
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UserUnitTest {

    private val TEST_UID: String = "firuh2oirhg29g8h2fr2g"
    private val TEST_USERNAME: String = "moderator"


    @Test
    fun userDefaultConstructor() {
        var user = User()
        assertThat(user.uid, `is`(""))
        assertThat(user.username, `is`(""))

    }

    @Test
    fun userConstructor() {

        var user = User(TEST_UID, TEST_USERNAME)
        assertThat(user.uid, `is`(TEST_UID))
        assertThat(user.username, `is`(TEST_USERNAME))
        assertThat(user.describeContents(), `is`(0))

    }


}