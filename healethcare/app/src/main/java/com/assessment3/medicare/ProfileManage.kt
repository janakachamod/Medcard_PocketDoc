package com.assessment3.medicare

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.assessment3.medicare.databinding.ActivityProfileManageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ProfileManage : AppCompatActivity() {
    private lateinit var binding: ActivityProfileManageBinding
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri: Uri? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileManageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("MedUsers")
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Redirect to the login screen or show a message asking the user to sign in
            // For example:
            // val signInIntent = Intent(this, SignInActivity::class.java)
            // startActivity(signInIntent)
            Toast.makeText(this, "Please sign in before accessing this feature.", Toast.LENGTH_SHORT).show()
            finish() // Finish the activity to prevent further actions without authentication
            return
        }

        binding.Submit.setOnClickListener {
            val firstName = binding.editTextName.text.toString()
            val address = binding.editTextAddress.text.toString()
            val tel = binding.editTextPhoneNumber.text.toString()
            val id = currentUser.uid // Use the current user's ID

            // Determine the selected blood group
            val selectedBloodGroup = when {
                binding.checkBoxBloodGroupA.isChecked -> "A"
                binding.checkBoxBloodGroupB.isChecked -> "B"
                binding.checkBoxBloodGroupO.isChecked -> "O"
                binding.checkBoxBloodGroupAB.isChecked -> "AB"
                else -> ""
            }
            val selectedBloodType = when {
                binding.checkBoxBloodtype1.isChecked -> "Positive"
                binding.checkBoxBloodtype2.isChecked -> "Negative"
                else -> ""
            }

            // Create a map for the user data
            val userData = hashMapOf<String, Any>(
                "firstName" to firstName,
                "address" to address,
                "tel" to tel,
                "bloodGroup" to selectedBloodGroup,
                "bloodType"  to selectedBloodType
            )

            // If an image is selected, add the image URL to the user data
            selectedImageUri?.let { uri ->
                userData["profileImage"] = uri.toString()
                uploadImageToFirebaseStorage(uri) // Upload the image to Firebase Storage
            }

            database.child(id).updateChildren(userData)
                .addOnSuccessListener {
                    clearFields()
                    Toast.makeText(this, "Successfully Updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update user", Toast.LENGTH_SHORT).show()
                }
        }

        binding.imageViewProfilePicture.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                binding.imageViewProfilePicture.setImageURI(uri)
            }
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val storageRef: StorageReference = storage.reference
        val imagesRef: StorageReference = storageRef.child("profile_imagesmed/${System.currentTimeMillis()}.jpg")

        imagesRef.putFile(imageUri)
            .addOnSuccessListener {
                // Get the download URL of the uploaded image
                imagesRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    // Save the download URL in the database
                    val downloadUrl = downloadUri.toString()

                    // Now, you can use this downloadUrl to store in the database or display the image.
                    // In this case, we have already saved it as "profileImage" in the user data.
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        // Clear all the input fields and reset the image view
        binding.editTextName.text.clear()
        binding.editTextAddress.text.clear()
        binding.editTextPhoneNumber.text.clear()
        binding.checkBoxBloodGroupA.isChecked = false
        binding.checkBoxBloodGroupB.isChecked = false
        binding.checkBoxBloodGroupO.isChecked = false
        binding.checkBoxBloodGroupAB.isChecked = false
        binding.checkBoxBloodtype1.isChecked = false
        binding.checkBoxBloodtype2.isChecked = false
        //binding.imageViewProfilePicture.setImageDrawable(null)
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}