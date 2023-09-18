package com.assessment3.testone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googlesinginclient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize firebaseAuth and googlesinginclient
        firebaseAuth = FirebaseAuth.getInstance()
        googlesinginclient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)

        // Find the Log Out button by its ID
        val btnLogout: Button = findViewById(R.id.button)

        // Set an OnClickListener to handle log out when the button is clicked
        btnLogout.setOnClickListener {
            signOut()
        }

        // Your other code for MainActivity
    }

    private fun signOut() {
        // Firebase sign out
        firebaseAuth.signOut()

        // Google sign out
        googlesinginclient.signOut().addOnCompleteListener(this) {
            // After signing out, you can redirect the user to the login screen or any other desired activity.
            val intent = Intent(this, register::class.java)
            startActivity(intent)
            finish()
        }
    }
}






