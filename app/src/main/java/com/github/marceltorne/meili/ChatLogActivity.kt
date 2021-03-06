package com.github.marceltorne.meili

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ChatLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        //test commits
        supportActionBar?.title = "Chat Log"
        //another comment
    }
}