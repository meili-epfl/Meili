package com.github.marceltorne.meili

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"


        fetchUsers()
    }

    private fun fetchUsers() {
        val ref = Firebase.database.getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach{
                    Log.d("NewMessage", "usernames: ${it.value.toString()}")
                    if(it.value != null){
                        adapter.add(UserItem(it.value.toString()))
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    startActivity(intent)

                    finish()
                }
                findViewById<RecyclerView>(R.id.recyclerview_newmessage).adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}

class UserItem(val username: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.username_text_view_newmessage).text = username
    }

}