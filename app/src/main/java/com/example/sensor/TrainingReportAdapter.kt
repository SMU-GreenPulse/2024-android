package com.example.sensor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrainingReportAdapter(private val reports: List<TrainingReportDTO>) :
    RecyclerView.Adapter<TrainingReportAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.tvTrainingDate)
        val graphImageView: ImageView = view.findViewById(R.id.ivGraph)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = reports[position]
        holder.dateTextView.text = report.date
        holder.graphImageView.setImageResource(report.graphImageResId)
    }

    override fun getItemCount() = reports.size
}
