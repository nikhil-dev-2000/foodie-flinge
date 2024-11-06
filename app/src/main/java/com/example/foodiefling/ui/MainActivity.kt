package com.example.foodiefling.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.foodiefling.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginBtn = findViewById<Button>(R.id.login_btn)
        val forgotPass = findViewById<TextView>(R.id.forgot_pass)
        val signup = findViewById<TextView>(R.id.signup)

        loginBtn.setOnClickListener {
            val username = findViewById<EditText>(R.id.username_login).text.toString()
            val password = findViewById<EditText>(R.id.pass_login).text.toString()
            // Perform login logic here
        }
        forgotPass.setOnClickListener {
            // Handle forgot password click
        }
        signup.setOnClickListener {
            // Handle signup click
        }






    }
}