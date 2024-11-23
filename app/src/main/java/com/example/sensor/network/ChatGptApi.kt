package com.example.sensor.network

import retrofit2.http.Body
import retrofit2.http.Header
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
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun getChatResponse(
        @Header("Authorization") authorization: String,
        @Body request: ChatRequest
    ): ChatResponse
}
