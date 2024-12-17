package com.example.sensor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ReportFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var detectionAdapter: DetectionAdapter
    private val detectionList = mutableListOf<DetectionItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
}
