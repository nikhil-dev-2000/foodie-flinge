package com.example.foodiefling.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodiefling.R

data class Restaurant(
    val name: String,
    val city: String,
    val latitude: Double,
    val longitude: Double
)

class RestaurantAdapter(
    private var restaurants: List<Restaurant>,
    private val onItemClick: (Restaurant) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    fun updateData(newRestaurants: List<Restaurant>) {
        restaurants = newRestaurants
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_restaurant, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurants[position]
        holder.name.text = restaurant.name
        holder.city.text = restaurant.city
        holder.itemView.setOnClickListener { onItemClick(restaurant) }
    }

    override fun getItemCount() = restaurants.size

    class RestaurantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.restaurantName)
        val city: TextView = view.findViewById(R.id.restaurantCity)
    }
}
