package com.example.foodiefling.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.foodiefling.R

class ProfileSetup3 : AppCompatActivity() {

    var genderPref: String? = null
    var relationshipPref: String? = null

    val men = findViewById<LinearLayout>(R.id.menL)
    val women = findViewById<LinearLayout>(R.id.womenL)
    val anyone = findViewById<LinearLayout>(R.id.anyoneL)
    val nonBinary = findViewById<LinearLayout>(R.id.nonBinaryL)

    val longTerm = findViewById<LinearLayout>(R.id.longTermL)
    val ENM = findViewById<LinearLayout>(R.id.ENML)
    val marriage = findViewById<LinearLayout>(R.id.marriageL)
    val casual = findViewById<LinearLayout>(R.id.casualL)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_setup3)


        val clickListener = View.OnClickListener { view ->
            setGenderPrefColor()
            view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C3D03A8C"))
            when (view.id) {
                R.id.menL -> genderPref("men")
                R.id.womenL -> genderPref("women")
                R.id.anyoneL -> genderPref("anyone")
                R.id.nonBinaryL -> genderPref("non-binary")
            }
        }
        men.setOnClickListener(clickListener)
        women.setOnClickListener(clickListener)
        anyone.setOnClickListener(clickListener)
        nonBinary.setOnClickListener(clickListener)

        val clickListener2 = View.OnClickListener { view ->
            setRelationshipPrefColor()
            view.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#C3D03A8C"))
            when (view.id) {
                R.id.longTermL -> relationshipPref("long-term")
                R.id.ENML -> relationshipPref("non-monogamy")
                R.id.marriageL -> relationshipPref("marriage")
                R.id.casualL -> relationshipPref("casual")
            }
        }
        longTerm.setOnClickListener(clickListener2)
        ENM.setOnClickListener(clickListener2)
        marriage.setOnClickListener(clickListener2)
        casual.setOnClickListener(clickListener2)

    }

    private fun genderPref(gender: String) {
        genderPref = gender
    }

    private fun relationshipPref(relationship: String) {
        relationshipPref = relationship
    }

    private fun setGenderPrefColor() {
        men.backgroundTintList = null
        women.backgroundTintList = null
        anyone.backgroundTintList = null
        nonBinary.backgroundTintList = null
    }

    private fun setRelationshipPrefColor() {
        longTerm.backgroundTintList = null
        ENM.backgroundTintList = null
        marriage.backgroundTintList = null
        casual.backgroundTintList = null
    }


}