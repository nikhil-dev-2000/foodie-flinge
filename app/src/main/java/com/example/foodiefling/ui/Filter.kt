package com.example.foodiefling.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodiefling.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.InputStream

class Filter: AppCompatActivity() {

    private val allCitiesWithStates = mutableListOf<Pair<String, String>>() // List of (City, State)
    private lateinit var cityInput: TextInputEditText
    private lateinit var cityDropdown: RecyclerView
    private lateinit var cityAdapter: CityAdapter
    private lateinit var religionInput: TextInputEditText
    private lateinit var ethnicityInput: TextInputEditText
    private lateinit var religionDropdown: RecyclerView
    private lateinit var ethnicityDropdown: RecyclerView
    private lateinit var religionAdapter: CityAdapter
    private lateinit var ethnicityAdapter: CityAdapter
    private lateinit var yes: LinearLayout
    private lateinit var occasionally: LinearLayout
    private lateinit var no: LinearLayout
    private lateinit var occasionallyL1: LinearLayout
    private lateinit var noL1: LinearLayout
    private lateinit var yesL1: LinearLayout
    private lateinit var user: User

    private var smokingPref: String? = null
    private var drinkPref: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.filter)

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

        var selectedGender = ""
        val spinnerGender: Spinner = findViewById(R.id.gender)
        val genderAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.gender_options,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = genderAdapter

        spinnerGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedGender = if (position == 0) {
                    ""
                } else {
                    parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        yes = findViewById(R.id.yesL)
        occasionally = findViewById(R.id.occasionallyL)
        no = findViewById(R.id.noL)

        occasionallyL1 = findViewById(R.id.occasionallyL1)
        noL1 = findViewById(R.id.noL1)
        yesL1 = findViewById(R.id.yesL1)

        val smokingClickListener = View.OnClickListener { view ->
            resetSmokingPrefColor()
            view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C3D03A8C"))
            when (view.id) {
                R.id.yesL -> setSmokingPref("Yes")
                R.id.occasionallyL -> setSmokingPref("Occasionally")
                R.id.noL -> setSmokingPref("No")
            }
        }

        val drinkClickListener = View.OnClickListener { view ->
            resetDrinkPrefColors()
            view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C3D03A8C"))
            when (view.id) {
                R.id.occasionallyL1 -> setDrinkPref("Occasionally")
                R.id.noL1 -> setDrinkPref("No")
                R.id.yesL1 -> setDrinkPref("Yes")
            }
        }

        yes.setOnClickListener(smokingClickListener)
        occasionally.setOnClickListener(smokingClickListener)
        no.setOnClickListener(smokingClickListener)

        yesL1.setOnClickListener(drinkClickListener)
        occasionallyL1.setOnClickListener(drinkClickListener)
        noL1.setOnClickListener(drinkClickListener)

        val minimum = findViewById<EditText>(R.id.min_age)
        val maximum = findViewById<EditText>(R.id.max_age)

        // Load cities from CSV inside onCreate
        val inputStream: InputStream = assets.open("uscities.csv")
        allCitiesWithStates.addAll(readCitiesFromCSV(inputStream))

        val ethnicity = listOf(
            "African", "African American", "Caribbean", "Central Asian", "East Asian",
            "European", "Hispanic", "Jewish", "Latino", "Mediterranean",
            "Middle Eastern", "Mixed/Multiracial", "Native American",
            "Native American/Indigenous", "Pacific Islander", "South Asian",
            "Southeast Asian", "White"
        )

        val religion = listOf(
            "Agnostic", "Atheist", "Buddhist", "Catholic", "Christian",
            "Hindu", "Jain", "Jewish", "Mormon", "Latter-day Saint",
            "Muslim", "Zoroastrianism", "Other"
        )


        // Initialize views
        cityInput = findViewById(R.id.cityInput)
        cityDropdown = findViewById(R.id.cityDropdown)
        religionInput = findViewById(R.id.religionInput)
        ethnicityInput = findViewById(R.id.ethnicityInput)
        religionDropdown = findViewById(R.id.religionDropdown)
        ethnicityDropdown = findViewById(R.id.ethnicityDropdown)


        // Initialize RecyclerView Adapter
        cityAdapter = CityAdapter(emptyList()) { selectedCityWithState ->
            cityInput.setText(selectedCityWithState) // Update the text input with "City, State"
            cityDropdown.visibility = View.GONE // Hide dropdown
        }

        religionAdapter = CityAdapter(emptyList()) { selectedReligion ->
            religionInput.setText(selectedReligion)
            religionDropdown.visibility = View.GONE
        }

        ethnicityAdapter = CityAdapter(emptyList()) { selectedEthnicity ->
            ethnicityInput.setText(selectedEthnicity)
            ethnicityDropdown.visibility = View.GONE
        }

        // Configure RecyclerView
        cityDropdown.apply {
            layoutManager = LinearLayoutManager(this@Filter)
            adapter = cityAdapter
        }

        religionDropdown.apply {
            layoutManager = LinearLayoutManager(this@Filter)
            adapter = religionAdapter
        }

        ethnicityDropdown.apply {
            layoutManager = LinearLayoutManager(this@Filter)
            adapter = ethnicityAdapter
        }

        // Add TextWatcher to filter city-state combinations
        cityInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase().trim()
                if (query.isNotEmpty()) {
                    val filteredCities = allCitiesWithStates.filter {
                        it.first.lowercase().contains(query) || it.second.lowercase()
                            .contains(query)
                    }.map { "${it.first}, ${it.second}" } // Format as "City, State" without quotes
                    if (filteredCities.isNotEmpty()) {
                        cityDropdown.visibility = View.VISIBLE
                        cityAdapter.updateCities(filteredCities)
                    } else {
                        cityDropdown.visibility = View.GONE
                    }
                } else {
                    cityDropdown.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        religionInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase().trim()
                if (query.isNotEmpty()) {
                    val filteredReligion = religion.filter {
                        it.lowercase().contains(query)
                    }.map { it } // Format as "City, State" without quotes
                    if (filteredReligion.isNotEmpty()) {
                        religionDropdown.visibility = View.VISIBLE
                        religionAdapter.updateCities(filteredReligion)
                    } else {
                        religionDropdown.visibility = View.GONE
                    }
                } else {
                    religionDropdown.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        ethnicityInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase().trim()
                if (query.isNotEmpty()) {
                    val filteredEthnicity = ethnicity.filter {
                        it.lowercase().contains(query)
                    }.map { it }
                    if (filteredEthnicity.isNotEmpty()) {
                        ethnicityDropdown.visibility = View.VISIBLE
                        ethnicityAdapter.updateCities(filteredEthnicity)
                    } else {
                        ethnicityDropdown.visibility = View.GONE
                    }
                } else {
                    ethnicityDropdown.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        val apply = findViewById<Button>(R.id.apply)
        apply.setOnClickListener {
            val minAge = minimum.text.toString().toIntOrNull()
            val maxAge = maximum.text.toString().toIntOrNull()
            val selectedCity = cityInput.text.toString().takeIf { it.isNotBlank() }
            val selectedReligion = religionInput.text.toString().takeIf { it.isNotBlank() }
            val selectedEthnicity = ethnicityInput.text.toString().takeIf { it.isNotBlank() }
            val smokingPreference = smokingPref
            val drinkingPreference = drinkPref
            val genderPreference = selectedGender.takeIf { it.isNotBlank() }

            user.preferences?.apply {
                if (minAge != null) {
                    this.minAge = minAge
                }
                if (maxAge != null) {
                    this.maxAge = maxAge
                }
                if (selectedCity != null) {
                    this.location = selectedCity
                }
                if (selectedReligion != null) {
                    this.religion = selectedReligion
                }
                if (selectedEthnicity != null) {
                    this.raceOrEthnicity = selectedEthnicity
                }
                if (genderPreference != null) {
                    this.gender = genderPreference
                }
                if (smokingPreference != null) {
                    this.smoke = smokingPreference
                }
                if (drinkingPreference != null) {
                    this.drink = drinkingPreference
                }
            }
            if (userId != null) {
                db.child(userId).setValue(user).addOnSuccessListener {
                    Log.d("Filter", "Preferences updated successfully.")
                }.addOnFailureListener { error ->
                    Log.e("Filter", "Error updating preferences: ${error.message}")
                }
            }

            val intent1 = Intent(this, PotentialMatches::class.java)
            intent1.putExtra("user_id", userId)
            startActivity(intent1)

        }

    }
    private fun readCitiesFromCSV(inputStream: InputStream): List<Pair<String, String>> {
        val citiesWithStates = mutableListOf<Pair<String, String>>()
        inputStream.bufferedReader().use { reader ->
            reader.lineSequence().drop(1) // Skip the header row if present
                .forEach { line ->
                    val parts = line.split(",")
                    if (parts.size >= 2) {
                        val city = parts[0].trim().replace("\"", "") // Remove quotes from city
                        val state = parts[2].trim().replace("\"", "") // Remove quotes from state
                        citiesWithStates.add(Pair(city, state))
                    }
                }
        }
        return citiesWithStates
    }

    private fun setSmokingPref(pref: String) {
        smokingPref = pref
    }

    private fun setDrinkPref(pref: String) {
        drinkPref = pref
    }

    private fun resetSmokingPrefColor() {
        yes.backgroundTintList = null
        occasionally.backgroundTintList = null
        no.backgroundTintList = null
    }

    private fun resetDrinkPrefColors() {
        occasionallyL1.backgroundTintList = null
        noL1.backgroundTintList = null
        yesL1.backgroundTintList = null
    }
}