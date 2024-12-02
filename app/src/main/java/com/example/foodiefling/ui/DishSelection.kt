package com.example.foodiefling.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodiefling.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DishSelection : AppCompatActivity() {

    private fun cuisineIndex1(index: Int): Int {
        return when (index) {
            0 -> R.id.c1d1l
            1 -> R.id.c1d2l
            2 -> R.id.c1d3l
            3 -> R.id.c1d4l
            4 -> R.id.c1d5l
            else -> throw IllegalArgumentException("Invalid index")
        }
    }

    private fun cuisineIndex2(index: Int): Int {
        return when (index) {
            0 -> R.id.c2d1l
            1 -> R.id.c2d2l
            2 -> R.id.c2d3l
            3 -> R.id.c2d4l
            4 -> R.id.c2d5l
            else -> throw IllegalArgumentException("Invalid index")
        }
    }

    private fun cuisineIndex3(index: Int): Int {
        return when (index) {
            0 -> R.id.c3d1l
            1 -> R.id.c3d2l
            2 -> R.id.c3d3l
            3 -> R.id.c3d4l
            4 -> R.id.c3d5l
            else -> throw IllegalArgumentException("Invalid index")
        }
    }

    private fun dishIndex1(index: Int): Int {
        return when (index) {
            0 -> R.id.c1d1
            1 -> R.id.c1d2
            2 -> R.id.c1d3
            3 -> R.id.c1d4
            4 -> R.id.c1d5
            else -> throw IllegalArgumentException("Invalid index")
        }
    }

    private fun dishIndex2(index: Int): Int {
        return when (index) {
            0 -> R.id.c2d1
            1 -> R.id.c2d2
            2 -> R.id.c2d3
            3 -> R.id.c2d4
            4 -> R.id.c2d5
            else -> throw IllegalArgumentException("Invalid index")
        }
    }

    private fun dishIndex3(index: Int): Int {
        return when (index) {
            0 -> R.id.c3d1
            1 -> R.id.c3d2
            2 -> R.id.c3d3
            3 -> R.id.c3d4
            4 -> R.id.c3d5
            else -> throw IllegalArgumentException("Invalid index")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dish_selection)

        lateinit var user: User

        val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

        val intent = intent
        val userId = intent.getStringExtra("user_id")
        val selectedCuisines = intent.getStringArrayExtra("selected_cuisines")

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

        val selectedDishes1: MutableList<String> = mutableListOf()
        val selectedDishes2: MutableList<String> = mutableListOf()
        val selectedDishes3: MutableList<String> = mutableListOf()

        val cuisine1 = selectedCuisines?.get(0)
        val cuisine2 = selectedCuisines?.get(1)
        val cuisine3 = selectedCuisines?.get(2)

        val expand1 = findViewById<ImageView>(R.id.expand1)
        val expand2 = findViewById<ImageView>(R.id.expand2)
        val expand3 = findViewById<ImageView>(R.id.expand3)

        val c1 = findViewById<TextView>(R.id.c1)
        val c2 = findViewById<TextView>(R.id.c2)
        val c3 = findViewById<TextView>(R.id.c3)

        c1.text = cuisine1
        c2.text = cuisine2
        c3.text = cuisine3

        val dishesPopUp = findViewById<ScrollView>(R.id.dishes_popup)
        val closePopUp = findViewById<ImageView>(R.id.close_popup)

        val cuisines = mapOf(
            "American" to listOf("Apple Pie", "BBQ Ribs", "BLT Sandwich", "Cheeseburger", "Chili", "Clam Chowder",
                "Cornbread", "Fried Chicken", "Hot Dog", "Mac and Cheese", "Meatloaf", "Pancakes",
                "Philly Cheesesteak", "Cobb Salad", "Buffalo Wings"),

            "Brazilian" to listOf("Acarajé", "Brigadeiro", "Churrasco", "Coxinha", "Empadão (Pot Pie)", "Farofa",
                "Feijoada", "Moqueca (Fish Stew)", "Pastel", "Pão de Queijo (Cheese Bread)",
                "Quindim", "Romeu e Julieta (Cheese and Guava Dessert)", "Tapioca Pancakes", "Vatapá"),

            "Caribbean" to listOf("Ackee and Saltfish", "Callaloo", "Conch Fritters", "Curry Goat", "Dumplings",
                "Jerk Chicken", "Mofongo", "Pepperpot", "Plantains", "Rice and Peas", "Roti",
                "Rum Cake", "Souse", "Sorrel Drink"),

            "Chinese" to listOf("Char Siu (BBQ Pork)", "Chow Mein", "Dim Sum", "Dumplings", "Egg Tarts",
                "Fried Rice", "Hot Pot", "Kung Pao Chicken", "Ma Po Tofu", "Mooncakes",
                "Peking Duck", "Sichuan Hot & Sour Soup", "Spring Rolls", "Sweet and Sour Pork",
                "Wonton Soup"),

            "Ethiopian" to listOf("Azifa (Lentil Salad)", "Berbere Spices", "Buticha (Chickpea Dip)", "Doro Wat",
                "Firfir", "Genfo (Porridge)", "Injera", "Kitfo", "Kitcha Fit-Fit (Bread Stew)",
                "Misir Wat (Lentil Stew)", "Shiro", "Tibs", "T'ej (Honey Wine)",
                "Yetsom Beyaynetu (Vegetarian Platter)"),

            "French" to listOf("Baguette", "Beef Bourguignon", "Bouillabaisse", "Cassoulet", "Coq au Vin",
                "Crème Brûlée", "Croissant", "Escargot", "French Onion Soup", "Quiche Lorraine",
                "Ratatouille", "Soufflé", "Tarte Tatin", "Duck Confit"),

            "German" to listOf("Apfelkuchen (Apple Cake)", "Black Forest Cake", "Bratwurst", "Kartoffelsalat (Potato Salad)",
                "Knödel (Dumplings)", "Lebkuchen (Gingerbread)", "Maultaschen (Pasta Pockets)",
                "Pretzels", "Rouladen", "Sauerbraten", "Schnitzel", "Spätzle", "Strudel", "Weisswurst"),

            "Greek" to listOf("Avgolemono Soup", "Baklava", "Dolmades (Stuffed Grape Leaves)", "Fasolada (Bean Soup)",
                "Greek Salad", "Gyro", "Kleftiko (Lamb Dish)", "Loukoumades (Greek Doughnuts)",
                "Moussaka", "Pastitsio (Greek Lasagna)", "Saganaki (Fried Cheese)",
                "Spanakopita (Spinach Pie)", "Tzatziki"),

            "Indian" to listOf("Aloo Gobi", "Biryani", "Butter Chicken", "Chicken Tikka Masala", "Dal Makhani",
                "Golgappe", "Gulab Jamun", "Kheer (Rice Pudding)", "Masala Dosa", "Naan",
                "Paneer Tikka", "Roti", "Samosas", "Shahi Paneer", "Tandoori Chicken"),

            "Italian" to listOf("Arancini", "Bruschetta", "Focaccia Bread", "Gelato", "Gnocchi", "Lasagna",
                "Minestrone Soup", "Ossobuco", "Pasta Carbonara", "Panna Cotta", "Pizza Margherita",
                "Ravioli", "Risotto", "Tiramisu"),
            "Japanese" to listOf("Agedashi Tofu", "Kaiseki (Traditional Multi-Course Meal)", "Miso Soup", "Mochi",
                "Okonomiyaki", "Onigiri", "Ramen", "Sashimi", "Shabu-Shabu",
                "Takoyaki (Octopus Balls)", "Tempura", "Tonkatsu", "Udon Noodles", "Yakitori"),

            "Korean" to listOf("Banchan (Side Dishes)", "Bibimbap", "Bulgogi", "Dakgangjeong (Sweet Crispy Chicken)",
                "Galbi (Short Ribs)", "Haemul Pajeon (Seafood Pancake)", "Japchae (Glass Noodles)",
                "Kimchi", "Kimchi Jjigae (Kimchi Stew)", "Kimbap", "Naengmyeon (Cold Noodles)",
                "Samgyeopsal (Pork Belly)", "Sundubu Jjigae (Soft Tofu Stew)", "Tteokbokki (Rice Cakes)"),

            "Lebanese" to listOf("Baba Ghanoush", "Baklava", "Batata Harra (Spicy Potatoes)", "Fattoush",
                "Falafel", "Hummus", "Kafta", "Kibbeh", "Manakish",
                "Moujadara (Lentil and Rice Dish)", "Shawarma", "Tabbouleh",
                "Knafeh (Cheese Pastry)", "Shish Taouk"),

            "Mexican" to listOf("Arroz con Leche (Rice Pudding)", "Carnitas", "Chilaquiles", "Chiles en Nogada",
                "Enchiladas", "Elote (Mexican Street Corn)", "Flautas", "Guacamole",
                "Mole Poblano", "Pozole", "Quesadillas", "Tamales", "Tacos", "Huevos Rancheros"),

            "Moroccan" to listOf("Beghrir (Pancakes)", "Briouats (Stuffed Pastry)", "Couscous", "Harira (Soup)",
                "Harcha (Semolina Bread)", "Kaab el Ghazal (Almond Pastry)", "Kefta (Meatballs)",
                "Mechoui (Roasted Lamb)", "Mrouzia (Lamb with Honey)", "Pastilla (Pastry)",
                "Rfissa (Chicken and Lentils)", "Tagine", "Zaalouk (Eggplant Salad)"),

            "Peruvian" to listOf("Aji de Gallina", "Anticuchos", "Causa", "Ceviche", "Chupe de Camarones",
                "Lomo Saltado", "Mazamorra Morada (Purple Corn Dessert)", "Papa a la Huancaína",
                "Pollo a la Brasa", "Picarones", "Rocoto Relleno", "Suspiro a la Limeña (Caramel Dessert)",
                "Tacu Tacu", "Tiradito"),

            "Spanish" to listOf("Albondigas (Meatballs)", "Calamari", "Churros", "Escalivada (Grilled Vegetables)",
                "Flan", "Gazpacho", "Jamón Ibérico", "Manchego Cheese", "Paella",
                "Patatas Bravas", "Pimientos de Padrón", "Pulpo a la Gallega (Octopus)",
                "Tapas", "Tortilla Española (Spanish Omelette)"),

            "Thai" to listOf("Green Curry", "Kai Jeow (Thai Omelette)", "Khao Soi", "Larb (Minced Meat Salad)",
                "Massaman Curry", "Moo Ping (Grilled Pork Skewers)", "Pad Thai", "Panang Curry",
                "Som Tam (Papaya Salad)", "Sticky Rice with Mango", "Tom Kha Gai (Coconut Chicken Soup)",
                "Tom Yum Soup", "Thai Fried Rice", "Satay Skewers"),

            "Turkish" to listOf("Adana Kebab", "Baklava", "Börek (Stuffed Pastry)", "Çiğ Köfte",
                "Doner Kebab", "Hummus", "Kofta", "Kunefe (Cheese Dessert)",
                "Lahmacun", "Manti (Turkish Dumplings)", "Menemen (Egg Dish)",
                "Simit (Turkish Bagel)", "Turkish Delight", "Pide (Turkish Pizza)"),

            "Vietnamese" to listOf("Banh Mi", "Banh Xeo (Vietnamese Pancakes)", "Bun Bo Hue",
                "Bun Cha", "Cao Lau", "Cha Ca (Turmeric Fish)",
                "Com Tam (Broken Rice)", "Goi Cuon (Fresh Rolls)",
                "Hu Tieu (Pork and Seafood Noodle Soup)", "Nem Ran (Fried Rolls)",
                "Pho", "Thit Kho To (Caramelized Pork)", "Xoi (Sticky Rice)",
                "Che (Sweet Dessert Soup)")
        )

        expand1.setOnClickListener{
            Log.d("aa", "in expand1")
            Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show()
            dishesPopUp.visibility = View.VISIBLE
            closePopUp.visibility = View.VISIBLE
            if (cuisine1 != null) {
                showDishes(cuisine1, cuisines, selectedDishes1, 1)
            }
        }
        expand2.setOnClickListener{
            Log.d("aa", "in expand2")
            Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show()
            dishesPopUp.visibility = View.VISIBLE
            closePopUp.visibility = View.VISIBLE
            if (cuisine2 != null) {
                showDishes(cuisine2, cuisines, selectedDishes2, 2)
            }
        }
        expand3.setOnClickListener{
            Log.d("aa", "in expand3")
            Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show()
            dishesPopUp.visibility = View.VISIBLE
            closePopUp.visibility = View.VISIBLE
            if (cuisine3 != null) {
                showDishes(cuisine3, cuisines, selectedDishes3, 3)
            }
        }
        closePopUp.setOnClickListener{
            dishesPopUp.visibility = View.GONE
            closePopUp.visibility = View.GONE
        }

        val next = findViewById<Button>(R.id.next)
        next.setOnClickListener{
            user.profile?.dish = (selectedDishes1 + selectedDishes2 + selectedDishes3).toMutableList()
            user.preferences?.maxAge = 999
            user.preferences?.minAge = 18
            user.preferences?.gender = user.profile?.genderPref
            user.preferences?.location = user.profile?.city
            user.preferences?.drink = user.profile?.drink
            user.preferences?.raceOrEthnicity = user.profile?.raceOrEthnicity
            user.preferences?.religion = user.profile?.religion
            db.child(userId!!).setValue(user)
            val intent1 = Intent(this, PotentialMatches::class.java)
            intent1.putExtra("user_id", userId)
            startActivity(intent1)
        }


    }

    private fun showDishes(cuisine: String, cuisines: Map<String, List<String>>, selectedDishes: MutableList<String>, pos: Int) {
        Log.d("aa", "in showDishes")
        val container = findViewById<LinearLayout>(R.id.dishes)
        val dishes = cuisines[cuisine]
        if (dishes != null) {
            for (dish in dishes) {
                val linearLayout = LinearLayout(this).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 10, 0, 0)
                    }
                    background = resources.getDrawable(R.drawable.background, null) // Set background drawable
                    gravity = android.view.Gravity.CENTER // Center the content
                    orientation = LinearLayout.VERTICAL
                    setPadding(18, 18, 18, 18)

                    setOnClickListener {
                        if (selectedDishes.contains(dish)) {
                            backgroundTintList = null
                            selectedDishes.remove(dish)
                            updateSelectedDishes(selectedDishes, pos)
                        } else if(selectedDishes.size < 5) {
                            selectedDishes.add(dish)
                            updateSelectedDishes(selectedDishes, pos)
                            backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C3D03A8C"))
                        }
                    }
                }
                val textView = TextView(this).apply {
                    text = dish
                    textSize = 20.0F
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }
                linearLayout.addView(textView)
                container.addView(linearLayout)
            }
        }
    }
    private fun updateSelectedDishes(dishes: List<String>, pos: Int) {

        if(pos == 1){
            for (i in 0..4) {
                if(i < dishes.size){
                    findViewById<LinearLayout>(cuisineIndex1(i)).visibility = View.VISIBLE
                    findViewById<TextView>(dishIndex1(i)).text = dishes[i]
                } else {
                    findViewById<LinearLayout>(cuisineIndex1(i)).visibility = View.GONE
                }
            }
        } else if(pos == 2){
            for (i in 0..4) {
                if(i < dishes.size){
                    findViewById<LinearLayout>(cuisineIndex2(i)).visibility = View.VISIBLE
                    findViewById<TextView>(dishIndex2(i)).text = dishes[i]
                } else {
                    findViewById<LinearLayout>(cuisineIndex2(i)).visibility = View.GONE
                }
            }
        } else if(pos == 3){
            for (i in 0..4) {
                if(i < dishes.size){
                    findViewById<LinearLayout>(cuisineIndex3(i)).visibility = View.VISIBLE
                    findViewById<TextView>(dishIndex3(i)).text = dishes[i]
                } else {
                    findViewById<LinearLayout>(cuisineIndex3(i)).visibility = View.GONE
                }
            }
        }
    }
}