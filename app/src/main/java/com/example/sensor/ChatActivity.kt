package com.example.sensor

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sensor.databinding.ActivityChatBinding
import com.example.sensor.ui.ChatAdapter
import com.example.sensor.viewmodel.ChatViewModel

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // ViewModel의 LiveData를 관찰하여 RecyclerView 업데이트
        viewModel.chatResponses.observe(this) { messages ->
            chatAdapter = ChatAdapter(messages)
            binding.recyclerView.adapter = chatAdapter
            binding.recyclerView.scrollToPosition(messages.size - 1) // 최신 메시지로 스크롤
        }
    }
}
