package com.example.foodiefling.ui

data class User(
    var userId: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val age: Int = 0,
    val gender: String = "",
    val password: String = "",
    val preferences: Preferences? = Preferences(),
    val profile: Profile? = null,
    val matches: MutableList<Match>? = mutableListOf()
)

data class Preferences(
    var maxAge: Int? = null,
    var minAge: Int? = null,
    var location: String? = null,
    var drink: String? = null,
    var gender: String? = null,
    var raceOrEthnicity: String? = null,
    var religion: String? = null,
    var smoke: String? = null
)

data class Profile(
    var city: String? = null,
    val bio: String? = null,
    var cuisine: MutableList<String>? = null,
    var dish: MutableList<String>? = null,
    var drink: String? = null,
    var education: String? = null,
    val foodAllergy: List<String>? = null,
    val height: Double? = null,
    var allergies: String? = null,
    var kids: String? = null,
    var familyPlans: String? = null,
    val location: String? = null,
    var photos: List<String>? = null,
    var raceOrEthnicity: String? = null,
    var religion: String? = null,
    var genderPref: String? = null,
    var smoke: String? = null,
    val vegan: Boolean? = null,
    val vegetarian: Boolean? = null,
    var whatLookingFor: String? = null
)


data class Match(
    val matchedUser: Int? = null,
    val score: String? = null,
    val cuisines: MutableList<String>? = mutableListOf(),
    val dishes: MutableList<String>? = mutableListOf()
)