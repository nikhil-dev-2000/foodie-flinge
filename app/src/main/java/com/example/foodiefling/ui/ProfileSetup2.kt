package com.example.foodiefling.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodiefling.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.InputStream

class ProfileSetup2 : AppCompatActivity() {

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
    private lateinit var ft: EditText
    private lateinit var inch: EditText
    private lateinit var user: User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_setup2)

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

        // Load cities from CSV inside onCreate
        val inputStream: InputStream = assets.open("uscities.csv")
        allCitiesWithStates.addAll(readCitiesFromCSV(inputStream))

        val ethnicity = listOf("African", "African American", "Caribbean", "Central Asian", "East Asian",
            "European", "Hispanic", "Jewish", "Latino", "Mediterranean",
            "Middle Eastern", "Mixed/Multiracial", "Native American",
            "Native American/Indigenous", "Pacific Islander", "South Asian",
            "Southeast Asian", "White")

        val religion = listOf("Agnostic", "Atheist", "Buddhist", "Catholic", "Christian",
            "Hindu", "Jain", "Jewish", "Mormon", "Latter-day Saint",
            "Muslim", "Zoroastrianism", "Other")


        // Initialize views
        cityInput = findViewById(R.id.cityInput)
        cityDropdown = findViewById(R.id.cityDropdown)
        religionInput = findViewById(R.id.religionInput)
        ethnicityInput = findViewById(R.id.ethnicityInput)
        religionDropdown = findViewById(R.id.religionDropdown)
        ethnicityDropdown = findViewById(R.id.ethnicityDropdown)
        ft = findViewById(R.id.ft)
        inch = findViewById(R.id.inch)

        val next = findViewById<Button>(R.id.next)
        val allergies = findViewById<EditText>(R.id.allergies)


        // Initialize RecyclerView Adapter
        cityAdapter = CityAdapter(emptyList()) { selectedCityWithState ->
            cityInput.setText(selectedCityWithState) // Update the text input with "City, State"
            cityDropdown.visibility = View.GONE // Hide dropdown
        }

        religionAdapter = CityAdapter(emptyList()){selectedReligion ->
            religionInput.setText(selectedReligion)
            religionDropdown.visibility = View.GONE
        }

        ethnicityAdapter = CityAdapter(emptyList()){selectedEthnicity ->
            ethnicityInput.setText(selectedEthnicity)
            ethnicityDropdown.visibility = View.GONE
        }

        // Configure RecyclerView
        cityDropdown.apply {
            layoutManager = LinearLayoutManager(this@ProfileSetup2)
            adapter = cityAdapter
        }

        religionDropdown.apply {
            layoutManager = LinearLayoutManager(this@ProfileSetup2)
            adapter = religionAdapter
        }

        ethnicityDropdown.apply {
            layoutManager = LinearLayoutManager(this@ProfileSetup2)
            adapter = ethnicityAdapter
        }

        // Add TextWatcher to filter city-state combinations
        cityInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase().trim()
                if (query.isNotEmpty()) {
                    val filteredCities = allCitiesWithStates.filter {
                        it.first.lowercase().contains(query) || it.second.lowercase().contains(query)
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


        next.setOnClickListener {
            user.profile?.city = cityInput.text.toString()
            user.profile?.religion = religionInput.text.toString()
            user.profile?.raceOrEthnicity = ethnicityInput.text.toString()
            user.profile?.allergies = allergies.text.toString()
            db.child(userId!!).setValue(user)
            val intent1 = android.content.Intent(this, ProfileSetup3::class.java)
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


}
