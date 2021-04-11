package com.github.epfl.meili.forum

import android.os.Parcel
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.epfl.meili.MainActivity
import com.github.epfl.meili.models.Post
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PostClassTest {

    @get:Rule
    var testRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    private val TEST_TEXT = "test text"
    private val TEST_TITLE = "test title"
    private val TEST_USERNAME = "test_username"
    private val TEST_ID = "1"



    @Test
    fun userConstructor() {

        var post = Post(TEST_ID, TEST_USERNAME, TEST_TITLE, TEST_TEXT)
        assertThat(post.id, `is`(TEST_ID))
        assertThat(post.component1(), `is`(TEST_ID))
        assertThat(post.author, `is`(TEST_USERNAME))
        assertThat(post.component2(), `is`(TEST_USERNAME))
        assertThat(post.title, `is`(TEST_TITLE))
        assertThat(post.component3(), `is`(TEST_TITLE))
        assertThat(post.text, `is`(TEST_TEXT))
        assertThat(post.component4(), `is`(TEST_TEXT))


        post.copy(TEST_ID, TEST_USERNAME, TEST_TITLE, TEST_TEXT)
        post.writeToParcel(Parcel.obtain(), 0)
    }

    @Test
    fun describeContentDefaultIsZero(){
        var post = Post(TEST_ID, TEST_USERNAME, TEST_TITLE, TEST_TEXT)


        assertThat(post.describeContents(), `is`(0))
    }

    @Test
    fun hashCodeIsConsistent(){
        var post = Post(TEST_ID, TEST_USERNAME, TEST_TITLE, TEST_TEXT)

        var otherPost = Post(TEST_ID, TEST_USERNAME, TEST_TITLE, TEST_TEXT)


        assertThat(post.hashCode(), `is`(otherPost.hashCode()))
    }

    @Test
    fun toStringShowsFields(){
        var post = Post(TEST_ID, TEST_USERNAME, TEST_TITLE, TEST_TEXT)


        print(post.toString())
        assertThat(post.toString(), `is`("Post(id=1, author=test_username, title=test title, text=test text)"))
    }

    @Test
    fun equalsBehavesAsExpected(){
        var post = Post(TEST_ID, TEST_USERNAME, TEST_TITLE, TEST_TEXT)
        var otherSamepost = Post(TEST_ID, TEST_USERNAME, TEST_TITLE, TEST_TEXT)
        var otherPost = Post(TEST_ID, TEST_USERNAME, "", TEST_TEXT)
        var nullPost = null


        assertThat(post.equals(otherSamepost), `is`(true))
        assertThat(post.equals(otherPost), `is`(false))
        assertThat(post.equals(nullPost ), `is`(false))
    }
}