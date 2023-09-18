package com.assessment3.testone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import com.google.firebase.database.*

class gascomposition : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var co2value:TextView
    private lateinit var nh4value:TextView
    private lateinit var covalue:TextView
    private lateinit var toluene:TextView
    private lateinit var alertgas:Switch
    private lateinit var watchcharts:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gascomposition)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title="Gas composition"
        co2value=findViewById(R.id.co2)
        nh4value=findViewById(R.id.nh4)
        covalue=findViewById(R.id.co)
        toluene=findViewById(R.id.toluene)
        alertgas=findViewById(R.id.situationalert)
        watchcharts=findViewById(R.id.charts)

        val actionBar = supportActionBar
        //actionBar?.hide();

        databaseReference = FirebaseDatabase.getInstance().getReference()


        alertgas.setOnCheckedChangeListener { _, isChecked ->
            val gasalert = if (isChecked) 1 else 0
            databaseReference.child("FAN").setValue(gasalert)
        }
        watchcharts.setOnClickListener {
            val intent = Intent(this, gascharts::class.java)
            startActivity(intent)
        }

        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val co2val = dataSnapshot.child("CO2").getValue(Float::class.java)
                val coval = dataSnapshot.child("CO").getValue(Float::class.java)
                val nh4val = dataSnapshot.child("NH4").getValue(Float::class.java)
                val tolueneval = dataSnapshot.child("Toluen").getValue(Float::class.java)
                val hazardalert = dataSnapshot.child("FAN").getValue(Int::class.java)

                 co2value.text = "$co2val"
                 nh4value.text="$nh4val"
                 covalue.text="$coval"
                 toluene.text="$tolueneval"


                alertgas.isChecked = hazardalert == 1




            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })


    }
}