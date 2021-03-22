package com.github.epfl.meili.registerlogin

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
class CustomFirebaseAuthenticationServiceTest {

    private val TEST_USERNAME: String = "moderator"
    private val TEST_EMAIL: String = "moderator99@gmail.com"
    private val TEST_PASSWORD: String = "123123"
    private val TEST_BAD_PASSWORD: String = "123"
    private val LOGIN_EMAIL: String = "moderator2@gmail.com"
    private val LOGIN_PASSWORD: String = "123123"

    @get: Rule
    var testRule: ActivityScenarioRule<RegisterActivity> =
        ActivityScenarioRule(RegisterActivity::class.java)

    var firebaseCustomService = CustomFirebaseAuthenticationService()

    @Before
    fun setup(){
        firebaseCustomService.init()
    }

    @Test
    fun registerBobToDatabase(){

        testRule.getScenario().onActivity { activity ->
            firebaseCustomService.saveUserToFirebaseDatabase(
                activity,
                "Bob"
            )
        }

    }

    @Test
    fun registerAddsAndRemovesUser(){

        testRule.getScenario().onActivity { activity ->
            firebaseCustomService.createUser(
                activity,
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_USERNAME
            )
        }

        testRule.getScenario().onActivity { activity ->
            firebaseCustomService.createUser(
                activity,
                TEST_EMAIL,
                TEST_PASSWORD,
                TEST_USERNAME
            )
        }

        FirebaseAuth.getInstance().currentUser?.delete()

    }

    @Test
    fun loginWithAvailableUser(){
        testRule.getScenario().onActivity { activity ->
            firebaseCustomService.signInWithEmailAndPassword(
                activity,
                LOGIN_EMAIL,
                LOGIN_PASSWORD
            )
        }
    }

    @Test
    fun loginWithWrongPasswordFails(){
        testRule.getScenario().onActivity { activity ->
            firebaseCustomService.signInWithEmailAndPassword(
                activity,
                LOGIN_EMAIL,
                TEST_BAD_PASSWORD
            )
        }

    }
}