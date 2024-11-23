package com.example.sensor.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sensor.R

data class ChatMessage(
    val message: String,
    val isUser: Boolean // true: 사용자 메시지, false: ChatGPT 응답
)

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userMessage: TextView = view.findViewById(R.id.userMessage)
        val chatbotMessage: TextView = view.findViewById(R.id.chatbotMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]

        // 사용자 메시지와 ChatGPT 메시지를 구분해서 표시
        if (message.isUser) {
            holder.userMessage.text = message.message
            holder.userMessage.visibility = View.VISIBLE
            holder.chatbotMessage.visibility = View.GONE
        } else {
            holder.chatbotMessage.text = message.message
            holder.chatbotMessage.visibility = View.VISIBLE
            holder.userMessage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = messages.size
}
