package com.github.epfl.meili.models


import android.os.Parcel
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.epfl.meili.MainActivity
import org.hamcrest.CoreMatchers.`is`
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule


class UserUnitTest {

    private val TEST_UID: String = "test_uid"
    private val TEST_USERNAME: String = "moderator"
    private val TEST_EMAIL: String = "moderator@meili.com"

    @get:Rule
    var testRule: ActivityScenarioRule<MainActivity> =
            ActivityScenarioRule(MainActivity::class.java)


    @Test
    fun userConstructor() {

        var user = User(TEST_UID, TEST_USERNAME, TEST_EMAIL)
        assertThat(user.uid, `is`(TEST_UID))
        assertThat(user.component1(), `is`(TEST_UID))
        assertThat(user.username, `is`(TEST_USERNAME))
        assertThat(user.component2(), `is`(TEST_USERNAME))
        assertThat(user.email, `is`(TEST_EMAIL))
        assertThat(user.component3(), `is`(TEST_EMAIL))


        user.copy(TEST_UID, TEST_USERNAME, TEST_EMAIL)
        // user.writeToParcel(Parcel.obtain(), 0)
    }

    @Test
    fun describeContentDefaultIsZero(){
        var user = User(TEST_UID, TEST_USERNAME, TEST_EMAIL)


        assertThat(user.describeContents(), `is`(0))
    }

    @Test
    fun hashCodeIsConsistent(){
        var user = User(TEST_UID, TEST_USERNAME, TEST_EMAIL)

        var otherUser = User(TEST_UID, TEST_USERNAME, TEST_EMAIL)


        assertThat(user.hashCode(), `is`(otherUser.hashCode()))
    }

    @Test
    fun toStringShowsFields(){
        var user = User(TEST_UID, TEST_USERNAME, TEST_EMAIL)


        assertThat(user.toString(), `is`("User(uid=test_uid, username=moderator, email=moderator@meili.com)"))
    }

    @Test
    fun equalsBehavesAsExpected(){
        var user = User(TEST_UID, TEST_USERNAME, TEST_EMAIL)

        var otherSameUser = User(TEST_UID, TEST_USERNAME, TEST_EMAIL)
        assertThat(user.equals(otherSameUser), `is`(true))
        var otherUser = User("", TEST_USERNAME, TEST_EMAIL)
        assertThat(user.equals(otherUser), `is`(false))
        var nullUser = null
        assertThat(user.equals(nullUser), `is`(false))
    }

}