package com.rubenmackin.firestorage.ui.xml.list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rubenmackin.firestorage.databinding.ItemGalleryBinding

class GalleryAdapter(private val images: MutableList<String> = mutableListOf()) :
    RecyclerView.Adapter<GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GalleryViewHolder(binding)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.render(images[position])
    }

    fun updateList(list:List<String>){
        images.clear()
        images.addAll(list)
        notifyDataSetChanged()
    }
}

class GalleryViewHolder(private val binding: ItemGalleryBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun render(url: String) {
        Glide.with(binding.ivGalleryItem.context).load(url).into(binding.ivGalleryItem)
    }
}
