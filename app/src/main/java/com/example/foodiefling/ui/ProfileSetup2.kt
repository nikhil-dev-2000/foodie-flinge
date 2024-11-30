package com.example.foodiefling.ui

import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.foodiefling.R
import org.apache.poi.ss.usermodel.WorkbookFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import java.io.InputStream

class ProfileSetup2 : AppCompatActivity() {

    private lateinit var citySpinner: Spinner
    private lateinit var cityList: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_setup2)
        citySpinner = findViewById(R.id.citySpinner)
        val searchView = findViewById<SearchView>(R.id.citySearchView)

        // Load the CSV file from assets
        val inputStream = assets.open("uscities.csv")
        cityList = readCitiesFromCSV(inputStream).sorted() // Sort cities in ascending order
        setupSpinner(cityList)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredCities = cityList.filter {
                    it.contains(newText ?: "", ignoreCase = true)
                }
                setupSpinner(filteredCities)
                return true
            }
        })
    }

    private fun setupSpinner(cities: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, cities)
        citySpinner.adapter = adapter
    }

    private fun readCitiesFromCSV(inputStream: InputStream): List<String> {
        val cities = mutableListOf<String>()
        inputStream.bufferedReader().use { reader ->
            reader.lineSequence().drop(1) // Skip the header if there is one
                .forEach { line ->
                    val city = line.split(",")[0] // Assuming the city names are in the first column
                    cities.add(city)
                }
        }
        return cities
    }
}
