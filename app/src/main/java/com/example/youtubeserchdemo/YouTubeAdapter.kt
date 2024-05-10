package com.example.youtubeserchdemo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class YouTubeAdapter(private var items: List<Item> = listOf()) :
    RecyclerView.Adapter<YouTubeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var ImageThumbnail: ImageView = view.findViewById(R.id.image_thumbnail)
        val textView: TextView = view.findViewById(R.id.text_site_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item.snippet.title
        Glide.with(holder.itemView.context)
            .load(item.snippet.thumbnails.medium.url)
            .into(holder.ImageThumbnail)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailAcitivty::class.java).apply {
                putExtra("title", item.snippet.title)
                putExtra("thumbnailUrl", item.snippet.thumbnails.medium.url)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount() = items.size

    fun addItems(moreItems: List<Item>) {
        val startPosition = this.items.size
        this.items = this.items + moreItems
        notifyItemRangeInserted(startPosition, moreItems.size)
    }

    fun clearItem() {
        items = listOf()
        notifyDataSetChanged()
    }

}