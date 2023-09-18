package com.assessment3.testone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat


class makeacall : AppCompatActivity() {
    private lateinit var number: AutoCompleteTextView
    private lateinit var callButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_makeacall)

        number = findViewById(R.id.txt_tel)
        callButton = findViewById(R.id.button)

        val actionBar = supportActionBar
        actionBar?.hide();

        val suggestions = arrayOf("0760940226", "0342250224", "0766600150","0778875354","07558117823")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)
        number.setAdapter(adapter)

        checkPermissions()

        callButton.setOnClickListener {
            val phoneNumber = number.text.toString()

            if (phoneNumber.isNotEmpty()) {
                val callIntent = Intent(Intent.ACTION_CALL)
                callIntent.data = Uri.parse("tel:$phoneNumber")
                startActivity(callIntent)
            }
        }
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 101)
        }
    }
}