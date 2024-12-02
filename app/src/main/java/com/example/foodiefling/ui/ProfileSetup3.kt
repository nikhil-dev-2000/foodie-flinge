package com.example.foodiefling.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.foodiefling.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileSetup3 : AppCompatActivity() {

    var genderPref: String? = null
    var relationshipPref: String? = null

    private lateinit var men: LinearLayout
    private lateinit var women: LinearLayout
    private lateinit var anyone: LinearLayout
    private lateinit var nonBinary: LinearLayout

    private lateinit var longTerm: LinearLayout
    private lateinit var ENM: LinearLayout
    private lateinit var marriage: LinearLayout
    private lateinit var casual: LinearLayout

    private lateinit var next: View

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_setup3)

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

        // Initializing UI elements
        men = findViewById(R.id.menL)
        women = findViewById(R.id.womenL)
        anyone = findViewById(R.id.anyoneL)
        nonBinary = findViewById(R.id.nonBinaryL)

        longTerm = findViewById(R.id.longTermL)
        ENM = findViewById(R.id.ENML)
        marriage = findViewById(R.id.marriageL)
        casual = findViewById(R.id.casualL)

        next = findViewById(R.id.next)

        val clickListener = View.OnClickListener { view ->
            setGenderPrefColor()
            view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C3D03A8C"))
            when (view.id) {
                R.id.menL -> genderPref("men")
                R.id.womenL -> genderPref("women")
                R.id.anyoneL -> genderPref("anyone")
                R.id.nonBinaryL -> genderPref("non-binary")
            }
        }
        men.setOnClickListener(clickListener)
        women.setOnClickListener(clickListener)
        anyone.setOnClickListener(clickListener)
        nonBinary.setOnClickListener(clickListener)

        val clickListener2 = View.OnClickListener { view ->
            setRelationshipPrefColor()
            view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C3D03A8C"))
            when (view.id) {
                R.id.longTermL -> relationshipPref("long-term")
                R.id.ENML -> relationshipPref("non-monogamy")
                R.id.marriageL -> relationshipPref("marriage")
                R.id.casualL -> relationshipPref("casual")
            }
        }
        longTerm.setOnClickListener(clickListener2)
        ENM.setOnClickListener(clickListener2)
        marriage.setOnClickListener(clickListener2)
        casual.setOnClickListener(clickListener2)

        next.setOnClickListener {
            user.profile?.genderPref = genderPref
            user.profile?.whatLookingFor = relationshipPref
            db.child(userId!!).setValue(user)
            val intent1 = Intent(this, ProfileSetup4::class.java)
            intent1.putExtra("user_id", userId)
            startActivity(intent1)
        }
    }

    private fun genderPref(gender: String) {
        genderPref = gender
    }

    private fun relationshipPref(relationship: String) {
        relationshipPref = relationship
    }

    private fun setGenderPrefColor() {
        men.backgroundTintList = null
        women.backgroundTintList = null
        anyone.backgroundTintList = null
        nonBinary.backgroundTintList = null
    }

    private fun setRelationshipPrefColor() {
        longTerm.backgroundTintList = null
        ENM.backgroundTintList = null
        marriage.backgroundTintList = null
        casual.backgroundTintList = null
    }


}