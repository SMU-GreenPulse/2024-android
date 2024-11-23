package com.example.sensor.network.model

// 사용자 메시지 요청 데이터 모델
data class ChatRequest(
    val model: String,                // 사용할 모델 이름 (예: "gpt-3.5-turbo")
    val messages: List<Message>       // 메시지 리스트
)

// 메시지 데이터 모델 (요청과 응답에서 공통으로 사용됨)
data class Message(
    val role: String,                 // 메시지 역할 (user/assistant/system)
    val content: String               // 메시지 내용
)
