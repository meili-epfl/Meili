package com.github.epfl.meili

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class ContainerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

       supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout, ProfileActivity())
            .commit()
    }
}