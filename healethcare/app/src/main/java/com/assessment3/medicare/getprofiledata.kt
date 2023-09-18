package com.assessment3.medicare

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.assessment3.medicare.databinding.ActivityGetprofiledataBinding
import com.bumptech.glide.Glide
import java.io.IOException

class getprofiledata : AppCompatActivity() {
    private lateinit var binding: ActivityGetprofiledataBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var userId: String

    private val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetprofiledataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        userId = auth.currentUser?.uid ?: ""

        if (userId.isEmpty()) {
            finish()
            return
        }

        // Check if the READ_EXTERNAL_STORAGE permission is granted
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission already granted, you can proceed with your code here
            loadDataFromFirebase()
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun loadDataFromFirebase() {
        // Reference to the current user's data in the database
        val userRef = database.getReference("MedUsers").child(userId)

        // Attach a ValueEventListener to listen for changes in the user's data
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Retrieve user data as a Map
                val userData = snapshot.value as? Map<*, *>

                // Check if userData is not null and contains the required fields
                if (userData != null && userData.containsKey("firstName") && userData.containsKey("address")
                    && userData.containsKey("tel") && userData.containsKey("bloodGroup") && userData.containsKey("bloodType")
                ) {
                    val firstName = userData["firstName"].toString()
                    val address = userData["address"].toString()
                    val tel = userData["tel"].toString()
                    val bloodGroup = userData["bloodGroup"].toString()
                    val bloodtype =userData["bloodType"].toString()

                    // Update the UI with the retrieved data
                    binding.textViewName.text = firstName
                    binding.textViewAddress.text = address
                    binding.textViewTelephone.text = tel
                    binding.textViewBloodGroup.text = bloodGroup
                    binding.textViewBloodtype.text=bloodtype

                    // If "profileImage" field is present, load the image from Firebase Storage
                    if (userData.containsKey("profileImage")) {
                        val imageUrl = userData["profileImage"].toString()
                        Glide.with(this@getprofiledata)
                            .load(imageUrl)
                            .into(binding.imageViewProfile)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database read error if necessary
            }
        })
    }

    private fun fetchImageFromFirebase(imageUrl: String) {
        val imageRef = storage.getReferenceFromUrl(imageUrl)

        val ONE_MEGABYTE = 1024 * 1024.toLong()

        imageRef.getBytes(ONE_MEGABYTE)
            .addOnSuccessListener { imageData ->
                // imageData contains the bytes of the image, you can now use it to set the image to your ImageView
                val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                binding.imageViewProfile.setImageBitmap(bitmap)
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Toast.makeText(this, "Failed to load profile image", Toast.LENGTH_SHORT).show()
            }
    }

}