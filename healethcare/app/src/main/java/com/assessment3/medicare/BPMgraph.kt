package com.assessment3.medicare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.database.*

class BPMgraph : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var lineChart1: LineChart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmpgraph)
        database = FirebaseDatabase.getInstance().reference
        lineChart1 = findViewById(R.id.lineChart1)
        fetchDataFromFirebase()
    }

    private fun fetchDataFromFirebase() {
        val pulserate = database.child("BPM")


        pulserate.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Retrieve the CO2 data from the dataSnapshot
                val pulsevalue = dataSnapshot.getValue(Double::class.java)

                // Update the line chart for CO2 with the fetched data
                updateLineChart1(pulsevalue)
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
    private fun createLineDataSet(entry: Entry, label: String): LineDataSet {
        val dataSet = LineDataSet(mutableListOf(entry), label)
        dataSet.setDrawCircles(true)
        dataSet.setDrawValues(false)
        //dataSet.color = resources.getColor(R.color.chart_color)
        return dataSet
    }

}