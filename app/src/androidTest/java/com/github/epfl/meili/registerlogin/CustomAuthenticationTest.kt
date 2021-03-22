package com.github.epfl.meili.registerlogin

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import com.github.epfl.meili.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@LargeTest
class CustomAuthenticationTest {

    private val TEST_USERNAME: String = "moderator"
    private val TEST_EMAIL: String = "moderator@gmail.com"
    private val TEST_PASSWORD: String = "123123"
    private val TEST_BAD_EMAIL: String = "mods"
    private val TEST_BAD_PASSWORD: String = "123"


    @get: Rule
    var testRule: ActivityScenarioRule<RegisterActivity> =
        ActivityScenarioRule(RegisterActivity::class.java)

    @Before
    fun setup() {
        CustomAuthentication.setAuthenticationService(CustomMockAuthenticationService())
    }

    @Test
    fun cantRegisterWithoutEmail() {
        CustomAuthentication.authService = CustomMockAuthenticationService()
        CustomAuthentication.setAuthenticationService(CustomMockAuthenticationService())
        testRule.getScenario().onActivity { activity ->
            CustomAuthentication.registerUser(
                activity,
                "",
                TEST_PASSWORD,
                TEST_USERNAME
            )
        }

    }

    @Test
    fun cantRegisterWithBadEmail() {

        testRule.getScenario().onActivity { activity ->
            CustomAuthentication.registerUser(
                activity,
                TEST_BAD_EMAIL,
                TEST_PASSWORD,
                TEST_USERNAME
            )
        }


    }

    @Test
    fun cantRegisterWithoutPassword() {


        testRule.getScenario().onActivity { activity ->
            CustomAuthentication.registerUser(
                activity,
                TEST_EMAIL,
                "",
                TEST_USERNAME
            )
        }


    }

    @Test
    fun cantRegisterWithBadPassword() {


        testRule.getScenario().onActivity { activity ->
            CustomAuthentication.registerUser(
                activity,
                TEST_EMAIL,
                TEST_BAD_PASSWORD,
                TEST_USERNAME
            )
        }


    }

    @Test
    fun cantRegisterWithoutUsername() {


        testRule.getScenario().onActivity { activity ->
            CustomAuthentication.registerUser(
                activity,
                TEST_EMAIL,
                TEST_PASSWORD,
                ""
            )
        }


    }

    @Test
    fun cantLoginWithoutEmail() {


        testRule.getScenario().onActivity { activity ->
            CustomAuthentication.loginUser(
                activity,
                "",
                TEST_PASSWORD,
            )
        }

    }

    @Test
    fun cantLoginWithBadEmail() {

        testRule.getScenario().onActivity { activity ->
            CustomAuthentication.loginUser(
                activity,
                TEST_BAD_EMAIL,
                TEST_PASSWORD,
            )
        }


    }

    @Test
    fun cantLoginWithoutPassword() {


        testRule.getScenario().onActivity { activity ->
            CustomAuthentication.loginUser(
                activity,
                TEST_EMAIL,
                "",
            )
        }


    }

    @Test
    fun cantLoginWithBadPassword() {


        testRule.getScenario().onActivity { activity ->
            CustomAuthentication.loginUser(
                activity,
                TEST_EMAIL,
                TEST_BAD_PASSWORD,
            )
        }


    }

}