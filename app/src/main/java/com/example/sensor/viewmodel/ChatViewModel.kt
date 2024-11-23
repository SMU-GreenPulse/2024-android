package com.example.sensor.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sensor.ui.ChatMessage
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _chatResponses = MutableLiveData<List<ChatMessage>>()
    val chatResponses: LiveData<List<ChatMessage>> get() = _chatResponses

    private val messages = mutableListOf<ChatMessage>()

    fun sendMessage(userMessage: String) {
        // 사용자 메시지 추가
        messages.add(ChatMessage(userMessage, isUser = true))
        _chatResponses.value = messages.toList()

        // ChatGPT 응답 추가 (비동기 처리)
        viewModelScope.launch {
            val response = getChatGptResponse(userMessage)
            messages.add(ChatMessage(response, isUser = false))
            _chatResponses.value = messages.toList()
        }
    }

    private suspend fun getChatGptResponse(message: String): String {
        // 실제 ChatGPT API 호출 로직 (코루틴)
        return "ChatGPT Response to '$message'"
    }
}
