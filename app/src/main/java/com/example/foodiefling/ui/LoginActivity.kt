package com.example.foodiefling.ui

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.foodiefling.R

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
            // Perform login logic here
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