package com.example.foodiefling.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.foodiefling.R

class SignUp : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)

        val email = findViewById<EditText>(R.id.email)

        val verifyEmailButton = findViewById<Button>(R.id.verify_email)
        verifyEmailButton.setOnClickListener {
            val emailSender = EmailSender("nikhilnandish2312@gmail.com", "govy itnm acve lqpw")
            val otp = (1000..9999).random()
            val recipientEmail = "nikhil.nandish2000@gmail.com"
            emailSender.sendEmail(recipientEmail, "Your OTP Code", "Your OTP is: $otp")

            showVerifyEmailDialog(email.text.toString())
        }


    }
    private fun showVerifyEmailDialog(email: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.verify_email)
        dialog.show()
        dialog.setCanceledOnTouchOutside(true);
    }

}