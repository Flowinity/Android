package com.troplo.privateuploader.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.troplo.privateuploader.R
import com.troplo.privateuploader.api.Chat
import com.troplo.privateuploader.api.TpuFunctions

class ChatAdapter(private val items: List<Chat>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.chatName.text = TpuFunctions.getChatName(item)
        holder.chatDescription.text = item.users.size.toString() + " users"
        Glide
            .with(holder.chatImage)
            .load(TpuFunctions.image(item.icon, item.recipient))
            .centerCrop()
            .into(holder.chatImage)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatName: TextView = itemView.findViewById(R.id.chatName)
        val chatDescription: TextView = itemView.findViewById(R.id.chatDescription)
        val chatImage: ImageView = itemView.findViewById(R.id.avatarImage)
    }
}