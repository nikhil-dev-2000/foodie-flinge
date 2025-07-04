package com.example.foodiefling.ui

// MainActivity.kt
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginBottom
import com.example.foodiefling.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CuisineSelection : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cusine_selection)

        lateinit var user: User

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

        val container = findViewById<LinearLayout>(R.id.cuisines)


        val cuisines = listOf("American", "Brazilian", "Caribbean", "Chinese", "Ethiopian", "French", "German",
            "Greek", "Indian", "Italian", "Japanese", "Korean", "Lebanese", "Mexican", "Moroccan",
            "Peruvian", "Spanish", "Thai", "Turkish", "Vietnamese")

        val selectedCuisines: MutableList<String> = mutableListOf()

        for (cuisine in cuisines) {
            // Create a new LinearLayout
            val linearLayout = LinearLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    dpToPx(150),
                    dpToPx(50)
                ).apply {
                    setMargins(0, 0, 0, 10)
                }
                background = resources.getDrawable(R.drawable.background, null)
                gravity = android.view.Gravity.CENTER // Center the content
                orientation = LinearLayout.VERTICAL

                setOnClickListener {
                    if (selectedCuisines.contains(cuisine)) {
                        backgroundTintList = null
                        selectedCuisines.remove(cuisine)
                        Log.d("selectedCuisines", selectedCuisines.toString())
                        updateSelectedCuisines(selectedCuisines)
                    } else if(selectedCuisines.size < 3) {
                        selectedCuisines.add(cuisine)
                        println(selectedCuisines)
                        Log.d("selectedCuisines", selectedCuisines.toString())
                        updateSelectedCuisines(selectedCuisines)
                        backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C3D03A8C"))
                    }
                }
            }

            // Create a new TextView
            val textView = TextView(this).apply {
                text = cuisine
                textSize = 20.0F
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            linearLayout.addView(textView)
            container.addView(linearLayout)

            val next = findViewById<Button>(R.id.next)
            next.setOnClickListener {
                user.profile?.cuisine = selectedCuisines
                db.child(userId!!).setValue(user)
                val intent1 = Intent(this, DishSelection::class.java)
                intent1.putExtra("user_id", userId)
                intent1.putExtra("selected_cuisines", selectedCuisines.toTypedArray())
                startActivity(intent1)
            }
        }

    }
    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()
    }

    private fun updateSelectedCuisines(cuisines: List<String>) {
        val cuisineLayout1 = findViewById<LinearLayout>(R.id.cuisine_layout1)
        val cuisineLayout2 = findViewById<LinearLayout>(R.id.cuisine_layout2)
        val cuisineLayout3 = findViewById<LinearLayout>(R.id.cuisine_layout3)

        val cuisine1 = findViewById<TextView>(R.id.cuisine1)
        val cuisine2 = findViewById<TextView>(R.id.cuisine2)
        val cuisine3 = findViewById<TextView>(R.id.cuisine3)
        val variableMap = mapOf("cuisine1" to cuisine1, "cuisine2" to cuisine2, "cuisine3" to cuisine3)
        val layoutVariableMap: Map<String, LinearLayout> = mapOf("cuisine_layout1" to cuisineLayout1, "cuisine_layout2" to cuisineLayout2, "cuisine_layout3" to cuisineLayout3)

        for(i in 1..3){
            if (i <= cuisines.size) {
                layoutVariableMap["cuisine_layout$i"]?.visibility = View.VISIBLE
                variableMap["cuisine$i"]?.text = cuisines[i - 1]
            } else {
                layoutVariableMap["cuisine_layout$i"]?.visibility = View.GONE
            }
        }



    }


}