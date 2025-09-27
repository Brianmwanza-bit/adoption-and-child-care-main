package com.adoptionapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adoptionapp.R
import com.adoptionapp.entity.BackgroundChecksEntity

class BackgroundChecksAdapter(
    private val onClick: (BackgroundChecksEntity) -> Unit
) : ListAdapter<BackgroundChecksEntity, BackgroundChecksAdapter.BackgroundCheckViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackgroundCheckViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_background_check, parent, false)
        return BackgroundCheckViewHolder(view)
    }

    override fun onBindViewHolder(holder: BackgroundCheckViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BackgroundCheckViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val personText: TextView = itemView.findViewById(R.id.checkPersonNameText)
        private val typeText: TextView = itemView.findViewById(R.id.checkTypeText)
        private val statusText: TextView = itemView.findViewById(R.id.checkStatusText)
        private val resultText: TextView = itemView.findViewById(R.id.checkResultText)
        fun bind(check: BackgroundChecksEntity) {
            personText.text = check.person_name
            typeText.text = "Type: ${check.type}"
            statusText.text = "Status: ${check.status}"
            resultText.text = "Result: ${check.result ?: "-"}"
            itemView.setOnClickListener { onClick(check) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<BackgroundChecksEntity>() {
        override fun areItemsTheSame(oldItem: BackgroundChecksEntity, newItem: BackgroundChecksEntity): Boolean =
            oldItem.check_id == newItem.check_id
        override fun areContentsTheSame(oldItem: BackgroundChecksEntity, newItem: BackgroundChecksEntity): Boolean =
            oldItem == newItem
    }
} 