package com.adoptionapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adoptionapp.R
import com.adoptionapp.entity.FamilyProfilesEntity

class FamilyProfilesAdapter(
    private val onClick: (FamilyProfilesEntity) -> Unit
) : ListAdapter<FamilyProfilesEntity, FamilyProfilesAdapter.FamilyProfileViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_family_profile, parent, false)
        return FamilyProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FamilyProfileViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FamilyProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.familyNameText)
        private val contactText: TextView = itemView.findViewById(R.id.familyContactText)
        private val addressText: TextView = itemView.findViewById(R.id.familyAddressText)
        fun bind(profile: FamilyProfilesEntity) {
            nameText.text = profile.name
            contactText.text = "Contact: ${profile.contact}"
            addressText.text = "Address: ${profile.address}"
            itemView.setOnClickListener { onClick(profile) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<FamilyProfilesEntity>() {
        override fun areItemsTheSame(oldItem: FamilyProfilesEntity, newItem: FamilyProfilesEntity): Boolean =
            oldItem.family_id == newItem.family_id
        override fun areContentsTheSame(oldItem: FamilyProfilesEntity, newItem: FamilyProfilesEntity): Boolean =
            oldItem == newItem
    }
} 