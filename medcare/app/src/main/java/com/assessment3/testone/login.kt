package com.assessment3.testone

import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.assessment3.testone.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()

        binding.btnLogin.setOnClickListener{
            val email=binding.email.text.toString()
            val pass=binding.password.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty())
            {
                firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        val intent= Intent(this,dashboard::class.java)
                        startActivity(intent)

                    }else
                    {
                        Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Toast.makeText(this,"Empty set is not allowed", Toast.LENGTH_SHORT).show()
            }
        }
    }
}