package com.adoptionapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adoptionapp.R
import com.adoptionapp.entity.PlacementsEntity

class PlacementsAdapter(
    private val onClick: (PlacementsEntity) -> Unit
) : ListAdapter<PlacementsEntity, PlacementsAdapter.PlacementViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacementViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_placement, parent, false)
        return PlacementViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlacementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlacementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val childIdText: TextView = itemView.findViewById(R.id.placementChildIdText)
        private val familyIdText: TextView = itemView.findViewById(R.id.placementFamilyIdText)
        private val statusText: TextView = itemView.findViewById(R.id.placementStatusText)
        fun bind(placement: PlacementsEntity) {
            childIdText.text = "Child ID: ${placement.child_id}"
            familyIdText.text = "Family ID: ${placement.family_id}"
            statusText.text = "Status: ${placement.status}"
            itemView.setOnClickListener { onClick(placement) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PlacementsEntity>() {
        override fun areItemsTheSame(oldItem: PlacementsEntity, newItem: PlacementsEntity): Boolean =
            oldItem.placement_id == newItem.placement_id
        override fun areContentsTheSame(oldItem: PlacementsEntity, newItem: PlacementsEntity): Boolean =
            oldItem == newItem
    }
} 