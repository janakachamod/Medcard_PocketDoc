package com.assessment3.testone

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.isLocationEnabled
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.FirebaseDatabase

class setlocation : AppCompatActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var latitude:TextView
    private lateinit var longitude:TextView
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setlocation)
        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
        latitude=findViewById(R.id.textView)
        longitude=findViewById(R.id.textView2)
        // Initialize Firebase database
        database = FirebaseDatabase.getInstance()

        getCurentLocation();
        supportActionBar?.apply {
            val colorDrawable = ColorDrawable(resources.getColor(R.color.white))
            setBackgroundDrawable(colorDrawable)
        }
    }

    private fun getCurentLocation()
    {
        if(checkPermission())
        {
            if(isLocationEnabled())
            {
                if(ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED )
                {
                    requestPermissions()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){
                        task->
                    val location: Location?=task.result
                    if(location==null)
                    {
                        Toast.makeText(this,"nothing to display",Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        Toast.makeText(this,"get successfully",Toast.LENGTH_SHORT).show()
                        latitude.text=""+location.latitude
                        longitude.text=""+location.longitude
                        val locationRef = database.reference.child("location")
                        locationRef.child("latitude").setValue(location.latitude)
                        locationRef.child("longitude").setValue(location.longitude)
                    }
                }
            }
            else
            {
                Toast.makeText(this,"Turn on Location",Toast.LENGTH_SHORT).show()
                val intent=Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else
        {
            requestPermissions()
        }
    }
    private fun isLocationEnabled():Boolean
    {
        val locationManager:LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)|| locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun requestPermissions(){
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission
                .ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }


    companion object{
        private const val PERMISSION_REQUEST_ACCESS_LOCATION=100
    }
    private fun checkPermission():Boolean
    {
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED )
        {
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode== PERMISSION_REQUEST_ACCESS_LOCATION){
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext,"Granted",Toast.LENGTH_SHORT).show()
                getCurentLocation()
            }
            else
            {
                Toast.makeText(applicationContext,"Denied",Toast.LENGTH_SHORT).show()
            }
        }
    }

}