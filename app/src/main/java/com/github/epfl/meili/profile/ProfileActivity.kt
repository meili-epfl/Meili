package com.github.epfl.meili.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.github.epfl.meili.R
import com.github.epfl.meili.auth.Auth
import com.github.epfl.meili.auth.FacebookAuthenticationService
import com.github.epfl.meili.profile.favoritepois.FavoritePoisActivity
import com.github.epfl.meili.profile.friends.FriendsListActivity
import com.github.epfl.meili.profile.myposts.MyPostsActivity
import com.github.epfl.meili.util.UIUtility
import com.github.epfl.meili.util.UserPreferences
import com.github.epfl.meili.util.navigation.HomeActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView


class ProfileActivity : HomeActivity(R.layout.activity_profile, R.id.profile_activity) {
    private val launchGallery = registerForActivityResult(ActivityResultContracts.GetContent())
    { viewModel.loadLocalImage(contentResolver, it) }

    private lateinit var photoView: CircleImageView

    private lateinit var photoEditView: FloatingActionButton
    private lateinit var nameView: TextView
    private lateinit var bioView: TextView
    private lateinit var nameEditView: EditText
    private lateinit var bioEditView: EditText

    private lateinit var profileEditButton: FloatingActionButton
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var seeFriendsButton: ImageButton
    private lateinit var signInButton: Button
    private lateinit var facebookSignInButton: LoginButton
    private lateinit var signOutButton: Button
    private lateinit var postsButton: ImageButton
    private lateinit var favoritePoisButton: ImageButton
    private lateinit var lightDarkModeButton: ImageButton

    private lateinit var signedInView: View
    private lateinit var profileView: View
    private lateinit var profileEditView: View

    private lateinit var viewModel: ProfileViewModel

    private lateinit var callbackManager: CallbackManager
    private var isProfileOwner = false
    private var profileUid: String? = null

    companion object {
        private const val SUPPORT_ACTIONBAR_SIGNED_IN = ""
        private const val SUPPORT_ACTIONBAR_NOT_SIGNED_IN = "Not Signed In"
        private const val STORAGE_IMAGES_PATH = "image/*"
        const val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Auth.isLoggedIn.observe(this) {
            verifyAndUpdateUserIsLoggedIn()
        }

        profileUid = intent.getStringExtra(USER_KEY)
        if (profileUid == null) {
            if (Auth.isLoggedIn.value!!) {
                profileUid = Auth.getCurrentUser()!!.uid // By default profile we are seeing is ours
            }
        }

        initializeViews()
    }

    private fun initializeViews() {
        photoView = findViewById(R.id.photo)
        photoEditView = findViewById(R.id.photo_edit)
        nameView = findViewById(R.id.profile_name)
        bioView = findViewById(R.id.profile_bio)
        nameEditView = findViewById(R.id.profile_edit_name)
        bioEditView = findViewById(R.id.profile_edit_bio)

        initializeButtons()

        signedInView = findViewById(R.id.signed_in)

        registerFacebookCallBack()

        profileView = findViewById(R.id.profile_container)
        profileEditView = findViewById(R.id.profile_edit_container)
    }

    private fun initializeButtons() {
        profileEditButton = findViewById(R.id.profile_edit_button)
        saveButton = findViewById(R.id.save)
        cancelButton = findViewById(R.id.cancel)
        seeFriendsButton = findViewById(R.id.list_friends_button)
        signInButton = findViewById(R.id.sign_in)
        facebookSignInButton = findViewById(R.id.facebook_sign_in)
        signOutButton = findViewById(R.id.sign_out)
        postsButton = findViewById(R.id.profile_posts_button)
        favoritePoisButton = findViewById(R.id.profile_favorite_pois_button)
        lightDarkModeButton = findViewById(R.id.switch_mode)
    }

    private fun registerFacebookCallBack() {
        callbackManager = CallbackManager.Factory.create()

        facebookSignInButton.registerCallback(
                callbackManager, facebookCallback
        )
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, ProfileViewModelFactory(profileUid!!))
                .get(ProfileViewModel::class.java)
        viewModel.getUser().removeObservers(this)
        viewModel.getUser().observe(this) { user ->
            nameView.text = user.username
            bioView.text = user.bio

            if (nameView.text.isEmpty()) {

                nameView.text = Auth.getCurrentUser()!!.username
            }
        }
        viewModel.getRequestCreator().removeObservers(this)
        viewModel.getRequestCreator().observe(this) { it.into(photoView) }
    }

    /** Buttons callback function */
    fun onProfileButtonClick(view: View) {
        when (view) {
            photoEditView -> launchGallery.launch(STORAGE_IMAGES_PATH)
            saveButton -> saveProfile()
            cancelButton -> showProfile()

            seeFriendsButton -> showProfileOwnersInfo(FriendsListActivity::class.java)
            signInButton -> Auth.signInIntent(this)
            signOutButton -> {
                Auth.signOut(); profileUid = null
            }
            profileEditButton -> showEditMode()
            postsButton -> showProfileOwnersInfo(MyPostsActivity::class.java)
            favoritePoisButton -> showProfileOwnersInfo(FavoritePoisActivity::class.java)
            lightDarkModeButton -> changeMode()
        }
    }

    private fun saveProfile() {
        val user = Auth.getCurrentUser()!!
        user.username = nameEditView.text.toString()
        user.bio = bioEditView.text.toString()
        viewModel.updateProfile(user)

        showProfile()
    }

    private fun showProfile() {
        UIUtility.hideSoftKeyboard(this)

        profileView.visibility = View.VISIBLE
        profileEditView.visibility = View.GONE
        photoEditView.visibility = View.GONE

        seeFriendsButton.isVisible = isProfileOwner
        signOutButton.isVisible = isProfileOwner
        profileEditButton.isVisible = isProfileOwner
    }

    private fun showEditMode() {
        nameEditView.setText(nameView.text)
        bioEditView.setText(bioView.text)

        profileView.visibility = View.GONE
        seeFriendsButton.visibility = View.GONE
        signOutButton.visibility = View.GONE
        profileEditView.visibility = View.VISIBLE
        photoEditView.visibility = View.VISIBLE
        profileEditButton.visibility = View.GONE
        photoEditView.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Auth.onActivityResult(this, requestCode, resultCode, data) {}
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun verifyAndUpdateUserIsLoggedIn() {
        if (Auth.isLoggedIn.value!! || profileUid != null) {
            supportActionBar?.title = SUPPORT_ACTIONBAR_SIGNED_IN
            signedInView.visibility = View.VISIBLE
            signInButton.visibility = View.GONE
            facebookSignInButton.visibility = View.GONE

            if (profileUid == null) {
                profileUid = Auth.getCurrentUser()!!.uid
            }

            setupViewModel()
            updateIsProfileOwner()
            showProfile()
        } else {

            supportActionBar?.title = SUPPORT_ACTIONBAR_NOT_SIGNED_IN
            signedInView.visibility = View.GONE
            signInButton.visibility = View.VISIBLE
            facebookSignInButton.visibility = View.VISIBLE
            signOutButton.visibility = View.GONE
        }
    }


    private fun updateIsProfileOwner() {
        if (Auth.isLoggedIn.value!!) {
            val authUser = Auth.getCurrentUser()!!
            isProfileOwner = (authUser.uid == profileUid)
        } else {
            isProfileOwner = false
        }
    }

    private fun showProfileOwnersInfo(activityClass: Class<out AppCompatActivity>) {
        val intent = Intent(this, activityClass)
                .putExtra(USER_KEY, profileUid)
        startActivity(intent)
    }

    private fun changeMode() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose Mode")
        val styles = arrayOf("System default", "Light", "Dark")

        val preferences = UserPreferences(this)

        builder.setSingleChoiceItems(styles, preferences.darkMode) { dialog, which ->
            preferences.applyMode(which)
            preferences.darkMode = which
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private val facebookCallback = object : FacebookCallback<LoginResult> {
        private lateinit var profileTracker: ProfileTracker

        override fun onSuccess(loginResult: LoginResult?) {
            if (Profile.getCurrentProfile() == null) {
                profileTracker = object : ProfileTracker() {
                    override fun onCurrentProfileChanged(
                            oldProfile: Profile?,
                            currentProfile: Profile
                    ) {
                        Auth.setAuthenticationService(FacebookAuthenticationService())
                        profileTracker.stopTracking()
                        viewModel.updateProfile(Auth.getCurrentUser()!!)
                    }
                }
            } else {
                Auth.setAuthenticationService(FacebookAuthenticationService())
            }
        }

        override fun onCancel() {}

        override fun onError(exception: FacebookException) {}
    }
}