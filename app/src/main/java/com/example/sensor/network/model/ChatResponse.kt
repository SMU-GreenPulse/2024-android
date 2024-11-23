package com.example.sensor.network.model

// ChatGPT 응답 데이터 모델
data class ChatResponse(
    val choices: List<Choice>         // 응답 선택지 리스트
)

data class Choice(
    val message: Message              // 선택지에 포함된 메시지
)
