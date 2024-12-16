package com.example.sensor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sensor.dto.TrainingReportDTO

class TrainingReportAdapter(private val reports: List<TrainingReportDTO>) :
    RecyclerView.Adapter<TrainingReportAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.tvTrainingDate)
        val graphImageView: ImageView = view.findViewById(R.id.ivGraph)
        val detectTextView: TextView = view.findViewById(R.id.detect)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = reports[position]

        // Firebase Storage URL을 Glide를 통해 로드
        Glide.with(holder.graphImageView.context)
            .load(report.image_url) // Realtime Database에서 가져온 URL
            .placeholder(R.drawable.add_circle) // 로딩 중 이미지
            .error(R.drawable.error_image) // 에러 이미지
            .into(holder.graphImageView)

        // 탐지된 병충해 정보 표시
        val detectionsText = report.detections.entries.joinToString { "${it.key}: ${it.value}" }
        holder.detectTextView.text = detectionsText
    }

    override fun getItemCount() = reports.size
}
