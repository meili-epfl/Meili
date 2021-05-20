package com.github.epfl.meili.home

import android.os.Parcel
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import com.facebook.AccessToken
import org.hamcrest.CoreMatchers.*
import com.facebook.Profile
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class FacebookAuthenticationServiceTest {

    @Test
    fun fetchNull() {
        FacebookAuthenticationService.fetchFacebookUser(null, Profile.getCurrentProfile())
    }

    @Test
    fun fetchWithAccessToken() {
        FacebookAuthenticationService.fetchFacebookUser(AccessToken.getCurrentAccessToken(), Profile.getCurrentProfile())

    }

}