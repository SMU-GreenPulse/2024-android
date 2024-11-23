package com.example.sensor

import android.media.MediaDataSource
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sensor.databinding.ActivityChatBinding
import com.example.sensor.network.OpenAiTtsService
import com.example.sensor.ui.ChatAdapter
import com.example.sensor.ui.ChatMessage
import com.example.sensor.viewmodel.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        setupSendButton()
        observeChatResponses()
    }

    private fun initRecyclerView() {
        chatAdapter = ChatAdapter(emptyList())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = chatAdapter
        }
    }

    private fun setupSendButton() {
        binding.sendButton.setOnClickListener {
            val userMessage = binding.messageInput.text.toString().trim()
            if (userMessage.isNotBlank()) {
                viewModel.sendMessage(userMessage)
                binding.messageInput.text.clear()
            }
        }
    }

    private fun observeChatResponses() {
        viewModel.chatResponses.observe(this) { messages ->
            updateChatAdapter(messages)
            playLastChatGptResponse(messages)
        }
    }

    private fun updateChatAdapter(messages: List<ChatMessage>) {
        chatAdapter = ChatAdapter(messages)
        binding.recyclerView.adapter = chatAdapter
        binding.recyclerView.scrollToPosition(messages.size - 1)
    }

    private fun playLastChatGptResponse(messages: List<ChatMessage>) {
        val lastMessage = messages.lastOrNull()
        if (lastMessage != null && !lastMessage.isUser) {
            playTtsWithOpenAi(lastMessage.message)
        }
    }

    private fun playTtsWithOpenAi(text: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val audioBytes = OpenAiTtsService.getTtsAudioBytes(text)
                withContext(Dispatchers.Main) {
                    playAudio(audioBytes)
                }
            } catch (e: Exception) {
                Log.e("TTS", "오디오 재생 실패: ${e.message}")
            }
        }
    }

    private fun playAudio(audioBytes: ByteArray) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(ByteArrayMediaDataSource(audioBytes))
            prepare()
            start()
        }
    }

    private class ByteArrayMediaDataSource(private val data: ByteArray) : MediaDataSource() {
        override fun close() {}
        override fun readAt(position: Long, buffer: ByteArray, offset: Int, size: Int): Int {
            val remaining = data.size - position.toInt()
            if (remaining <= 0) return -1
            val copyLength = minOf(remaining, size)
            System.arraycopy(data, position.toInt(), buffer, offset, copyLength)
            return copyLength
        }
        override fun getSize(): Long = data.size.toLong()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}