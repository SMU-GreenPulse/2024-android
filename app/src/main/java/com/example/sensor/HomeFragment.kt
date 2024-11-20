package com.example.sensor

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.sensor.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class HomeFragment : Fragment() {

    // View Binding 설정
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Firebase Database와 Storage 참조 설정
    private val databaseReference = FirebaseDatabase.getInstance().getReference("sensorData")
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference.child("images")

    private val IMAGE_PICK_CODE = 1000
    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // View Binding 초기화
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firebase 데이터 로드 함수 호출
        fetchSensorData()

        // 저장된 이미지 불러오기
        loadSavedImage()

        // 이미지뷰 클릭 이벤트 (이미지 선택)
        binding.userImageView.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private fun fetchSensorData() {
        // Firebase Database에서 센서 데이터 로드
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val humidity = snapshot.child("humidity").getValue(Double::class.java) ?: 0.0
                val pH = snapshot.child("ph").getValue(Double::class.java) ?: 0.0
                binding.humidityTextView.text = "습도: $humidity"
                binding.phTextView.text = "pH: $pH"
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Failed to read values", error.toException())
            }
        })
    }

    private fun pickImageFromGallery() {
        // 갤러리에서 이미지 선택
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            uploadImageToFirebase(imageUri)
        }
    }

    private fun uploadImageToFirebase(uri: Uri?) {
        if (uri != null) {
            // 고유 파일 이름 생성
            val fileName = UUID.randomUUID().toString()
            val imageRef = storageReference.child(fileName)

            // Firebase Storage에 이미지 업로드
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    // 업로드 성공 후 다운로드 URL 가져오기
                    imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        saveImageUrlToDatabase(downloadUrl.toString())
                        Toast.makeText(requireContext(), "이미지 업로드 완료", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveImageUrlToDatabase(imageUrl: String) {
        // Firebase Database에 이미지 URL 저장
        databaseReference.child("userImage").setValue(imageUrl)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    loadSavedImage() // 저장 후 이미지를 다시 로드
                } else {
                    Toast.makeText(requireContext(), "이미지 저장 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loadSavedImage() {
        // Firebase Database에서 이미지 URL 가져와서 ImageView에 표시
        databaseReference.child("userImage").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val imageUrl = snapshot.getValue(String::class.java)
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.sample_pot) // 기본 이미지
                        .into(binding.userImageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Failed to load image URL", error.toException())
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 메모리 누수 방지
    }
}