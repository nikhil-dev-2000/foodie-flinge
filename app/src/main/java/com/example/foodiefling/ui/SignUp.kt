package com.example.foodiefling.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.foodiefling.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar

class SignUp : AppCompatActivity() {

    private var db: DatabaseReference? = FirebaseDatabase.getInstance().getReference("Users")

    val keys = mutableListOf<String>()
    private lateinit var dob: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_up)

        val fname = findViewById<EditText>(R.id.fname)
        val lname = findViewById<EditText>(R.id.lname)
        val email = findViewById<EditText>(R.id.email)
        dob = findViewById(R.id.editTextDate)
        val password = findViewById<EditText>(R.id.editTextTextPassword)
        val password2 = findViewById<EditText>(R.id.editTextTextPassword2)
        var userGender = ""

        val spinnerGender: Spinner = findViewById(R.id.gender)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.gender_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = adapter

        spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                userGender = if (position == 0) {
                    ""
                } else {
                    parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        dob.setOnClickListener {
            showDatePickerDialog()
        }

        db?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val key = childSnapshot.key
                    if (key != null) {
                        keys.add(key)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("MainActivity", "Database error: ${databaseError.message}")
            }
        })

        val verifyEmailButton = findViewById<Button>(R.id.verify_email)
        verifyEmailButton.setOnClickListener {


            val otp = sendOTP(email.text.toString())
            val userFullName = fname.text.toString()
            val userLastName = lname.text.toString()
            val userEmail = email.text.toString()
            val userPassword = password.text.toString()
            val userPassword2 = password2.text.toString()

            when {
                userFullName.isEmpty() -> {
                    fname.error = "First name is required"
                    fname.requestFocus()
                    return@setOnClickListener
                }
                userLastName.isEmpty() -> {
                    lname.error = "Last name is required"
                    lname.requestFocus()
                    return@setOnClickListener
                }
                userEmail.isEmpty() -> {
                    email.error = "Email is required"
                    email.requestFocus()
                    return@setOnClickListener
                }
                dob.text.toString().isEmpty() -> {
                    dob.error = "Date of birth is required"
                    dob.requestFocus()
                    return@setOnClickListener
                }
                userPassword.isEmpty() -> {
                    password.error = "Password is required"
                    password.requestFocus()
                    return@setOnClickListener
                }
                userPassword2.isEmpty() -> {
                    password2.error = "Password is required"
                    password2.requestFocus()
                    return@setOnClickListener
                }

            }

            if (password.text.toString() != password2.text.toString()) {
                password2.error = "Passwords do not match"
                return@setOnClickListener
            }

            checkIfEmailExists(email.text.toString(), object : EmailCheckCallback {
                override fun onResult(emailExists: Boolean) {
                    if (emailExists) {
                        email.error = "Email already exists"
                    } else {
                        val userAge = calculateAge(dob.text.toString())
                        val user = User(
                            userId = 4001,
                            age = userAge,
                            email = userEmail,
                            firstName = userFullName,
                            lastName = userLastName,
                            password = HandelPassword.encrypt(userPassword),
                            gender = userGender,
                            preferences = null,
                            profile = null,
                            matches = null
                        )
                        showVerifyEmailDialog(email.text.toString(), keys, otp, user)
                    }
                }
            })


        }

    }
    private fun sendOTP(email: String): Int {
        val emailSender = EmailSender("nikhilnandish2312@gmail.com", "govy itnm acve lqpw")
        val otp = (1000..9999).random()
        emailSender.sendEmail(email, "Your OTP Code", "Your OTP is: $otp")
        Log.d("OTP", "Generated OTP: ")
        return otp
    }


    private fun showVerifyEmailDialog(email: String, keys: List<String>, otp: Int, user: User) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.verify_email)
        dialog.show()
        dialog.window?.setLayout(1000,630)
        dialog.setCanceledOnTouchOutside(true);
        val verifyButton = dialog.findViewById<Button>(R.id.verifyButton)
        verifyButton.setOnClickListener {
            val enteredOTP = dialog.findViewById<EditText>(R.id.enteredOTP)
            if(enteredOTP.text.toString() == otp.toString()){
                val largestKey = keys.maxOrNull()
                val regex = Regex("\\d+")
                val largestNum  = largestKey?.let { it1 -> regex.find(it1) }
                val newUserId = largestNum?.value?.toInt()?.plus(1)
                if (newUserId != null) {
                    user.userId = newUserId
                }
                createAccount("User_$newUserId", user)
            }
        }
    }

    private fun createAccount(largestKey: String, user: User) {
        db?.child(largestKey)?.setValue(user)
            ?.addOnSuccessListener {
                val intent = Intent(this, ProfileSetup1::class.java)
                intent.putExtra("user_id", largestKey)
                startActivity(intent)
            }
            ?.addOnFailureListener { exception ->
                // Handle the error
                println("Failed to add user: ${exception.message}")
            }

    }

    private fun showDatePickerDialog() {
        // Get the current date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create a DatePickerDialog
        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Format the date as MM/dd/yyyy
            val date = String.format("%02d/%02d/%d", selectedMonth + 1, selectedDay, selectedYear)
            dob.setText(date)

            // Log the selected date
            Log.d("MainActivity", "Selected date: $date")
        }, year, month, day)

        // Show the dialog
        datePickerDialog.show()
    }

    private fun calculateAge(dateString: String): Int {
        val birthDate = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val currentDate = LocalDate.now()
        return Period.between(birthDate, currentDate).years
    }

    private fun checkIfEmailExists(email: String, callback: EmailCheckCallback){
        db?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot){
                var emailExists = false
                for (childSnapshot in dataSnapshot.children) {
                    val userEmail = childSnapshot.child("email").getValue(String::class.java)
                    if (userEmail == email) {
                        emailExists = true
                        break
                    }
                }
                callback.onResult(emailExists)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
                Log.e("SignUp", "Database error: ${databaseError.message}")
            }
        })
    }

}