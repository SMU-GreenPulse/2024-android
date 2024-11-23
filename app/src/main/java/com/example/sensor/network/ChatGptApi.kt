package com.example.sensor.network

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// 요청에 필요한 데이터 클래스
data class ChatRequest(
    val model: String,
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: String
)

// 응답 데이터 클래스
data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

// Retrofit API 인터페이스 정의
interface ChatGptApi {
    @Headers(
        "Content-Type: application/json",
        "Authorization: Bearer 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"
    )
    @POST("v1/chat/completions")
    suspend fun getChatResponse(@Body request: ChatRequest): ChatResponse
}
