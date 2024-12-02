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

class ProfileSetup4 : AppCompatActivity() {

    private var educationPref: String? = null
    private var smokingPref: String? = null

    private lateinit var hsd: LinearLayout
    private lateinit var ug: LinearLayout
    private lateinit var grad: LinearLayout
    private lateinit var gradSchool: LinearLayout

    private lateinit var yes: LinearLayout
    private lateinit var occasionally: LinearLayout
    private lateinit var no: LinearLayout

    private lateinit var next: Button

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_setup4)

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

        hsd = findViewById(R.id.hsdL)
        ug = findViewById(R.id.ugL)
        grad = findViewById(R.id.gradL)
        gradSchool = findViewById(R.id.gradSchoolL)

        yes = findViewById(R.id.yesL)
        occasionally = findViewById(R.id.occasionallyL)
        no = findViewById(R.id.noL)
        next = findViewById(R.id.next)

        // Education preference click listener
        val educationClickListener = View.OnClickListener { view ->
            resetEducationPrefColor()
            view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C3D03A8C"))
            when (view.id) {
                R.id.hsdL -> setEducationPref("High School")
                R.id.ugL -> setEducationPref("Undergraduate")
                R.id.gradL -> setEducationPref("Graduate")
                R.id.gradSchoolL -> setEducationPref("Grad School")
            }
        }

        // Smoking preference click listener
        val smokingClickListener = View.OnClickListener { view ->
            resetSmokingPrefColor()
            view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C3D03A8C"))
            when (view.id) {
                R.id.yesL -> setSmokingPref("Yes")
                R.id.occasionallyL -> setSmokingPref("Occasionally")
                R.id.noL -> setSmokingPref("No")
            }
        }

        // Set click listeners for education preferences
        hsd.setOnClickListener(educationClickListener)
        ug.setOnClickListener(educationClickListener)
        grad.setOnClickListener(educationClickListener)
        gradSchool.setOnClickListener(educationClickListener)

        // Set click listeners for smoking preferences
        yes.setOnClickListener(smokingClickListener)
        occasionally.setOnClickListener(smokingClickListener)
        no.setOnClickListener(smokingClickListener)

        next.setOnClickListener {
            user.profile?.education = educationPref
            user.profile?.smoke = smokingPref
            db.child(userId!!).setValue(user)
            val intent1 = Intent(this, ProfileSetup5::class.java)
            intent1.putExtra("user_id", userId)
            startActivity(intent1)
        }
    }

    private fun setEducationPref(pref: String) {
        educationPref = pref
    }

    private fun setSmokingPref(pref: String) {
        smokingPref = pref
    }

    private fun resetEducationPrefColor() {
        hsd.backgroundTintList = null
        ug.backgroundTintList = null
        grad.backgroundTintList = null
        gradSchool.backgroundTintList = null
    }

    private fun resetSmokingPrefColor() {
        yes.backgroundTintList = null
        occasionally.backgroundTintList = null
        no.backgroundTintList = null
    }
}
