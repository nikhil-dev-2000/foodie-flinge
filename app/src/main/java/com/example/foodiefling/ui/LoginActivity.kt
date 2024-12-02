package com.example.foodiefling.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodiefling.R
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val emailField = findViewById<EditText>(R.id.username_login)
        val passwordFiled = findViewById<EditText>(R.id.pass_login)
        val loginBtn = findViewById<Button>(R.id.login_btn)
        val forgotPass = findViewById<TextView>(R.id.forgot_pass)
        val signup = findViewById<TextView>(R.id.signup)

        loginBtn.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordFiled.text.toString()
            val encryptedPassword = HandelPassword.encrypt(password)

            val db = FirebaseDatabase.getInstance().getReference("Users")

            db.get().addOnSuccessListener { dataSnapshot ->
                var isMatchFound = false
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null && user.email == email && user.password == encryptedPassword) {
                        // Email and password match
                        isMatchFound = true

                        // Create intent to PotentialMatches
                        val intent = Intent(this, PotentialMatches::class.java)
                        intent.putExtra("user_id", "User_${user.userId}")
                        startActivity(intent)
                        finish() // Finish the current activity
                        break
                    }
                }

                if (!isMatchFound) {
                    // No matching email and password found
                    Toast.makeText(this, "Email and password don't match", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { error ->
                // Handle database fetch error
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }

        forgotPass.setOnClickListener {
            // Handle forgot password click
            showVerifyEmailDialog(emailField.text.toString())
        }

        signup.setOnClickListener {
            // Handle signup click
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }



    }

    private fun showVerifyEmailDialog(email: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.verify_email)
        dialog.show()
        dialog.setCanceledOnTouchOutside(true);
    }
}