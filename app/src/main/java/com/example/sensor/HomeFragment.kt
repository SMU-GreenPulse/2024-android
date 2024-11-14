package com.example.sensor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sensor.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    // View Binding 설정
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Firebase Database 참조 설정
    private val databaseReference = FirebaseDatabase.getInstance().getReference("sensorData")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // View Binding 초기화
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firebase 데이터 로드 함수 호출
        fetchSensorData()
    }

    private fun fetchSensorData() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Firebase에서 데이터를 받아와 TextView에 표시
                val humidity = snapshot.child("humidity").getValue(Double::class.java) ?: 0.0
                val pH = snapshot.child("ph").getValue(Double::class.java) ?: 0.0
                binding.humidityTextView.text = "습도: $humidity"
                binding.phTextView.text = "pH: $pH"
            }

            override fun onCancelled(error: DatabaseError) {
                // 오류 처리
                Log.e("HomeFragment", "Failed to read values", error.toException())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지
    }
}
