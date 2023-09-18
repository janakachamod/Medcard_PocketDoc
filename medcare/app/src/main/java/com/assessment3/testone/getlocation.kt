package com.assessment3.testone

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class getlocation : AppCompatActivity(),OnMapReadyCallback {
    private lateinit var database: FirebaseDatabase
    private lateinit var locationRef: DatabaseReference
    private lateinit var lattitude:TextView
    private lateinit var longitude1:TextView
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_getlocation)
        database = FirebaseDatabase.getInstance()
        locationRef = database.reference.child("location")
        lattitude=findViewById(R.id.textView)
        longitude1=findViewById(R.id.textView2)
        mapView = findViewById(R.id.mapView)
        // Initialize MapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        getLocationFromFirebase()
        val actionBar = supportActionBar
        actionBar?.hide();


    }
    private fun getLocationFromFirebase() {
        locationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val latitude = dataSnapshot.child("latitude").getValue(Double::class.java)
                val longitude = dataSnapshot.child("longitude").getValue(Double::class.java)

                if (latitude != null && longitude != null) {
                    lattitude.text = latitude.toString()
                    longitude1.text = longitude.toString()

                    // Add marker to the map
                    val location = LatLng(latitude, longitude)
                    googleMap.addMarker(MarkerOptions().position(location))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error if any
            }
        })
    }
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
    fun onNavigationButtonClick(view: View) {
        // Retrieve the latitude and longitude values from the TextViews
        val latitudeValue = lattitude.text.toString()
        val longitudeValue = longitude1.text.toString()

        // Launch the navigation intent with the coordinates
        val uri = Uri.parse("google.navigation:q=$latitudeValue,$longitudeValue")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }else {
            val webIntent = Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/place/$latitudeValue,$longitudeValue"))
            if (webIntent.resolveActivity(packageManager) != null) {
                startActivity(webIntent)
            } else {
                Toast.makeText(this, "No map application found", Toast.LENGTH_SHORT).show()
            }     }
    }


}