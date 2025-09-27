package com.adoptionapp.ui.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adoptionapp.R
import com.adoptionapp.entity.ChildrenEntity

class ChildrenAdapter(
    private val onClick: (ChildrenEntity) -> Unit
) : ListAdapter<ChildrenEntity, ChildrenAdapter.ChildViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_child, parent, false)
        return ChildViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.childNameText)
        private val ageText: TextView = itemView.findViewById(R.id.childAgeText)
        private val photoImage: ImageView = itemView.findViewById(R.id.childPhotoImage)
        fun bind(child: ChildrenEntity) {
            nameText.text = child.name
            ageText.text = "Age: ${child.age}"
            if (child.photoBlob != null) {
                try {
                    val bmp = BitmapFactory.decodeByteArray(child.photoBlob, 0, child.photoBlob.size)
                    photoImage.setImageBitmap(bmp)
                } catch (_: Exception) {
                    photoImage.setImageResource(R.drawable.ic_person)
                }
            } else {
                photoImage.setImageResource(R.drawable.ic_person)
            }
            itemView.setOnClickListener { onClick(child) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ChildrenEntity>() {
        override fun areItemsTheSame(oldItem: ChildrenEntity, newItem: ChildrenEntity): Boolean =
            oldItem.child_id == newItem.child_id
        override fun areContentsTheSame(oldItem: ChildrenEntity, newItem: ChildrenEntity): Boolean =
            oldItem == newItem
    }
} 