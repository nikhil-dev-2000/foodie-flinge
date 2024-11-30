package com.example.foodiefling.ui

data class User(
    var userId: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val age: Int = 0,
    val gender: String = "",
    val password: String = "",
    val preferences: Preferences? = null,
    val profile: Profile? = null,
    val matches: List<Match>? = null
)

data class Preferences(
    val age: String? = null,
    val distance: String? = null,
    val drink: String? = null,
    val gender: String? = null,
    val raceOrEthnicity: String? = null,
    val religion: String? = null,
    val smoke: String? = null
)

data class Profile(
    val bio: String? = null,
    val cuisine: List<Cuisine>? = null,
    val dish: List<Dish>? = null,
    val drink: Boolean? = null,
    val education: String? = null,
    val foodAllergy: List<String>? = null,
    val height: Double? = null,
    val kids: Boolean? = null,
    val location: String? = null,
    var photos: List<String>? = null,
    val raceOrEthnicity: String? = null,
    val religion: String? = null,
    val sexualPreference: String? = null,
    val smoke: Boolean? = null,
    val vegan: Boolean? = null,
    val vegetarian: Boolean? = null,
    val whatLookingFor: String? = null
)

data class Cuisine(
    val cuisineId: Int = 0,
    val cuisineName: String = ""
)

data class Dish(
    val dishId: Int = 0,
    val dishName: String = ""
)

data class Match(
    val matchedUser: Int? = null,
    val score: String? = null,
    val cuisines: List<Cuisine>? = null,
    val dishes: List<Dish>? = null
)