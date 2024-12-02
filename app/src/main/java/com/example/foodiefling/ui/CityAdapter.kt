package com.example.foodiefling.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CityAdapter(
private var citiesWithStates: List<String>,
private val onCityClick: (String) -> Unit
) : RecyclerView.Adapter<CityAdapter.CityViewHolder>() {

    inner class CityViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cityName: TextView = view.findViewById(android.R.id.text1)

        fun bind(cityWithState: String) {
            cityName.text = cityWithState // Directly set the formatted text
            cityName.setOnClickListener { onCityClick(cityWithState) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return CityViewHolder(view)
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        holder.bind(citiesWithStates[position])
    }

    override fun getItemCount(): Int = citiesWithStates.size

    fun updateCities(newCitiesWithStates: List<String>) {
        citiesWithStates = newCitiesWithStates
        notifyDataSetChanged()
    }
}


