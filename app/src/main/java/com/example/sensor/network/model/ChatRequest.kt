package com.example.sensor.network.model

data class ChatRequest(
    val model: String = "gpt-4o",    // 기본 모델값 설정
    val messages: List<Message>,             // 필수: 메시지 리스트
    val temperature: Double = 0.7,           // 응답의 무작위성 조절 (0.0 ~ 1.0)
    val maxTokens: Int = 1000,              // 응답 최대 토큰 수
    val topP: Double = 1.0,                 // 토큰 샘플링 확률
    val frequencyPenalty: Double = 0.0,     // 단어 반복 패널티 (-2.0 ~ 2.0)
    val presencePenalty: Double = 0.0,      // 주제 반복 패널티 (-2.0 ~ 2.0)
    val stream: Boolean = false             // 스트리밍 응답 여부
)

data class Message(
    val role: String,                       // user, assistant, system
    val content: String,                    // 메시지 내용
    val name: String? = null                // 선택적: 메시지 발신자 이름
) {
    companion object {
        const val ROLE_USER = "user"
        const val ROLE_ASSISTANT = "assistant"
        const val ROLE_SYSTEM = "system"
    }
}