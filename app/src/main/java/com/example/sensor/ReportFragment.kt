package com.example.sensor

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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ReportFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrainingReportAdapter
    private val reportList = mutableListOf<TrainingReportDTO>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_report, container, false)

        // RecyclerView 설정
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 구분선 추가
        val dividerItemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(dividerItemDecoration)

        // 어댑터 설정
        adapter = TrainingReportAdapter(reportList)
        recyclerView.adapter = adapter

        // Firebase에서 데이터 로드
        loadReportsFromFirebase()

        return view
    }

    private fun loadReportsFromFirebase() {
        val database = Firebase.database.reference.child("detections")

        database.get().addOnSuccessListener { dataSnapshot ->
            val reportList = mutableListOf<TrainingReportDTO>()

            for (reportSnapshot in dataSnapshot.children) {
                val report = reportSnapshot.getValue(TrainingReportDTO::class.java)
                if (report != null) {
                    reportList.add(report)
                }
            }

            // RecyclerView 업데이트
            recyclerView.adapter = TrainingReportAdapter(reportList)
        }.addOnFailureListener { exception ->
            Log.e("ReportFragment", "Error loading data from Firebase: ${exception.message}")
        }
    }

}
