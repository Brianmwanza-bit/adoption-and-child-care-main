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
import com.adoptionapp.DocumentsEntity
import com.adoptionapp.R

class DocumentsAdapter(
    private val onClick: (DocumentsEntity) -> Unit
) : ListAdapter<DocumentsEntity, DocumentsAdapter.DocumentViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_document, parent, false)
        return DocumentViewHolder(view)
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DocumentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val typeText: TextView = itemView.findViewById(R.id.documentTypeText)
        private val descriptionText: TextView = itemView.findViewById(R.id.documentDescriptionText)
        private val previewImage: ImageView = itemView.findViewById(R.id.documentPreviewImage)
        fun bind(document: DocumentsEntity) {
            typeText.text = document.document_type
            descriptionText.text = document.description
            if (document.fileBlob != null) {
                try {
                    val bmp = BitmapFactory.decodeByteArray(document.fileBlob, 0, document.fileBlob.size)
                    previewImage.setImageBitmap(bmp)
                } catch (_: Exception) {
                    previewImage.setImageResource(R.drawable.ic_file)
                }
            } else {
                previewImage.setImageResource(R.drawable.ic_file)
            }
            itemView.setOnClickListener { onClick(document) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DocumentsEntity>() {
        override fun areItemsTheSame(oldItem: DocumentsEntity, newItem: DocumentsEntity): Boolean =
            oldItem.document_id == newItem.document_id
        override fun areContentsTheSame(oldItem: DocumentsEntity, newItem: DocumentsEntity): Boolean =
            oldItem == newItem
    }
} 