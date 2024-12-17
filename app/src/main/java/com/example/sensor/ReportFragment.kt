package com.example.sensor

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sensor.dto.TrainingReportDTO
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ReportFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var detectionAdapter: DetectionAdapter
    private val detectionList = mutableListOf<DetectionItem>()
    private val databaseReference = FirebaseDatabase.getInstance().getReference("sensor_data")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        detectionAdapter = DetectionAdapter(detectionList)
        recyclerView.adapter = detectionAdapter

        // Firebase 초기화
        database = FirebaseDatabase.getInstance().getReference("detections")
        fetchDetectionData()

        // 최근 7개의 토양 습도 데이터를 가져와서 그래프에 표시
        fetchRecentSoilMoistureData()
    }

    private fun fetchDetectionData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                detectionList.clear()
                for (data in snapshot.children) {
                    val detections = data.child("detections")
                    val healthy = detections.child("healthy").getValue(Int::class.java) ?: 0
                    val powdery = detections.child("powdery").getValue(Int::class.java) ?: 0
                    val fusarium = detections.child("fusarium").getValue(Int::class.java) ?: 0
                    val fileUrl = data.child("file_url").getValue(String::class.java) ?: ""
                    val timestamp = data.child("timestamp").getValue(String::class.java) ?: ""

                    detectionList.add(
                        DetectionItem(
                            healthy = healthy,
                            powdery = powdery,
                            fusarium= fusarium,
                            file_url = fileUrl,
                            timestamp = timestamp
                        )
                    )
                }
                detectionAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ReportFragment", "Database Error: ${error.message}")
            }
        })
    }

    private fun fetchRecentSoilMoistureData() {
        databaseReference.orderByChild("timestamp").limitToLast(7)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val entries = mutableListOf<Entry>()
                    var index = 0f

                    for (data in snapshot.children) {
                        val soilMoisture = data.child("soil_moisture").getValue(Double::class.java) ?: 0.0
                        entries.add(Entry(index, soilMoisture.toFloat()))
                        index++
                    }
                    updateLineChartWithHumidity(entries)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("GraphFragment", "Database Error: ${error.message}")
                }
            })
    }

    private fun updateLineChartWithHumidity(entries: List<Entry>) {
        val lineChart = requireView().findViewById<LineChart>(R.id.lineChart1)

        val mutableEntries = if (entries.isEmpty()) mutableListOf(Entry(0f, 0f)) else entries.toMutableList()

        val dataSet = LineDataSet(mutableEntries, "토양 습도").apply {
            color = Color.parseColor("#FFBF00")
            setCircleColor(Color.parseColor("#FFBF00"))
            circleRadius = 5f
            lineWidth = 3f
            mode = LineDataSet.Mode.LINEAR
        }

        // X축 설정
        lineChart.xAxis.apply {
            axisMinimum = 0f  // 최소값 0
            axisMaximum = 6f  // 최대값 6
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f  // 간격을 1로 설정
            setLabelCount(7, true)
            valueFormatter = IndexAxisValueFormatter(arrayOf("1", "2", "3", "4", "5", "6", "7"))
        }

        // Y축 설정
        lineChart.axisLeft.apply {
            axisMinimum = 30f  // Y축 최소값
            axisMaximum = 100f // Y축 최대값
            removeAllLimitLines()
        }

        // 오른쪽 Y축 숨기기
        lineChart.axisRight.isEnabled = false

        // 데이터 설정 및 그래프 갱신
        lineChart.data = LineData(dataSet)
        lineChart.invalidate()
    }
}