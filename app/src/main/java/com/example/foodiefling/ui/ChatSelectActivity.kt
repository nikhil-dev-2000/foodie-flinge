package com.example.foodiefling.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodiefling.R
import com.example.foodiefling.ui.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatSelectActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: ChatUserAdapter
    private lateinit var mDbRef: DatabaseReference
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.chat_activity_main)

//        mAuth = FirebaseAuth.getInstance()
        val intent = intent
        val userId = intent.getStringExtra("user_id")

        mDbRef = FirebaseDatabase.getInstance().getReference("Users")
        if (userId != null) {
            mDbRef.child(userId).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    // Extract user data
                    currentUser = dataSnapshot.getValue(User::class.java)!!
                    // Check if user is not null
                    currentUser.let {
                        Log.d("User Retrieval", "User Data: $it")
                        Log.d("User Retrieval", "User Data: $currentUser")

                        userList = ArrayList()
                        adapter = userId?.let { ChatUserAdapter(this, userList, it) }!!
                        // Initialize the RecyclerView and set the adapter with the user list from firebase
                        userRecyclerView = findViewById(R.id.userRecyclerView)
                        userRecyclerView.adapter = adapter
                        userRecyclerView.layoutManager = LinearLayoutManager(this)

                        //add the users to the list
                        val matchedUsers = currentUser.matches?.mapNotNull { it.matchedUser } ?: emptyList()
                        mDbRef.get().addOnSuccessListener { dataSnapshot ->
                            val users = mutableListOf<User>()

                            for (userSnapshot in dataSnapshot.children) {
                                val user = userSnapshot.getValue(User::class.java)
                                if (userId != null) {
                                    if (user != null
                                        && user.userId != userId.filter { it.isDigit() }.toInt()
                                        && user.userId in matchedUsers
                                    ) {
                                        userList.add(user)
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged()
                        }.addOnFailureListener { error ->
                            Log.e("PotentialMatches", "Error fetching users: ${error.message}")
                        }
                    }
                } else {
                    // Handle the case where the user does not exist
                    Log.e("User Retrieval", "User  not found")
                }
            }.addOnFailureListener { error ->
                // Handle any errors that occur during the retrieval
                Log.e("User Retrieval", "Error retrieving user data: ${error.message}")
            }
        }

//        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                userList.clear() // Clear the list before adding new users
//                for (postSnapshot in snapshot.children){
//                    val currentUser = postSnapshot.getValue(ChatUser::class.java) // change to User
//                    if (mAuth.currentUser?.uid != currentUser?.uid){     // get all matched users into userList
//                        userList.add(currentUser!!)
//                    }
//                    adapter.notifyDataSetChanged()
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.btn_logout) {
//            mAuth.signOut()
//            val intent = Intent(this@ChatSelectActivity, Login::class.java)
//            finish()
//            startActivity(intent)
//            return true
//        }
//        return true
//    }
}