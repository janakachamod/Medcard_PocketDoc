package com.assessment3.testone

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.assessment3.testone.databinding.ActivityRegisterBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class register : AppCompatActivity() {

    private lateinit var binding:ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googlesinginclient:GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()

        val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .requestId()
            .build()
        googlesinginclient = GoogleSignIn.getClient(this,gso)


        binding.textview.setOnClickListener{
            val intent=Intent(this,login::class.java)
            startActivity(intent)
        }

        binding.button4.setOnClickListener{
            val intent= Intent(this,ProfileManage::class.java)
            startActivity(intent)
        }
        binding.btnRegisterwithgoogle.setOnClickListener{
            signInWithGoogle()
        }

        binding.btnRegister.setOnClickListener{
            val email=binding.email.text.toString()
            val pass=binding.password.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty())
            {
                firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        val intent=Intent(this,login::class.java)
                        startActivity(intent)

                    }else
                    {
                        Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Toast.makeText(this,"Empty set is not allowed",Toast.LENGTH_SHORT).show()
            }
        }



    }
    private fun signInWithGoogle()
    {
       val signInIntent =googlesinginclient.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        result->
        if(result.resultCode ==Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
      if(task.isSuccessful)
      {
          val account:GoogleSignInAccount?=task.result
          if(account!=null){
              updateUI(account)
          }
      }
        else
      {
         Toast.makeText(this, "SignIn failed, try again",Toast.LENGTH_SHORT).show()
      }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credentials).addOnCompleteListener {
            if(it.isSuccessful){
                startActivity((Intent(this,dashboard::class.java)))
                finish()
            }
            else
            {
                Toast.makeText(this, "cant login currently.try after sometime",Toast.LENGTH_SHORT).show()
            }
        }

    }
}