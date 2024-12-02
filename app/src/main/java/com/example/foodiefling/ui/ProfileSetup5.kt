package com.example.foodiefling.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.foodiefling.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileSetup5 : AppCompatActivity() {

    private var drinkPref: String? = null
    private var familyPref: String? = null
    private var haveKids: String? = null

    private lateinit var occasionallyL: LinearLayout
    private lateinit var noL: LinearLayout
    private lateinit var yesL: LinearLayout
    private lateinit var haveKidsL: LinearLayout
    private lateinit var dontHaveKidsL: LinearLayout
    private lateinit var noKidsL: LinearLayout
    private lateinit var wantKidsL: LinearLayout
    private lateinit var yesKidsL: LinearLayout
    private lateinit var notSureL: LinearLayout
    private lateinit var next: Button

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_setup5)

        val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

        val intent = intent
        val userId = intent.getStringExtra("user_id")

        if (userId != null) {
            db.child(userId).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    // Extract user data
                    user = dataSnapshot.getValue(User::class.java)!!
                    // Check if user is not null
                    user.let {
                        Log.d("User Retrieval", "User Data: $it")
                        Log.d("User Retrieval", "User Data: $user")
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

        // Initialize UI elements
        occasionallyL = findViewById(R.id.occasionallyL)
        noL = findViewById(R.id.noL)
        yesL = findViewById(R.id.yesL)
        haveKidsL = findViewById(R.id.haveKidsL)
        dontHaveKidsL = findViewById(R.id.dontHaveKidsL)
        noKidsL = findViewById(R.id.noKidsL)
        wantKidsL = findViewById(R.id.wantKidsL)
        yesKidsL = findViewById(R.id.yesKidsL)
        notSureL = findViewById(R.id.notSureL)
        next = findViewById(R.id.next)

        // Set up click listeners for drinking preferences
        val drinkClickListener = View.OnClickListener { view ->
            resetDrinkPrefColors()
            view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C3D03A8C"))
            when (view.id) {
                R.id.occasionallyL -> setDrinkPref("Occasionally")
                R.id.noL -> setDrinkPref("No")
                R.id.yesL -> setDrinkPref("Yes")
            }
        }
        occasionallyL.setOnClickListener(drinkClickListener)
        noL.setOnClickListener(drinkClickListener)
        yesL.setOnClickListener(drinkClickListener)

        // Set up click listeners for family preferences
        val familyClickListener = View.OnClickListener { view ->
            resetFamilyPrefColors()
            view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C3D03A8C"))
            when (view.id) {
                R.id.noKidsL -> setFamilyPref("No Kids")
                R.id.wantKidsL -> setFamilyPref("Want Kids")
                R.id.yesKidsL -> setFamilyPref("Open to Kids")
                R.id.notSureL -> setFamilyPref("Not Sure")
            }
        }
        noKidsL.setOnClickListener(familyClickListener)
        wantKidsL.setOnClickListener(familyClickListener)
        yesKidsL.setOnClickListener(familyClickListener)
        notSureL.setOnClickListener(familyClickListener)

        // Set up click listeners for drinking habits
        val haveKidsClickListener = View.OnClickListener { view ->
            resetHavingKidsColors()
            view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C3D03A8C"))
            when (view.id) {
                R.id.haveKidsL -> setHaveKids("Yes")
                R.id.dontHaveKidsL -> setHaveKids("No")
            }
        }
        haveKidsL.setOnClickListener(haveKidsClickListener)
        dontHaveKidsL.setOnClickListener(haveKidsClickListener)

        // Finish button click listener
        next.setOnClickListener {
            user.profile?.drink = drinkPref
            user.profile?.familyPlans = familyPref
            user.profile?.kids = haveKids
            db.child(userId!!).setValue(user)
            val intent1 = Intent(this, CuisineSelection::class.java)
            intent1.putExtra("user_id", userId)
            startActivity(intent1)
        }
    }

    private fun setDrinkPref(pref: String) {
        drinkPref = pref
    }

    private fun setFamilyPref(pref: String) {
        familyPref = pref
    }

    private fun setHaveKids(pref: String) {
        haveKids = pref
    }

    private fun resetDrinkPrefColors() {
        occasionallyL.backgroundTintList = null
        noL.backgroundTintList = null
        yesL.backgroundTintList = null
    }

    private fun resetFamilyPrefColors() {
        noKidsL.backgroundTintList = null
        wantKidsL.backgroundTintList = null
        yesKidsL.backgroundTintList = null
        notSureL.backgroundTintList = null
    }

    private fun resetHavingKidsColors() {
        haveKidsL.backgroundTintList = null
        dontHaveKidsL.backgroundTintList = null
    }
}
