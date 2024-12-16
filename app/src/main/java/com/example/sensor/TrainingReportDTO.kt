package com.example.sensor

data class TrainingReportDTO(
    var date: String = "",
    var image_url: String = "", // image_url 필드 추가
    var detections: Map<String, Int> = emptyMap() // Firebase의 detections 키와 매핑
)