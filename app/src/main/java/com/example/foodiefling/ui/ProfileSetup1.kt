package com.example.foodiefling.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileSetup1 : AppCompatActivity() {

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private val PERMISSION_REQUEST_CODE = 1001
    private val isImageUploaded = BooleanArray(6) // Array to track uploaded images
    private var currentLayoutId: Int = 0 // To hold the currently clicked layout ID
    private var currentLayoutIndex: Int = 0 // To hold the currently clicked layout index
    private var currentCrossButtonId: Int = 0 // To hold the currently clicked cross button ID
    private lateinit var storageRef: StorageReference

    private lateinit var user: User

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_setup1)

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            auth.signInAnonymously()
                .addOnSuccessListener {
                    Log.d("FirebaseAuth", "User signed in anonymously")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseAuth", "Anonymous sign-in failed: ${e.message}")
                }
        }

        val db: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        storageRef = FirebaseStorage.getInstance("gs://foodie-fling.firebasestorage.app").reference


//        val intent = intent
//        val userId = intent.getStringExtra("user_id")
        val userId = "User_4021"

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

                uploadImageToFirebase(selectedUri, userId)
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
                val photoUrl = user.profile?.photos?.get(index)
                if (photoUrl != null) {
                    deleteImageFromFirebase(photoUrl, userId, index)
                }
                // Reset the layout background and hide the cross button
                layout.background = getDrawable(R.drawable.background) // Change to your default background
                crossButton.visibility = ImageView.GONE
                isImageUploaded[index] = false // Reset the uploaded state

                // Show the image view again
                val imageView = findViewById<ImageView>(getImageViewId(index))
                imageView.visibility = ImageView.VISIBLE
            }
        }

        val bio = findViewById<EditText>(R.id.bio)
        val nextButton = findViewById<Button>(R.id.next_button)
        nextButton.setOnClickListener {
            val dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("profile").child("bio")
            dbRef.setValue(bio.text.toString()).addOnSuccessListener {
                Log.d("Firebase Database", "Bio updated successfully")
            }.addOnFailureListener { e ->
                Log.e("Firebase Database", "Error updating bio: ${e.message}")
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

    private fun uploadImageToFirebase(uri: Uri, userId: String) {
        val uniqueFileName = "${System.currentTimeMillis()}_${uri.lastPathSegment}"
        val photoRef: StorageReference = storageRef.child("users/$userId/photos/$uniqueFileName")

        val dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("profile").child("photos")

        photoRef.putFile(uri)
            .addOnSuccessListener {
                photoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val photoUrl = downloadUri.toString()

                    dbRef.get().addOnSuccessListener { snapshot ->
                        // Use GenericTypeIndicator to handle the list of Strings
                        val typeIndicator = object : GenericTypeIndicator<List<String>>() {}
                        val currentPhotos: List<String> = snapshot.getValue(typeIndicator) ?: emptyList()

                        // Add the new photo URL
                        val updatedPhotos = currentPhotos.toMutableList()
                        updatedPhotos.add(photoUrl)

                        // Save the updated photos list back to Firebase
                        dbRef.setValue(updatedPhotos).addOnSuccessListener {
                            user.profile?.photos = updatedPhotos
                            Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                            Log.d("Firebase Database", "Updated Photos List: $updatedPhotos")
                        }.addOnFailureListener { e ->
                            Log.e("Firebase Database", "Error saving photo URL: ${e.message}")
                        }
                    }.addOnFailureListener { e ->
                        Log.e("Firebase Database", "Error retrieving current photos: ${e.message}")
                    }
                }.addOnFailureListener { e ->
                    Log.e("Firebase Storage", "Error getting download URL: ${e.message}")
                }
            }.addOnFailureListener { e ->
                Log.e("Firebase Storage", "Error uploading image: ${e.message}")
            }
    }



    private fun deleteImageFromFirebase(photoUrl: String, userId: String, index: Int) {
        Log.d("Firebase Storage", "Attempting to delete image with URL: $photoUrl")

        val dbRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("profile").child("photos")
        val storage = FirebaseStorage.getInstance("gs://foodie-fling.firebasestorage.app")

        Log.d("Firebase Storage", "Attempting to delete image with URL: $photoUrl")

        // Extract the storage path from the URL
        val storagePath = Uri.parse(photoUrl).path?.split("/o/")?.get(1)?.split("?")?.get(0)?.replace("%2F", "/")

        if (storagePath == null) {
            Log.e("Firebase Storage", "Invalid photo URL: $photoUrl")
            Toast.makeText(this, "Failed to delete image: Invalid photo URL.", Toast.LENGTH_SHORT).show()
            return
        }

        val photoRef = storage.getReference(storagePath)

        Log.d("Firebase Storage", "Attempting to delete file at: $storagePath")

        // Delete from Firebase Storage
        photoRef.delete()
            .addOnSuccessListener {
                Log.d("Firebase Storage", "Image deleted successfully from storage")

                // Remove from Firebase Database
                dbRef.get().addOnSuccessListener { snapshot ->
                    val typeIndicator = object : GenericTypeIndicator<List<String>>() {}
                    val currentPhotos: List<String> = snapshot.getValue(typeIndicator) ?: emptyList()

                    val updatedPhotos = currentPhotos.toMutableList()
                    if (index < updatedPhotos.size) {
                        updatedPhotos.removeAt(index)
                    }

                    dbRef.setValue(updatedPhotos).addOnSuccessListener {
                        Toast.makeText(this, "Image deleted successfully", Toast.LENGTH_SHORT).show()
                        Log.d("Firebase Database", "Updated Photos List after Deletion: $updatedPhotos")
                        user.profile?.photos = updatedPhotos
                    }.addOnFailureListener { e ->
                        Log.e("Firebase Database", "Error updating photo list: ${e.message}")
                    }
                }.addOnFailureListener { e ->
                    Log.e("Firebase Database", "Error retrieving current photos: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase Storage", "Error deleting image from storage: ${e.message}")
                Toast.makeText(this, "Failed to delete image from storage.", Toast.LENGTH_SHORT).show()
            }
    }




}