package com.proyek.paba.feasthub.adapter

import android.graphics.Color
import com.proyek.paba.feasthub.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.proyek.paba.feasthub.api.home.ItemsArray
import com.squareup.picasso.Picasso

class HomeAdapter (
    private val listItem: ArrayList<ItemsArray>
): RecyclerView.Adapter<HomeAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(data: ItemsArray)
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val _item: ConstraintLayout = itemView.findViewById(R.id.item)
        val _thumbnailItem: ImageView = itemView.findViewById(R.id.thumbnailItem)
        val _tvJudulItem: TextView = itemView.findViewById(R.id.tvJudulItem)
        val _tvHargaItem: TextView = itemView.findViewById(R.id.tvHargaItem)
        val _tvPackItem: TextView = itemView.findViewById(R.id.tvPackItem)
        val _tvSatuanItem: TextView = itemView.findViewById(R.id.tvSatuanItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view : View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rv, parent, false)

        return this.ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val item = listItem[position]

        holder._item.setOnClickListener {
            onItemClickCallback.onItemClicked(item)
        }

        holder._tvJudulItem.text = item.nama
        holder._tvHargaItem.text = item.harga
        holder._tvPackItem.text = item.ukuran

        val satuanItem = "/ ${item.satuan}"
        holder._tvSatuanItem.text = satuanItem

        Picasso.get().load(item.thumbnail).into(holder._thumbnailItem)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
}