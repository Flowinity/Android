package com.troplo.privateuploader.ui.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.troplo.privateuploader.R
import com.troplo.privateuploader.api.TpuFunctions
import com.troplo.privateuploader.data.model.Upload

class GalleryAdapter(private val items: List<Upload>) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.description.text = "Uploaded by: ${item.user.username}\nType: ${item.type}\nSize: ${item.fileSize}\nCreated: ${item.createdAt}"
        if(item.type == "image") {
            holder.image.visibility = View.VISIBLE
            Glide
                .with(holder.image)
                .load(TpuFunctions.image(item.attachment, null))
                .into(holder.image)
        } else {
            holder.image.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.galleryItemTitle)
        val description: TextView = itemView.findViewById(R.id.galleryItemDescription)
        val image: ImageView = itemView.findViewById(R.id.galleryItemImage)
    }
}