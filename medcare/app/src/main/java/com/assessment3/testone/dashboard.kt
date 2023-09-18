package com.assessment3.testone

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class dashboard : AppCompatActivity() , TextToSpeech.OnInitListener{

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var databaseReference: DatabaseReference
    private lateinit var currentdate: TextView
    private val Dashboard= dashboard::class.java
    private val Location= getlocation::class.java
    private val updateprofile = ProfileManage::class.java
    private val seeprofile =getprofiledata::class.java
    private val makecall =makeacall::class.java
    private val gascomposition =gascomposition::class.java
    private val setalert = getnotification::class.java
    private val setLocation=setlocation::class.java
    private lateinit var currenttime: TextView
    private lateinit var bloodtype: TextView
    private lateinit var BPM: TextView
    private lateinit var reply: Switch
    private lateinit var vehicle_state: Switch
    private lateinit var ventilation:Switch
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userId: String
    private lateinit var textspeech:TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        textspeech= TextToSpeech(this, this )
        supportActionBar!!.title="Dashboard"
        drawerLayout = findViewById(R.id.drawerLayout)
        val navView1: NavigationView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        currentdate = findViewById(R.id.txt_current_data)
        currenttime = findViewById(R.id.txt_current_time)
        reply = findViewById(R.id.reply_state)
        BPM = findViewById(R.id.bpmvalue)
        vehicle_state=findViewById(R.id.vehicle)
        ventilation=findViewById(R.id.ventilation)
        databaseReference = FirebaseDatabase.getInstance().getReference()

        supportActionBar?.apply {
            val colorDrawable = ColorDrawable(resources.getColor(R.color.white))
            setBackgroundDrawable(colorDrawable)
        }


        val auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""
         loadDataFromFirebase();
        currentdate = findViewById(R.id.txt_current_data)
        currenttime=findViewById(R.id.txt_current_time)

        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        val currentTime: TextView = findViewById(R.id.txt_current_time)

        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", currentHour, currentMinute)


        currenttime.text = formattedTime
        currentdate.text = formattedDate

        if (userId.isEmpty()) {
            finish()
            return
        }
        //loadDataFromFirebase()

        reply.setOnCheckedChangeListener { _, isChecked ->
            val reply = if (isChecked) 1 else 0
            databaseReference.child("relay").setValue(reply)

            if (reply == 1) {
                textspeech.speak("now, we are on the way...", TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
        vehicle_state.setOnCheckedChangeListener { _, isChecked ->
            val relay = if (isChecked) 1 else 0
            databaseReference.child("vehicle").setValue(relay)
        }
        ventilation.setOnCheckedChangeListener { _, isChecked ->
            val alert = if (isChecked) 1 else 0
            databaseReference.child("FAN").setValue(alert)
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
                R.id.makecall -> {
                    val intent =Intent(this,makecall)
                    startActivity(intent)
                    true
                }
                R.id.Set_location -> {
                    val intent =Intent(this,setLocation)
                    startActivity(intent)
                    true
                }
                R.id.Gas_composition -> {
                    val intent =Intent(this,gascomposition)
                    startActivity(intent)
                    true
                }
                R.id.SetAlert -> {
                    val intent =Intent(this,setalert)
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

                val pulseValue = dataSnapshot.child("BPM").getValue(Int::class.java)
                val replyback = dataSnapshot.child("relay").getValue(Int::class.java)
                val statevehicle = dataSnapshot.child("vehicle").getValue(Int::class.java)
                val ventilate = dataSnapshot.child("FAN").getValue(Int::class.java)

                 BPM.text = "$pulseValue"
                 reply.isChecked = replyback == 1
                 vehicle_state.isChecked = statevehicle == 1
                 ventilation.isChecked = ventilate == 1
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

            val intent = Intent(this, register::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loadDataFromFirebase() {

        val userRef1 = database.getReference("Users/Users").child(userId)


        userRef1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val userData = snapshot.value as? Map<*, *>


                if (userData != null && userData.containsKey("firstName") && userData.containsKey("address")
                    && userData.containsKey("tel") && userData.containsKey("bloodGroup")
                ) {
                    val bloodGroup = userData["bloodGroup"].toString()


                    if (userData.containsKey("profileImage")) {
                        val imageUrl = userData["profileImage"].toString()


                        val navView1: NavigationView = findViewById(R.id.nav_view)
                        val headerView = navView1.getHeaderView(0)
                        val imageViewProfile: ImageView = headerView.findViewById(R.id.profileimage)
                        Glide.with(this@dashboard)
                            .load(imageUrl)
                            .into(imageViewProfile)
                    }

                    val firstName = userData["firstName"].toString()
                    val navView1: NavigationView = findViewById(R.id.nav_view)
                    val headerView = navView1.getHeaderView(0)
                    val userNameTextView: TextView = headerView.findViewById(R.id.user_name)
                    userNameTextView.text = firstName
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textspeech.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This language is not supported")
            }
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }

    // Override the onDestroy method to release the TextToSpeech object
    override fun onDestroy() {
        super.onDestroy()

        textspeech.stop()
        textspeech.shutdown()
    }


}
