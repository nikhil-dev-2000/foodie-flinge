package com.example.foodiefling.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodiefling.R
import com.google.firebase.database.FirebaseDatabase

class PotentialMatches : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserProfileAdapter
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.potential_matches)

        var userId = intent.getStringExtra("user_id")
        recyclerView = findViewById(R.id.recyclerViewContainer)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val db = FirebaseDatabase.getInstance().getReference("Users")

        // Fetch the current user
        if (userId != null) {
            db.child(userId).get().addOnSuccessListener { dataSnapshot ->
                currentUser = dataSnapshot.getValue(User::class.java)!!
                // Initialize adapter with an empty list
                adapter = UserProfileAdapter(emptyList(), userId) {
                    fetchAndDisplayUsers(userId) // Refresh the user list after an action
                }
                recyclerView.adapter = adapter
                fetchAndDisplayUsers(userId)
            }.addOnFailureListener { error ->
                Log.e("PotentialMatches", "Error fetching current user: ${error.message}")
            }
        }
    }

    private fun fetchAndDisplayUsers(userId: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")

        // Fetch the current user from the database before proceeding
        dbRef.child(userId).get().addOnSuccessListener { dataSnapshot ->
            currentUser = dataSnapshot.getValue(User::class.java)!!

            // Now that currentUser is updated, extract matched users
            val matchedUsers = currentUser.matches?.mapNotNull { it.matchedUser } ?: emptyList()
            Log.d("MatchedUsers", "List of matched users: $matchedUsers")

            // Fetch all users from the database
            dbRef.get().addOnSuccessListener { dataSnapshot ->
                val users = mutableListOf<User>()

                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null
                        && user.userId != userId.filter { it.isDigit() }.toInt()
                        && user.userId !in matchedUsers
                    ) {
                        users.add(user)
                    }
                }

                // Sort users by common dishes
                val sortedUsers = getSortedUsers(currentUser, users)

                // Update the adapter data
                adapter.updateUsers(sortedUsers)
            }.addOnFailureListener { error ->
                Log.e("PotentialMatches", "Error fetching users: ${error.message}")
            }
        }.addOnFailureListener { error ->
            Log.e("PotentialMatches", "Error fetching current user: ${error.message}")
        }

        val filter = findViewById<ImageView>(R.id.filter)
        filter.setOnClickListener {
            val intent1 = Intent(this, Filter::class.java)
            intent1.putExtra("user_id", userId)
            startActivity(intent1)
        }

        val messages = findViewById<ImageView>(R.id.messages)
        messages.setOnClickListener {
            val intent2 = Intent(this, ChatSelectActivity::class.java)
            intent2.putExtra("user_id", userId)
            startActivity(intent2)
        }
    }

    private fun getSortedUsers(currentUser: User, users: List<User>): List<User> {
        return users.filter { user ->
            // Filter users based on the number of common dishes
            currentUser.profile?.dish.orEmpty().intersect(user.profile?.dish.orEmpty()).isNotEmpty() &&

                    // Filter users based on preferences
                    (currentUser.preferences?.gender == null || currentUser.preferences.gender == user.gender) &&
                    (currentUser.preferences?.location == null || currentUser.preferences.location == user.profile?.city) &&
                    (currentUser.preferences?.drink == null || currentUser.preferences.drink == user.profile?.drink.toString()) &&
                    (currentUser.preferences?.raceOrEthnicity == null || currentUser.preferences.raceOrEthnicity == user.profile?.raceOrEthnicity) &&
                    (currentUser.preferences?.religion == null || currentUser.preferences.religion == user.profile?.religion) &&

                    // Filter users based on age range
                    isAgeInRange(user.age, currentUser.preferences?.minAge.toString(),
                        currentUser.preferences?.maxAge.toString()
                    )
        }.sortedByDescending { user ->
            // Sort remaining users by the number of common dishes
            currentUser.profile?.dish.orEmpty().intersect(user.profile?.dish.orEmpty()).size
        }
    }

    // Helper function to check if the user's age is within the preferred range
    private fun isAgeInRange(age: Int, minAge: String?, maxAge: String?): Boolean {
        val min = minAge?.toIntOrNull()
        val max = maxAge?.toIntOrNull()
        return (min == null || age >= min) && (max == null || age <= max)
    }

}

