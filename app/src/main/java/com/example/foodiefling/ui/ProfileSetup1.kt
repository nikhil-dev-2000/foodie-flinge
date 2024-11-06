package com.example.foodiefling.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.foodiefling.R

class ProfileSetup1 : AppCompatActivity() {

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private val PERMISSION_REQUEST_CODE = 1001
    private val isImageUploaded = BooleanArray(6) // Array to track uploaded images
    private var currentLayoutId: Int = 0 // To hold the currently clicked layout ID
    private var currentLayoutIndex: Int = 0 // To hold the currently clicked layout index
    private var currentCrossButtonId: Int = 0 // To hold the currently clicked cross button ID

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_setup1)

        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let { selectedUri ->
                // Find the clicked layout based on the tag
                val clickedLayout = findViewById<LinearLayout>(currentLayoutId)
                clickedLayout.background = getDrawableFromUri(selectedUri)

                // Hide the image view
                val imageView = findViewById<ImageView>(getImageViewId(currentLayoutIndex))
                imageView.visibility = ImageView.GONE

                // Show the cross button
                val crossButton = findViewById<ImageView>(currentCrossButtonId)
                crossButton.visibility = ImageView.VISIBLE
                isImageUploaded[currentLayoutIndex] = true
            } ?: run {
                Toast.makeText(this, "Image selection cancelled", Toast.LENGTH_SHORT).show()
            }
        }

        // Array of layout IDs
        val layoutIds = arrayOf(
            R.id.image_layout1,
            R.id.image_layout2,
            R.id.image_layout3,
            R.id.image_layout4,
            R.id.image_layout5,
            R.id.image_layout6
        )

        // Array of cross button IDs
        val crossButtonIds = arrayOf(
            R.id.cross_button1,
            R.id.cross_button2,
            R.id.cross_button3,
            R.id.cross_button4,
            R.id.cross_button5,
            R.id.cross_button6
        )

        // Set up click listeners for all image layouts and cross buttons
        layoutIds.forEachIndexed { index, layoutId ->
            val layout = findViewById<LinearLayout>(layoutId)
            val crossButton = findViewById<ImageView>(crossButtonIds[index])

            layout.setOnClickListener {
                if (!isImageUploaded[index]) {
                    if (checkPermissions()) {
                        currentLayoutId = layoutId // Store the current layout ID
                        currentLayoutIndex = index // Store the current layout index
                        currentCrossButtonId = crossButtonIds[index] // Store the current cross button ID
                        imagePickerLauncher.launch("image/*")
                    } else {
                        requestPermissions()
                    }
                } else {
                    Toast.makeText(this, "Image has already been uploaded for this slot", Toast.LENGTH_SHORT).show()
                }
            }

            // Set up click listener for the cross button
            crossButton.setOnClickListener {
                // Reset the layout background and hide the cross button
                layout.background = getDrawable(R.drawable.background) // Change to your default background
                crossButton.visibility = ImageView.GONE
                isImageUploaded[index] = false // Reset the uploaded state

                // Show the image view again
                val imageView = findViewById<ImageView>(getImageViewId(index))
                imageView.visibility = ImageView.VISIBLE
            }
        }
    }

    private fun getImageViewId(index: Int): Int {
        return when (index) {
            0 -> R.id.image1
            1 -> R.id.image2
            2 -> R.id.image3
            3 -> R.id.image4
            4 -> R.id.image5
            5 -> R.id.image6
            else -> throw IllegalArgumentException("Invalid index")
        }
    }


    private fun checkPermissions(): Boolean {
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
        return readPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
            PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePickerLauncher.launch("image/*")
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getDrawableFromUri(uri: Uri): Drawable? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            Drawable.createFromStream(inputStream, uri.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}