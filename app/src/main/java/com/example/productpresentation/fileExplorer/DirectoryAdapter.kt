package com.example.productpresentation.fileExplorer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.example.productpresentation.R
import com.google.android.material.card.MaterialCardView

class DirectoryAdapter(private val fileList: List<FileItem>): RecyclerView.Adapter<DirectoryAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name_text_view)
        val itemCard: MaterialCardView = view.findViewById(R.id.item_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.file_or_dir_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = fileList[position]
        if(item.selected){
            holder.itemCard.background = getDrawable(holder.itemCard.context, R.drawable.item_background_selected)
        }
        else{
            holder.itemCard.background = getDrawable(holder.itemCard.context, R.drawable.item_background)
        }
        holder.name.text = item.name
    }

    override fun getItemCount(): Int = fileList.size
}