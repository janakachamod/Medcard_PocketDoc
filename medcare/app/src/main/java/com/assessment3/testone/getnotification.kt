package com.assessment3.testone

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import com.assessment3.testone.databinding.ActivityGetnotificationBinding
import java.text.SimpleDateFormat
import java.util.*

class getnotification : AppCompatActivity() {
    private lateinit var binding: ActivityGetnotificationBinding
    private lateinit var picker: TimePicker
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var calendar: Calendar
    private lateinit var nameevent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetnotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title="Set Alerts"

        nameevent=findViewById(R.id.eventname)
        this.title = "Remainder"

        createNotificationChannel()

        binding.selectvalue.setOnClickListener {
            showDateTimePicker(this)
            //val intent = Intent(this, DateReceiver::class.java)

            //pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            // Set the alarm to trigger the broadcast at the selected date/time
            //val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            // alarmManager.setExact(AlarmManager.RTC_WAKEUP, binding.seleectedTime.text.toString(), pendingIntent)
        }
        binding.setvalue.setOnClickListener {

            // Create a pending intent that will start the Destinationview activity when the notification is clicked

        }
        binding.setcancel.setOnClickListener {
            cancelAlarm()
        }
    }

    private fun showDateTimePicker(activity: Activity) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val datePicker = DatePickerDialog(activity, { _, selectedYear, selectedMonth, selectedDay ->
            // Show the time picker after the user has selected the date
            val timePicker = TimePickerDialog(activity, { _, selectedHour, selectedMinute ->
                // Do something with the selected date and time
                val selectedDateTime = Calendar.getInstance()
                selectedDateTime.set(Calendar.YEAR, selectedYear)
                selectedDateTime.set(Calendar.MONTH, selectedMonth)
                selectedDateTime.set(Calendar.DAY_OF_MONTH, selectedDay)
                selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedDateTime.set(Calendar.MINUTE, selectedMinute)

                setAlarm(selectedDateTime)

                // You can format the selected date and time as a string using SimpleDateFormat
                val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                var formattedDateTime = dateTimeFormat.format(selectedDateTime.time)

                // Log the selected date and time for testing purposes
                Log.d("SelectedDateTime", formattedDateTime)
                binding.seleectedTime.text = formattedDateTime
            }, hour, minute, true)

            timePicker.show()
        }, year, month, day)

        datePicker.show()
    }

    private fun setAlarm(selectedDateTime: Calendar) {
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val time = dateTimeFormat.format(selectedDateTime.time)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, DateReceiver::class.java)
        intent.putExtra("name",nameevent.text.toString() )
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, selectedDateTime.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )

        Toast.makeText(this, "Alarm set", Toast.LENGTH_SHORT).show()
    }

    private fun cancelAlarm()
    {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, DateReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0)

        alarmManager.cancel(pendingIntent)

        Toast.makeText(this,"its canceled", Toast.LENGTH_SHORT).show()
    }


    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "reminder"
            val desccription = "get your reminder on your event"
            val impotance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("janaka", name, impotance)
            channel.description = desccription
            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)

        }
    }
    private fun createDestinationViewPendingIntent(context: Context, name: String, datetime: String): PendingIntent {
        val intent = Intent(context, Destination::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("name", nameevent.text)
            putExtra("datetime", binding.seleectedTime.text)
        }
        return PendingIntent.getActivity(context, 0, intent, 0)
    }

}