// File: RestaurantListing.kt
package com.example.foodiefling.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodiefling.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Callback

import org.json.JSONObject
import java.io.IOException

class RestaurantListing : AppCompatActivity() {

    private lateinit var user: User
    private lateinit var cuisineList: MutableList<String>
    private lateinit var restaurantAdapter: RestaurantAdapter
    private val restaurants: MutableList<Restaurant> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.restaurant_listings)

        val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

        val intent = intent
        val userId = intent.getStringExtra("current_user")
        val otherUserId = intent.getStringExtra("other_user")

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        restaurantAdapter = RestaurantAdapter(restaurants) { restaurant ->
            openGoogleMaps(restaurant)
        }
        recyclerView.adapter = restaurantAdapter

        if (userId != null && otherUserId != null) {
            db.child(userId).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    user = dataSnapshot.getValue(User::class.java)!!
                    user.let {
                        Log.d("User Retrieval", "User Data: $it")
                        cuisineList = retrieveCuisineList(otherUserId)
                        fetchRestaurants()
                    }
                } else {
                    Log.e("User Retrieval", "User not found")
                }
            }.addOnFailureListener { error ->
                Log.e("User Retrieval", "Error retrieving user data: ${error.message}")
            }
        }
    }

    private fun retrieveCuisineList(otherUserId: String?): MutableList<String> {
        val match = user.matches?.find { it.matchedUser == otherUserId?.filter { it.isDigit() }
            ?.toInt() }
        return match?.cuisines.orEmpty().toMutableList()
    }

    private fun fetchRestaurants() {
        val apiKey = "AIzaSyDQjhJFyva5_qjpmDsFWXZABUtMQH1lxCM"
        val cuisines = cuisineList.joinToString(" OR ") { it }
        val url =
            "https://maps.googleapis.com/maps/api/place/textsearch/json?query=restaurants+in+Arlington+Texas+serving+$cuisines&key=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API Error", "Request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { res ->
                    if (!res.isSuccessful) {
                        Log.e("API Error", "Request failed with code: ${res.code}")
                        return
                    }

                    val responseBody = res.body?.string()
                    if (responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        val results = jsonResponse.getJSONArray("results")

                        // Limit to 10 restaurants or available results
                        val maxResults = minOf(10, results.length())
                        for (i in 0 until maxResults) {
                            val restaurant = results.getJSONObject(i)
                            val name = restaurant.getString("name")
                            val city = restaurant.optJSONObject("plus_code")
                                ?.optString("compound_code") ?: "Unknown City"
                            val location = restaurant.getJSONObject("geometry").getJSONObject("location")
                            val latitude = location.getDouble("lat")
                            val longitude = location.getDouble("lng")
                            restaurants.add(Restaurant(name, city, latitude, longitude))
                        }

                        runOnUiThread {
                            restaurantAdapter.notifyDataSetChanged()
                        }
                    } else {
                        Log.e("API Response", "Response body is null")
                    }
                }
            }
        })
    }


    private fun fetchNextPage(token: String) {
        val apiKey = "AIzaSyDQjhJFyva5_qjpmDsFWXZABUtMQH1lxCM"
        val nextPageUrl =
            "https://maps.googleapis.com/maps/api/place/textsearch/json?pagetoken=$token&key=$apiKey"

        val client = OkHttpClient()
        val request = Request.Builder().url(nextPageUrl).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("API Error", "Failed to fetch next page: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { res ->
                    if (!res.isSuccessful) {
                        Log.e("API Error", "Next page request failed with code: ${res.code}")
                        return
                    }

                    val responseBody = res.body?.string()
                    if (responseBody != null) {
                        val jsonResponse = JSONObject(responseBody)
                        val results = jsonResponse.getJSONArray("results")

                        // Limit to 10 restaurants per page
                        val maxResults = minOf(10, results.length())
                        for (i in 0 until maxResults) {
                            val restaurant = results.getJSONObject(i)
                            val name = restaurant.getString("name")
                            val city = restaurant.optJSONObject("plus_code")
                                ?.optString("compound_code") ?: "Unknown City"
                            val location = restaurant.getJSONObject("geometry").getJSONObject("location")
                            val latitude = location.getDouble("lat")
                            val longitude = location.getDouble("lng")
                            restaurants.add(Restaurant(name, city, latitude, longitude))
                        }

                        runOnUiThread {
                            restaurantAdapter.notifyDataSetChanged()
                        }

                        // Check if there is another page
                        val nextPageToken = jsonResponse.optString("next_page_token")
                        if (!nextPageToken.isNullOrEmpty()) {
                            fetchNextPage(nextPageToken)
                        } else {
                            Log.d("next pageAPI Response", "No more pages to fetch")
                        }
                    } else {
                        Log.e("API Response", "Response body is null")
                    }
                }
            }
        })
    }


    private fun openGoogleMaps(restaurant: Restaurant) {
        val uri =
            Uri.parse("geo:${restaurant.latitude},${restaurant.longitude}?q=${Uri.encode(restaurant.name)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }
}
