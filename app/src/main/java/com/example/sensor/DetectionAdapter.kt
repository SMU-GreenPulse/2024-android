package com.example.sensor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

data class DetectionItem(
    val healthy: Int,
    val powdery: Int,
    val fusarium: Int,
    val file_url: String,
    val timestamp: String
)

class DetectionAdapter(private val detectionList: List<DetectionItem>) :
    RecyclerView.Adapter<DetectionAdapter.DetectionViewHolder>() {

    class DetectionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.itemImage)
        val detectionText: TextView = view.findViewById(R.id.itemDetections)
        val timestampText: TextView = view.findViewById(R.id.itemTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return DetectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetectionViewHolder, position: Int) {
        val item = detectionList[position]

        // 상태를 판별하여 표시
        val status = when {
            item.healthy >= item.powdery && item.healthy >= item.fusarium -> "건강함"
            item.powdery >= item.fusarium -> "흰가루병"
            else -> "시들음병"
        }

        holder.detectionText.text = "현재상태: $status"

        holder.timestampText.text = "촬영시간: ${item.timestamp}"

        // 이미지 로드 (Glide 사용)
        Glide.with(holder.imageView.context)
            .load(item.file_url)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = detectionList.size
}