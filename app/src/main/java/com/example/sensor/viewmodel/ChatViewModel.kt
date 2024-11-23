package com.example.sensor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sensor.network.ChatRequest
import com.example.sensor.network.Message
import com.example.sensor.network.RetrofitInstance.api
import com.example.sensor.ui.ChatMessage
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _chatResponses = MutableLiveData<List<ChatMessage>>()
    val chatResponses: LiveData<List<ChatMessage>> get() = _chatResponses

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val messages = mutableListOf<ChatMessage>()
    private val apiMessages = mutableListOf<Message>()

    fun sendMessage(userMessage: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // 사용자 메시지 추가
                messages.add(ChatMessage(userMessage, isUser = true))
                _chatResponses.value = messages.toList()

                // API 메시지 히스토리에 추가
                apiMessages.add(Message(
                    role = "user",
                    content = userMessage
                ))

                // ChatGPT 응답 받기
                val response = getChatGptResponse(userMessage)

                // 챗봇 응답 추가
                messages.add(ChatMessage(response, isUser = false))
                _chatResponses.value = messages.toList()

                // API 메시지 히스토리에 추가
                apiMessages.add(Message(
                    role = "assistant",
                    content = response
                ))

            } catch (e: Exception) {
                messages.add(ChatMessage("오류가 발생했습니다. 다시 시도해주세요.", isUser = false))
                _chatResponses.value = messages.toList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getChatGptResponse(message: String): String {
        return try {
            val request = ChatRequest(
                model = "gpt-3.5-turbo",
                messages = apiMessages.toList() // 전체 대화 히스토리 전송
            )

            val response = api.getChatResponse(
                authorization = "Bearer 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",
                request = request
            )

            response.choices.firstOrNull()?.message?.content
                ?: "죄송합니다. 응답을 생성하지 못했습니다."

        } catch (e: Exception) {
            throw e
        }
    }

    fun clearChat() {
        messages.clear()
        apiMessages.clear()
        _chatResponses.value = emptyList()
    }
}
