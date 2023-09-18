package com.assessment3.medicare

import android.content.Intent
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.assessment3.medicare.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()

        binding.textView4.setOnClickListener{
            val intent=Intent(this,register::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener{
            val email=binding.email.text.toString()
            val pass=binding.password.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty())
            {
                firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        val intent= Intent(this,MainActivity::class.java)
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