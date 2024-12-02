package com.example.foodiefling.ui

import ImageSliderAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.foodiefling.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserProfileAdapter(private var users: List<User>, private val userId: String, private val onLikeAction: () -> Unit) :
    RecyclerView.Adapter<UserProfileAdapter.UserViewHolder>() {

    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageSlider: ViewPager2 = view.findViewById(R.id.imageSlider)
        val userNameAge: TextView = view.findViewById(R.id.userNameAge)
        val whatLookingFor: TextView = view.findViewById(R.id.whatLookingFor)
        val knowMore: TextView = view.findViewById(R.id.knowMore)
        val like: ImageView = view.findViewById(R.id.like)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_profile_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.userNameAge.text = "${user.firstName} ${user.lastName}, ${user.age}"
        holder.whatLookingFor.text = "Looking for: ${user.profile?.whatLookingFor ?: "Not specified"}"

        // Set up image slider
        user.profile?.photos?.let { photos ->
            holder.imageSlider.adapter = ImageSliderAdapter(photos)
        }

        // Handle "Know more" click
        holder.knowMore.setOnClickListener {
            // Navigate to detailed profile view (implement navigation logic here)
        }
        holder.like.setOnClickListener {
            // Handle like button click
            lateinit var currentUser: User

            val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

            db.child(userId).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    // Extract user data
                    currentUser = dataSnapshot.getValue(User::class.java)!!
                    // Check if user is not null
                    currentUser.let {
                        Log.d("User Retrieval", "User Data: $it")
                        Log.d("User Retrieval", "User Data: $currentUser")
                        val currentUserCuisines = currentUser.profile?.cuisine ?: emptyList()
                        val currentUserDishes = currentUser.profile?.dish ?: emptyList()

                        // Extract cuisines and dishes from the other user
                        val userCuisines = user.profile?.cuisine ?: emptyList()
                        val userDishes = user.profile?.dish ?: emptyList()

                        // Find common cuisines and dishes
                        val commonCuisines = currentUserCuisines.intersect(userCuisines).toMutableList()
                        val commonDishes = currentUserDishes.intersect(userDishes).toMutableList()

                        user.matches?.add(Match(currentUser.userId, "0", commonCuisines, commonDishes))
                        currentUser.matches?.add(Match(user.userId, "0", commonCuisines, commonDishes))
                        db.child(userId).setValue(currentUser).addOnSuccessListener {
                            Log.d("User upload", "User Data: ${db.child(userId)}")
                        }
                        db.child("User_${user.userId}").setValue(user).addOnSuccessListener {
                            Log.d("User upload", "User Data: ${db.child(user.userId.toString())}")
                        }

                        onLikeAction()
                    }
                } else {
                    // Handle the case where the user does not exist
                    Log.e("User Retrieval", "User  not found")
                }
            }.addOnFailureListener { error ->
                // Handle any errors that occur during the retrieval
                Log.e("User Retrieval", "Error retrieving user data: ${error.message}")
            }

//            val currentUserCuisines = currentUser.profile?.cuisine ?: emptyList()
//            val currentUserDishes = currentUser.profile?.dish ?: emptyList()
//
//            // Extract cuisines and dishes from the other user
//            val userCuisines = user.profile?.cuisine ?: emptyList()
//            val userDishes = user.profile?.dish ?: emptyList()
//
//            // Find common cuisines and dishes
//            val commonCuisines = currentUserCuisines.intersect(userCuisines).toMutableList()
//            val commonDishes = currentUserDishes.intersect(userDishes).toMutableList()
//
//            user.matches?.add(Match(currentUser.userId, "0", commonCuisines, commonDishes))
//            currentUser.matches?.add(Match(user.userId, "0", commonCuisines, commonDishes))
//            db.child(userId).setValue(currentUser)
//            db.child(user.userId.toString()).setValue(user)
//
//            onLikeAction()

        }
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}
