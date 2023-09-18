package com.assessment3.testone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.firebase.database.*

class gascharts : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var lineChart1: LineChart
    private lateinit var lineChart2: LineChart
    private lateinit var linechart3:LineChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gascharts)
         supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title="Gas Charts"
        // Initialize the Firebase Realtime Database reference
        database = FirebaseDatabase.getInstance().reference

        // Initialize the line charts
        lineChart1 = findViewById(R.id.lineChart1)
        lineChart2 = findViewById(R.id.lineChart2)
        linechart3=findViewById(R.id.lineChart3)

        // Fetch data from Firebase
        fetchDataFromFirebase()
    }

    private fun fetchDataFromFirebase() {
        val co2DataRef = database.child("CO2")
        val tolueneDataRef = database.child("Toluen")
        val NH4Ref = database.child("NH4")

        co2DataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Retrieve the CO2 data from the dataSnapshot
                val co2Value = dataSnapshot.getValue(Double::class.java)

                // Update the line chart for CO2 with the fetched data
                updateLineChart1(co2Value)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occurred during data retrieval
                // For example, you can log the error or show an error message
            }
        })

        tolueneDataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Retrieve the Toluene data from the dataSnapshot
                val tolueneValue = dataSnapshot.getValue(Double::class.java)

                // Update the line chart for Toluene with the fetched data
                updateLineChart2(tolueneValue)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occurred during data retrieval
                // For example, you can log the error or show an error message
            }
        })
        NH4Ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Retrieve the Toluene data from the dataSnapshot
                val NH4value = dataSnapshot.getValue(Double::class.java)

                // Update the line chart for Toluene with the fetched data
                 updateLineChart3(NH4value)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle any errors that occurred during data retrieval
                // For example, you can log the error or show an error message
            }
        })
    }

    private fun updateLineChart1(co2Value: Double?) {
        val lineData1 = lineChart1.data
        if (lineData1 != null) {
            val co2DataSet = lineData1.getDataSetByIndex(0) as LineDataSet?
            if (co2DataSet != null) {
                val entryCount = lineData1.entryCount.toFloat()
                val co2Entry = Entry(entryCount, co2Value?.toFloat() ?: 0f)
                co2DataSet.addEntry(co2Entry)
                lineData1.notifyDataChanged()
                lineChart1.notifyDataSetChanged()
                lineChart1.moveViewToX(entryCount)
            }
        } else {
            val co2DataSet = createLineDataSet(Entry(0f, co2Value?.toFloat() ?: 0f), "CO2")
            val newLineData1 = LineData(co2DataSet)
            lineChart1.data = newLineData1
        }


    }
    private fun updateLineChart2(TolueneValue: Double?) {
        val lineData2 = lineChart2.data
        if (lineData2 != null) {
            val TolueneDataSet = lineData2.getDataSetByIndex(0) as LineDataSet?
            if (TolueneDataSet != null) {
                val entryCount = lineData2.entryCount.toFloat()
                val TolueneEntry = Entry(entryCount, TolueneValue?.toFloat() ?: 0f)
                TolueneDataSet.addEntry(TolueneEntry)
                lineData2.notifyDataChanged()
                lineChart2.notifyDataSetChanged()
                lineChart2.moveViewToX(entryCount)
            }
        } else {
            val TolueneDataSet = createLineDataSet(Entry(0f, TolueneValue?.toFloat() ?: 0f), "Toluene")
            val newLineData2 = LineData(TolueneDataSet)
            lineChart2.data = newLineData2
        }


    }
    private fun updateLineChart3(NH4Value: Double?) {
        val lineData3 = linechart3.data
        if (lineData3 != null) {
            val NH4DataSet = lineData3.getDataSetByIndex(0) as LineDataSet?
            if (NH4DataSet != null) {
                val entryCount = lineData3.entryCount.toFloat()
                val NH4Entry = Entry(entryCount, NH4Value?.toFloat() ?: 0f)
                NH4DataSet.addEntry(NH4Entry)
                lineData3.notifyDataChanged()
                linechart3.notifyDataSetChanged()
                linechart3.moveViewToX(entryCount)
            }
        } else {
            val NH4DataSet = createLineDataSet(Entry(0f, NH4Value?.toFloat() ?: 0f), "NH4")
            val newLineData3 = LineData(NH4DataSet)
            linechart3.data = newLineData3
        }


    }






    private fun createLineDataSet(entry: Entry, label: String): LineDataSet {
        val dataSet = LineDataSet(mutableListOf(entry), label)
        dataSet.setDrawCircles(true)
        dataSet.setDrawValues(false)
        //dataSet.color = resources.getColor(R.color.chart_color)
        return dataSet
    }
}
