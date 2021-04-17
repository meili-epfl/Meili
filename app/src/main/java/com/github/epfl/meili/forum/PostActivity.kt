package com.github.epfl.meili.forum

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.R
import com.github.epfl.meili.models.Post
import com.github.epfl.meili.storage.FirebaseStorageService
import com.squareup.picasso.Picasso

class PostActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        val post: Post = intent.getParcelableExtra("Post")!!
        postId = intent.getStringExtra("PostId")!!

        val authorView: TextView = findViewById(R.id.post_author)
        val titleView: TextView = findViewById(R.id.post_title)
        val textView: TextView = findViewById(R.id.post_text)
        imageView = findViewById(R.id.post_image)

        authorView.text = post.author
        titleView.text = post.title
        textView.text = post.text

        FirebaseStorageService.getDownloadUrl("forum/$postId", { uri -> getDownloadUrlCallback(uri)})
    }

    private fun getDownloadUrlCallback(uri: Uri) {
        Picasso.get().load(uri).into(imageView)
    }
}