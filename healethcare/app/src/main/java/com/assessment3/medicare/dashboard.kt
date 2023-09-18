package com.assessment3.medicare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.assessment3.medicare.getlocation
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class dashboard : AppCompatActivity() {

        private lateinit var toggle: ActionBarDrawerToggle
        private lateinit var drawerLayout: DrawerLayout
        private lateinit var databaseReference: DatabaseReference
        private lateinit var currentdate: TextView
        private val Dashboard= dashboard::class.java
        private val Location= getlocation::class.java
        private val updateprofile = ProfileManage::class.java
        private val seeprofile =getprofiledata::class.java
        private lateinit var currenttime: TextView
        private lateinit var bloodtype: TextView
        private lateinit var bloodtype1:TextView
        private lateinit var availability: Switch
        private lateinit var response: Switch
        private lateinit var alert: Switch
        private lateinit var pulsegraph:Button
        private lateinit var firebaseAuth: FirebaseAuth
        private lateinit var database: FirebaseDatabase
        private lateinit var userId: String

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_dashboard)

            drawerLayout = findViewById(R.id.drawerLayout)
            val navView1: NavigationView = findViewById(R.id.nav_view)
            toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
            currentdate = findViewById(R.id.txt_current_data)
            currenttime = findViewById(R.id.txt_current_time)
            bloodtype = findViewById(R.id.txt_current_date_part1)
            availability = findViewById(R.id.availability)
            pulsegraph=findViewById(R.id.measure)
            response = findViewById(R.id.response)
            alert = findViewById(R.id.alert12)
            databaseReference = FirebaseDatabase.getInstance().getReference()

            val auth = FirebaseAuth.getInstance()
            database = FirebaseDatabase.getInstance()
            userId = auth.currentUser?.uid ?: ""

            currentdate = findViewById(R.id.txt_current_data)

            val calendar = Calendar.getInstance()
            val currentDate = calendar.time
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = dateFormat.format(currentDate)

            currentdate.text = formattedDate

            if (userId.isEmpty()) {
                finish()
                return
            }

            pulsegraph.setOnClickListener{
                val intent=Intent(this,BPMgraph::class.java)
                startActivity(intent)
            }
            //loadDataFromFirebase()


            availability.setOnCheckedChangeListener { _, isChecked ->
                val buzzer = if (isChecked) 1 else 0
                databaseReference.child("buzzer").setValue(buzzer)
            }
            response.setOnCheckedChangeListener { _, isChecked ->
                val relay = if (isChecked) 1 else 0
                databaseReference.child("relay").setValue(relay)
            }
            alert.setOnCheckedChangeListener { _, isChecked ->
                val alert = if (isChecked) 1 else 0
                databaseReference.child("alert").setValue(alert)
            }

            firebaseAuth = FirebaseAuth.getInstance()

            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setHomeButtonEnabled(true)
            }


            navView1.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.Dashboard -> {
                        val intent = Intent(this, Dashboard)
                        startActivity(intent)
                        true
                    }
                    R.id.Location -> {
                        val intent = Intent(this, Location)
                        startActivity(intent)
                        true
                    }

                    R.id.User_Details -> {
                        val intent =Intent(this,seeprofile)
                        startActivity(intent)
                        true
                    }
                    R.id.PrfileManage -> {
                        val intent =Intent(this,updateprofile)
                        startActivity(intent)
                        true
                    }
                    R.id.Logout -> {
                        signOut()
                        true
                    }
                    else -> false
                }
            }

            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val buzzer1 = dataSnapshot.child("buzzer").getValue(Int::class.java)
                    val relay1 = dataSnapshot.child("relay").getValue(Int::class.java)
                    val alert12 = dataSnapshot.child("alert").getValue(Int::class.java)

                    availability.isChecked = buzzer1 == 1
                    response.isChecked = relay1 == 1
                    alert.isChecked = alert12 == 1

                    if (alert12 == 1) {
                        val userRef1 = database.getReference("Users").child(userId)
                        userRef1.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val userData = snapshot.value as? Map<*, *>

                                if (userData != null && userData.containsKey("bloodGroup") && userData.containsKey("bloodType")) {
                                    val bloodGroup = userData["bloodGroup"].toString()
                                    val bloodType =userData["bloodType"].toString()
                                    bloodtype.text = bloodGroup
                                    bloodtype1.text=bloodType

                                    if (userData.containsKey("profileImage")) {
                                        val imageUrl = userData["profileImage"].toString()


                                        val navView1: NavigationView = findViewById(com.google.firebase.database.R.id.nav_view)
                                        val headerView = navView1.getHeaderView(0)
                                        val imageViewProfile: ImageView = headerView.findViewById(
                                            com.google.firebase.database.R.id.profileimage)
                                        Glide.with(this@dashboard)
                                            .load(imageUrl)
                                            .into(imageViewProfile)
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle database read error if necessary
                            }
                        })
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle database read error if necessary
                }
            })

        }

    private fun signOut() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut()

        // Google sign out
        val googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN)
        googleSignInClient.signOut().addOnCompleteListener(this) {
            val intent = Intent(this, register::class.java)
            startActivity(intent)
            finish()
        }
    }
        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            if (toggle.onOptionsItemSelected(item)) {
                return true
            }
            return super.onOptionsItemSelected(item)
        }

        override fun onStart() {
            super.onStart()
            val currentUser: FirebaseUser? = firebaseAuth.currentUser
            if (currentUser == null) {
                // User is not signed in, navigate to the register activity
                val intent = Intent(this, register::class.java)
                startActivity(intent)
                finish()
            }
        }

        private fun loadDataFromFirebase() {
            // Reference to the current user's data in the database
            val userRef1 = database.getReference("Users").child(userId)

            // Attach a ValueEventListener to listen for changes in the user's data
            userRef1.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Retrieve user data as a Map
                    val userData = snapshot.value as? Map<*, *>

                    // Check if userData is not null and contains the required fields
                    if (userData != null && userData.containsKey("firstName") && userData.containsKey("address")
                        && userData.containsKey("tel") && userData.containsKey("bloodGroup") && userData.containsKey("bloodType")
                    ) {
                        val bloodGroup = userData["bloodGroup"].toString()
                        val bloodType = userData["bloodType"].toString()
                        bloodtype.text = bloodGroup
                        bloodtype1.text=bloodType
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database read error if necessary
                }
            })
        }
    }
