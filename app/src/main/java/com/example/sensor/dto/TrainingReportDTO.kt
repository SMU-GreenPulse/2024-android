package com.example.sensor.dto

data class TrainingReportDTO(
    val file_url: String? = null,     // 이미지 파일 URL
    val timestamp: String? = null,    // 타임스탬프
    val detections: Map<String, String>? = null // 탐지된 데이터 (키-값 쌍)
)
