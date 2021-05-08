package com.github.epfl.meili.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.github.epfl.meili.R
import com.github.epfl.meili.home.Auth
import com.github.epfl.meili.profile.friends.FriendsListActivity
import com.github.epfl.meili.util.NavigableActivity
import de.hdodenhof.circleimageview.CircleImageView


class ProfileActivity : NavigableActivity(R.layout.activity_profile, R.id.profile) {
    private val launchGallery = registerForActivityResult(ActivityResultContracts.GetContent())
    { viewModel.loadLocalImage(contentResolver, it) }

    private lateinit var photoView: CircleImageView
    private lateinit var nameView: EditText
    private lateinit var bioView: EditText
    private lateinit var saveButton: Button
    private lateinit var seeFriendsButton: Button
    private lateinit var signInButton: Button
    private lateinit var signOutButton: Button

    private lateinit var signedInView: View

    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        photoView = findViewById(R.id.photo)
        nameView = findViewById(R.id.name)
        bioView = findViewById(R.id.bio)
        saveButton = findViewById(R.id.save)
        seeFriendsButton = findViewById(R.id.list_friends_button)
        signInButton = findViewById(R.id.sign_in)
        signOutButton = findViewById(R.id.sign_out)

        signedInView = findViewById(R.id.signed_in)

        Auth.isLoggedIn.observe(this) {
            verifyAndUpdateUserIsLoggedIn()
        }

        if (!Auth.isLoggedIn.value!!) {
            Auth.signIn(this)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, ProfileViewModelFactory(Auth.getCurrentUser()!!))
                .get(ProfileViewModel::class.java)
        viewModel.getUser().removeObservers(this)
        viewModel.getUser().observe(this) { user ->
            nameView.setText(user.username)
            bioView.setText(user.bio)

            if(nameView.text.isEmpty()){
                nameView.setText(Auth.getCurrentUser()!!.username)
            }
        }
        viewModel.getRequestCreator().removeObservers(this) //TODO: why remove observers?? I want to reuse the view model in other parts of the code is it still fine?
        viewModel.getRequestCreator().observe(this) { it.into(photoView) }
    }

    fun onProfileButtonClick(view: View) {
        when (view) {
            photoView -> launchGallery.launch("image/*")
            saveButton -> saveProfile()
            seeFriendsButton -> showFriends()
            signInButton -> Auth.signIn(this)
            signOutButton -> Auth.signOut()
        }
    }

    private fun showFriends() {
        val intent = Intent(this, FriendsListActivity::class.java)
        startActivity(intent)
    }

    private fun saveProfile() {
        val user = Auth.getCurrentUser()!!
        user.username = nameView.text.toString()
        user.bio = bioView.text.toString()
        viewModel.updateProfile(user)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Auth.onActivityResult(this, requestCode, resultCode, data)
    }

    private fun verifyAndUpdateUserIsLoggedIn() {
        if (Auth.isLoggedIn.value!!) {
            setupViewModel()
            supportActionBar?.title = ""
            signedInView.visibility = View.VISIBLE
            signInButton.visibility = View.GONE
        } else {
            supportActionBar?.title = "Not Signed In"
            signedInView.visibility = View.GONE
            signInButton.visibility = View.VISIBLE
        }
    }
}