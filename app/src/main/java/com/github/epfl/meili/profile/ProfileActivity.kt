package com.github.epfl.meili.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.github.epfl.meili.R
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.profile.favoritepois.FavoritePoisActivity
import com.github.epfl.meili.profile.friends.FriendsListActivity
import com.github.epfl.meili.util.NavigableActivity
import com.github.epfl.meili.util.UIUtility
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.hdodenhof.circleimageview.CircleImageView


class ProfileActivity : NavigableActivity(R.layout.activity_profile, R.id.profile) {
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
    private lateinit var signOutButton: Button
    private lateinit var commentsButton: ImageButton
    private lateinit var postsButton: ImageButton
    private lateinit var reviewsButton: ImageButton
    private lateinit var favoritePoisButton: ImageButton

    private lateinit var signedInView: View
    private lateinit var profileView: View
    private lateinit var profileEditView: View

    private lateinit var viewModel: ProfileViewModel

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

        profileUid = intent.getStringExtra(USER_KEY)
        if (profileUid == null) {
            profileUid = Auth.getCurrentUser()!!.uid // By default profile we are seeing is ours
        }

        Auth.isLoggedIn.observe(this) {
            verifyAndUpdateUserIsLoggedIn()
        }
        if (!Auth.isLoggedIn.value!!) {
            Auth.signIn(this)
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

        profileEditButton = findViewById(R.id.profile_edit_button)
        saveButton = findViewById(R.id.save)
        cancelButton = findViewById(R.id.cancel)
        seeFriendsButton = findViewById(R.id.list_friends_button)
        signInButton = findViewById(R.id.sign_in)
        signOutButton = findViewById(R.id.sign_out)
        commentsButton = findViewById(R.id.profile_comments_button)
        postsButton = findViewById(R.id.profile_posts_button)
        reviewsButton = findViewById(R.id.profile_reviews_button)
        favoritePoisButton = findViewById(R.id.profile_poi_history_button)

        signedInView = findViewById(R.id.signed_in)
        profileView = findViewById(R.id.profile_container)
        profileEditView = findViewById(R.id.profile_edit_container)
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
            seeFriendsButton -> showFriends()
            signInButton -> Auth.signIn(this)
            signOutButton -> Auth.signOut()
            profileEditButton -> showEditMode()
            favoritePoisButton -> showFavoritePoi()
        }
    }

    private fun showFriends() {
        val intent = Intent(this, FriendsListActivity::class.java)
        startActivity(intent)
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

        val editableVisibility = if (isProfileOwner) View.VISIBLE else View.GONE
        seeFriendsButton.visibility = editableVisibility
        signOutButton.visibility = editableVisibility
        profileEditButton.visibility = editableVisibility
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
        Auth.onActivityResult(this, requestCode, resultCode, data)
    }

    private fun verifyAndUpdateUserIsLoggedIn() {
        if (Auth.isLoggedIn.value!!) {
            supportActionBar?.title = SUPPORT_ACTIONBAR_SIGNED_IN
            signedInView.visibility = View.VISIBLE
            signInButton.visibility = View.GONE

            setupViewModel()
            updateIsProfileOwner()
            showProfile()
        } else {
            supportActionBar?.title = SUPPORT_ACTIONBAR_NOT_SIGNED_IN
            signedInView.visibility = View.GONE
            signInButton.visibility = View.VISIBLE
            signOutButton.visibility = View.GONE
        }
    }

    private fun updateIsProfileOwner() {
        val authUser = Auth.getCurrentUser()!!
        isProfileOwner = (authUser.uid == profileUid)
    }

    private fun showFavoritePoi() {
        val intent = Intent(this, FavoritePoisActivity::class.java)
            .putExtra(USER_KEY, profileUid)
        startActivity(intent)
    }
}
