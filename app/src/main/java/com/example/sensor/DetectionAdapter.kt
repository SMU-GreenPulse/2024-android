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
        // 0이 아닌 항목만 리스트에 추가
        val detectionList = mutableListOf<String>()
        if (item.healthy != 0) detectionList.add("Healthy: ${item.healthy}")
        if (item.powdery != 0) detectionList.add("Powdery: ${item.powdery}")
        if (item.fusarium != 0) detectionList.add("Fusarium: ${item.fusarium}")

        // 리스트를 쉼표로 구분된 문자열로 합치기
        holder.detectionText.text = detectionList.joinToString(", ")

        holder.timestampText.text = "촬영시간: ${item.timestamp}"

        // 이미지 로드 (Glide 사용)
        Glide.with(holder.imageView.context)
            .load(item.file_url)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = detectionList.size
}
