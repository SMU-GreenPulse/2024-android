package com.example.sensor

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
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
import java.math.BigDecimal

class GraphFragment : Fragment() {

    private val databaseReference = FirebaseDatabase.getInstance().getReference("sensor_data")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_graph, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 최근 7개의 토양 습도 데이터를 가져와서 그래프에 표시
        fetchRecentSoilMoistureData()
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