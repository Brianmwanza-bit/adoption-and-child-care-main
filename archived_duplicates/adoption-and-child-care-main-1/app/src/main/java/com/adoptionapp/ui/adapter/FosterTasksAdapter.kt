package com.adoptionapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adoptionapp.R
import com.adoptionapp.entity.FosterTasksEntity

class FosterTasksAdapter(
    private val onClick: (FosterTasksEntity) -> Unit
) : ListAdapter<FosterTasksEntity, FosterTasksAdapter.FosterTaskViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FosterTaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_foster_task, parent, false)
        return FosterTaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: FosterTaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FosterTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.taskTitleText)
        private val dueDateText: TextView = itemView.findViewById(R.id.taskDueDateText)
        private val statusText: TextView = itemView.findViewById(R.id.taskStatusText)
        fun bind(task: FosterTasksEntity) {
            titleText.text = task.title
            dueDateText.text = "Due: ${task.due_date ?: "-"}"
            statusText.text = "Status: ${task.status}"
            itemView.setOnClickListener { onClick(task) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<FosterTasksEntity>() {
        override fun areItemsTheSame(oldItem: FosterTasksEntity, newItem: FosterTasksEntity): Boolean =
            oldItem.task_id == newItem.task_id
        override fun areContentsTheSame(oldItem: FosterTasksEntity, newItem: FosterTasksEntity): Boolean =
            oldItem == newItem
    }
} 