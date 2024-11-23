package com.example.sensor

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sensor.databinding.ActivityChatBinding
import com.example.sensor.ui.ChatAdapter
import com.example.sensor.viewmodel.ChatViewModel
import java.util.Locale

class ChatActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TextToSpeech 초기화
        textToSpeech = TextToSpeech(this, this)

        // RecyclerView 초기화
        chatAdapter = ChatAdapter(emptyList())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }

        // 메시지 전송 버튼 클릭 리스너
        binding.sendButton.setOnClickListener {
            val userMessage = binding.messageInput.text.toString()
            if (userMessage.isNotBlank()) {
                viewModel.sendMessage(userMessage)
                binding.messageInput.text.clear()
            }
        }

        // ViewModel의 LiveData를 관찰하여 RecyclerView 업데이트 및 TTS로 챗봇 응답 읽기
        viewModel.chatResponses.observe(this) { messages ->
            chatAdapter = ChatAdapter(messages)
            binding.recyclerView.adapter = chatAdapter
            binding.recyclerView.scrollToPosition(messages.size - 1) // 최신 메시지로 스크롤

            // 마지막 메시지가 ChatGPT 응답인지 확인 후 TTS로 읽기
            val lastMessage = messages.lastOrNull()
            if (lastMessage != null && !lastMessage.isUser) { // 챗봇 응답일 때만 읽음
                speak(lastMessage.message) // TTS 호출
            }
        }
    }

    // TextToSpeech 초기화 완료 시 호출
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.KOREAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "한국어 데이터가 없거나 지원되지 않습니다.")
                installVoiceData()
            }
        } else {
            Log.e("TTS", "TTS 초기화 실패")
        }
    }

    // 텍스트를 음성으로 읽는 함수
    private fun speak(text: String) {
        if (::textToSpeech.isInitialized) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    // TTS 데이터 설치 유도
    private fun installVoiceData() {
        val installIntent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
        startActivity(installIntent)
    }

    override fun onDestroy() {
        if (::textToSpeech.isInitialized) {
            textToSpeech.stop()
            textToSpeech.shutdown()
        }
        super.onDestroy()
    }
}
